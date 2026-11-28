package com.openjarvis

import com.openjarvis.llm.UniversalAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/** Plain JVM checks that need no Android runtime. */
class UnitSanityTest {

    @Test
    fun defaultModelForEveryAdvertisedProviderIsNonBlank() {
        val providers = UniversalAdapter.AVAILABLE_PROVIDERS
        assertEquals(7, providers.size)
        for (p in providers) {
            val model = UniversalAdapter.getDefaultModel(p)
            assertNotNull("model for $p", model)
            assertEquals("model for $p must not be blank", false, model.isBlank())
        }
    }

    @Test
    fun groqDefaultModelMatchesReadme() {
        assertEquals("llama-3.1-70b-versatile", UniversalAdapter.getDefaultModel("Groq"))
    }
}
