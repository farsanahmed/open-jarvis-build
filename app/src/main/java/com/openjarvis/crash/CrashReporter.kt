package com.openjarvis.crash

import android.content.Context
import android.content.Intent
import android.os.Build
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Writes uncaught stack traces to shared storage when possible, falling back to
 * the app files dir. The trace is shown by CrashReportActivity on next launch.
 */
object CrashReporter {

    const val PREFS = "jarvis_crash"
    const val KEY_TRACE = "last_crash"
    const val KEY_WHEN = "last_crash_when"

    private val externalFile: File
        get() = File(
            File("/storage/emulated/0/Download"),
            "open-jarvis-crash.txt"
        )

    private fun fallbackFile(context: Context): File =
        File(context.filesDir, "open-jarvis-crash.txt")

    fun write(context: Context, throwable: Throwable): File? {
        val trace = buildTrace(context, throwable)
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

        val target = runCatching {
            if (externalFile.parentFile?.exists() == true) {
                externalFile.writeText(trace)
                externalFile
            } else {
                null
            }
        }.getOrNull() ?: runCatching {
            fallbackFile(context).also { it.writeText(trace) }
        }.getOrNull()

        runCatching {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TRACE, trace)
                .putString(KEY_WHEN, stamp)
                .apply()
        }

        return target
    }

    fun lastTrace(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TRACE, null)

    fun lastWhen(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_WHEN, null)

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TRACE)
            .remove(KEY_WHEN)
            .apply()
        runCatching { fallbackFile(context).delete() }
        runCatching { externalFile.delete() }
    }

    private fun buildTrace(context: Context, throwable: Throwable): String {
        val sw = java.io.StringWriter()
        throwable.printStackTrace(java.io.PrintWriter(sw))
        return buildString {
            append("Open Jarvis crash\n")
            append("time: ").append(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            ).append('\n')
            append("device: ").append(Build.MANUFACTURER).append(' ')
                .append(Build.MODEL).append(" (Android ").append(Build.VERSION.RELEASE)
                .append(", API ").append(Build.VERSION.SDK_INT).append(")\n")
            append("package: ").append(context.packageName).append('\n')
            append('\n').append(sw.toString())
        }
    }

    /** Installs the process-wide handler. Safe to call more than once. */
    fun install(context: Context) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { write(context, throwable) }
            previous?.uncaughtException(thread, throwable)
        }
    }

    fun shareIntent(): Intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Open Jarvis crash report")
    }
}
