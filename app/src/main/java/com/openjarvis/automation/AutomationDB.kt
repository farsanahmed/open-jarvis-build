package com.openjarvis.automation

import android.content.Context
import androidx.room.*
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.openjarvis.agent.AgentCore

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val command: String,
    val scheduleType: String,
    val scheduleHour: Int = 0,
    val scheduleMinute: Int = 0,
    val scheduleDayOfWeek: Int = 0,
    val scheduleIntervalMs: Long = 0,
    val enabled: Boolean = true,
    val lastRun: Long? = null,
    val lastResult: String? = null,
    val runCount: Int = 0
)

/** Maps between the Room row and the domain model used by [AutomationManager]. */
fun AutomationEntity.toAutomation(): AutomationManager.Automation =
    AutomationManager.Automation(
        id = id,
        name = name,
        command = command,
        schedule = when (scheduleType) {
            "daily" -> AutomationManager.AutomationSchedule.Daily(scheduleHour, scheduleMinute)
            "weekly" -> AutomationManager.AutomationSchedule.Weekly(
                scheduleDayOfWeek, scheduleHour, scheduleMinute
            )
            "interval" -> AutomationManager.AutomationSchedule.Interval(scheduleIntervalMs)
            "once" -> AutomationManager.AutomationSchedule.Once(scheduleIntervalMs)
            else -> AutomationManager.AutomationSchedule.Daily(scheduleHour, scheduleMinute)
        },
        enabled = enabled,
        lastRun = lastRun,
        lastResult = lastResult,
        runCount = runCount
    )

fun AutomationManager.Automation.toEntity(): AutomationEntity =
    AutomationEntity(
        id = id,
        name = name,
        command = command,
        scheduleType = when (schedule) {
            is AutomationManager.AutomationSchedule.Daily -> "daily"
            is AutomationManager.AutomationSchedule.Weekly -> "weekly"
            is AutomationManager.AutomationSchedule.Interval -> "interval"
            is AutomationManager.AutomationSchedule.Once -> "once"
        },
        scheduleHour = (schedule as? AutomationManager.AutomationSchedule.Daily)?.hour ?: 0,
        scheduleMinute = (schedule as? AutomationManager.AutomationSchedule.Daily)?.minute ?: 0,
        scheduleDayOfWeek = (schedule as? AutomationManager.AutomationSchedule.Weekly)?.dayOfWeek ?: 0,
        scheduleIntervalMs = when (val s = schedule) {
            is AutomationManager.AutomationSchedule.Interval -> s.intervalMs
            is AutomationManager.AutomationSchedule.Once -> s.atMs
            else -> 0L
        },
        enabled = enabled,
        lastRun = lastRun,
        lastResult = lastResult,
        runCount = runCount
    )

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automations ORDER BY name")
    suspend fun getAll(): List<AutomationEntity>

    @Query("SELECT * FROM automations WHERE id = :id")
    suspend fun getById(id: String): AutomationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(automation: AutomationEntity)

    @Update
    suspend fun update(automation: AutomationEntity)

    @Query("DELETE FROM automations WHERE id = :id")
    suspend fun delete(id: String)
}

@Database(entities = [AutomationEntity::class], version = 1)
abstract class AutomationDB : RoomDatabase() {
    abstract fun automationDao(): AutomationDao

    companion object {
        private var instance: AutomationDB? = null

        fun getInstance(context: Context): AutomationDB =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AutomationDB::class.java,
                    "automations.db"
                ).build().also { instance = it }
            }
    }
}

/**
 * Runs one scheduled automation. WorkManager gives this worker the automation id;
 * the command is handed to the real agent so the schedule actually executes.
 */
class AutomationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString("automation_id") ?: return Result.failure()
        val fallbackCommand = inputData.getString("automation_command")
        val db = AutomationDB.getInstance(applicationContext)
        val entity = db.automationDao().getById(id)

        val command = entity?.command ?: fallbackCommand
        if (command.isNullOrBlank()) return Result.failure()

        return try {
            val agent = AgentCore(applicationContext)
            agent.executeTask(command)
            db.automationDao().getById(id)?.let {
                db.automationDao().update(
                    it.copy(
                        lastRun = System.currentTimeMillis(),
                        lastResult = "queued",
                        runCount = it.runCount + 1
                    )
                )
            }
            Result.success()
        } catch (e: Exception) {
            db.automationDao().getById(id)?.let {
                db.automationDao().update(
                    it.copy(lastRun = System.currentTimeMillis(), lastResult = e.message)
                )
            }
            Result.retry()
        }
    }
}
