package com.openjarvis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openjarvis.crash.CrashReporter
import com.openjarvis.graphify.GraphifyRepository
import com.openjarvis.ui.theme.OpenJarvisTheme

class MainActivity : ComponentActivity() {

    // Nullable on purpose: a storage/database failure must not stop the launcher.
    private var graphifyRepo: GraphifyRepository? = null
    private var initError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CrashReporter.install(this)
        super.onCreate(savedInstanceState)

        graphifyRepo = try {
            GraphifyRepository(this)
        } catch (e: Exception) {
            initError = e.javaClass.simpleName + ": " + e.message
            null
        }

        setContent {
            OpenJarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        initError = initError,
                        onOpenAccessibility = {
                            startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onOpenSettings = {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        },
                        onOpenCrashReport = {
                            startActivity(Intent(this@MainActivity, CrashReportActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    initError: String?,
    onOpenAccessibility: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCrashReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Open Jarvis",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Voice and accessibility assistant. Grant the accessibility service in system settings to let Jarvis control the device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (initError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Storage unavailable",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = initError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Button(
            onClick = onOpenAccessibility,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Accessibility Settings")
        }

        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }

        OutlinedButton(
            onClick = onOpenCrashReport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Last crash")
        }
    }
}
