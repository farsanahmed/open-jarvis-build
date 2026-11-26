package com.openjarvis.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openjarvis.ui.theme.OpenJarvisTheme
import com.openjarvis.ui.theme.VoidColor

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenJarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = VoidColor.Violet
                        )

                        Divider(color = VoidColor.BorderSubtle)

                        Text("Providers", style = MaterialTheme.typography.titleMedium)

                        val providers = listOf(
                            "Groq" to "llama-3.1-70b-versatile",
                            "Google Gemini" to "gemini-1.5-flash",
                            "OpenRouter" to "meta-llama/llama-3-8b-instruct:free",
                            "Anthropic Claude" to "claude-haiku-4-20250514",
                            "OpenAI" to "gpt-4o-mini"
                        )

                        providers.forEach { (name, model) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(name, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        model,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VoidColor.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
