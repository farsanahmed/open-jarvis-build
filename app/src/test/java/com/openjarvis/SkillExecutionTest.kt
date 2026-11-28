package com.openjarvis

import com.openjarvis.skills.SkillEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider

/** Skills must load from the real assets and match a trigger phrase. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SkillExecutionTest {

    private fun engine(): SkillEngine =
        SkillEngine(ApplicationProvider.getApplicationContext())

    @Test
    fun builtinSkillsLoadFromAssets() {
        val engine = engine()
        kotlinx.coroutines.runBlocking { engine.ensureLoaded() }
        assertTrue(
            "expected the 7 built-in skills, got ${engine.getSkillCount()}",
            engine.getSkillCount() >= 7
        )
    }

    @Test
    fun triggerPhraseMatchesAndVariablesAreExtracted() {
        val engine = engine()
        kotlinx.coroutines.runBlocking { engine.ensureLoaded() }
        val command = "whatsapp Dad saying I will be late"
        val skill = engine.findMatch(command)
        assertNotNull("no skill matched: $command", skill)
        val vars = engine.extractVariables(command, skill!!)
        assertTrue("no variables extracted for ${skill.name}: $vars", vars.isNotEmpty())
        val context = engine.buildContext(skill, vars)
        assertTrue("skill context is empty", context.isNotBlank())
    }

    @Test
    fun unmatchedCommandReturnsNull() {
        val engine = engine()
        kotlinx.coroutines.runBlocking { engine.ensureLoaded() }
        assertEquals(null, engine.findMatch("zzz qqq nothing matches this"))
    }
}
