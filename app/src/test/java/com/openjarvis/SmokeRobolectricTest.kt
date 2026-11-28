package com.openjarvis

import android.app.Service
import androidx.test.core.app.ApplicationProvider
import com.openjarvis.accessibility.JarvisAccessibilityService
import com.openjarvis.intelligence.JarvisNotificationListener
import com.openjarvis.ui.CrashReportActivity
import com.openjarvis.ui.MainActivity
import com.openjarvis.ui.OverlayService
import com.openjarvis.ui.SettingsActivity
import com.openjarvis.ui.onboarding.OnboardingActivity
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * JVM smoke tests. Robolectric is NOT a real device: it runs the real
 * framework and app classes on the JVM, so it catches startup crashes and
 * missing resources, but it cannot prove that taps, voice, OCR or MediaProjection
 * work on hardware.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SmokeRobolectricTest {

    private fun launchActivity(clazz: Class<out android.app.Activity>) {
        val controller = Robolectric.buildActivity(clazz)
        controller.create()
        controller.start()
        controller.resume()
        controller.visible()
        org.junit.Assert.assertNotNull("activity did not build: ${clazz.simpleName}", controller.get())
        controller.pause()
        controller.stop()
        controller.destroy()
    }

    @Test
    fun mainActivityStarts() {
        launchActivity(MainActivity::class.java)
    }

    @Test
    fun settingsActivityStarts() {
        launchActivity(SettingsActivity::class.java)
    }

    @Test
    fun onboardingActivityStarts() {
        launchActivity(OnboardingActivity::class.java)
    }

    @Test
    fun crashReportActivityStarts() {
        launchActivity(CrashReportActivity::class.java)
    }

    @Test
    fun accessibilityServiceCanBeCreated() {
        val service = Robolectric.buildService(JarvisAccessibilityService::class.java)
            .create()
            .get()
        assertNotNull("accessibility service was null", service)
        service.onDestroy()
    }

    @Test
    fun overlayServiceCanBeCreated() {
        val service = Robolectric.buildService(OverlayService::class.java)
            .create()
            .get()
        assertNotNull("overlay service was null", service)
        service.onDestroy()
    }

    @Test
    fun notificationListenerCanBeCreated() {
        val service = Robolectric.buildService(JarvisNotificationListener::class.java)
            .create()
            .get()
        assertNotNull("notification listener was null", service)
        service.onDestroy()
    }

    @Test
    fun targetPackageIsIntact() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        org.junit.Assert.assertEquals("com.openjarvis", context.packageName)
        val launcher = context.packageManager.getLaunchIntentForPackage("com.openjarvis")
        assertNotNull("no launcher intent for com.openjarvis", launcher)
    }
}
