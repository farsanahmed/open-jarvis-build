package com.openjarvis

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.openjarvis.ui.CrashReportActivity
import com.openjarvis.ui.MainActivity
import com.openjarvis.ui.SettingsActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Opens every screen the app exposes and asserts none of them crash.
 * Runs on the emulator jobs (API 28 and API 34).
 */
@RunWith(AndroidJUnit4::class)
class AllScreensTest {

    private val timeout = 10_000L

    private fun device(): UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private fun assertNoFatal() {
        val packages = InstrumentationRegistry.getInstrumentation()
            .targetContext.packageName
        val found = device().wait(
            Until.findObject(By.pkg(packages)),
            timeout
        )
        assertTrue("process for $packages is not alive", found != null)
    }

    @Test
    fun mainActivityOpens() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
            assertNoFatal()
        }
    }

    @Test
    fun settingsActivityOpens() {
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
            assertNoFatal()
        }
    }

    @Test
    fun crashReportActivityOpens() {
        ActivityScenario.launch(CrashReportActivity::class.java).use { scenario ->
            scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED)
            assertNoFatal()
        }
    }

    @Test
    fun launcherIntentResolvesToMainActivity() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(context.packageName)
        }
        val resolved = context.packageManager.resolveActivity(intent, 0)
        assertTrue("launcher intent did not resolve", resolved != null)
        assertEquals(
            "com.openjarvis.ui.MainActivity",
            resolved!!.activityInfo.name
        )
    }
}
