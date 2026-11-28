package com.openjarvis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.openjarvis.accessibility.JarvisAccessibilityService
import com.openjarvis.agent.AgentCore
import com.openjarvis.agent.AgentState
import com.openjarvis.crash.CrashReporter
import com.openjarvis.graphify.GraphifyRepository
import com.openjarvis.ui.theme.OpenJarvisTheme

class MainActivity : ComponentActivity() {

    // Nullable on purpose: a storage/database failure must not stop the launcher.
    private var graphifyRepo: GraphifyRepository? = null
    private var agentCore: AgentCore? = null
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
        agentCore = try {
            AgentCore(this)
        } catch (e: Exception) {
            initError = e.javaClass.simpleName + ": " + e.message
            null
        }

        val core = agentCore

        setContent {
            OpenJarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        initError = initError,
                        agentState = core?.state?.collectAsState()?.value ?: AgentState.Idle,
                        onCommand = { command -> core?.executeTask(command) },
                        onStop = { core?.stopTask() },
                        onOpenAccessibility = {
                            startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onOpenOverlay = {
                            startActivity(
                                Intent(
                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    android.net.Uri.parse("package:$packageName")
                                )
                            )
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

    override fun onDestroy() {
        agentCore?.stopTask()
        super.onDestroy()
    }
}

@Composable
private fun MainScreen(
    initError: String?,
    agentState: AgentState,
    onCommand: (String) -> Unit,
    onStop: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCrashReport: () -> Unit
) {
    var command by remember { mutableStateOf("") }
    val running = agentState is AgentState.Running

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Open Jarvis",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
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

        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Command") },
            placeholder = { Text("open whatsapp and message Dad") },
            minLines = 2,
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = {
                    val trimmed = command.trim()
                    if (trimmed.isNotEmpty() && !running) {
                        onCommand(trimmed)
                        command = ""
                    }
                }
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            enabled = !running
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Button(
                onClick = {
                    val trimmed = command.trim()
                    if (trimmed.isNotEmpty()) {
                        onCommand(trimmed)
                        command = ""
                    }
                },
                enabled = !running && command.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Run")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onStop,
                enabled = running,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop")
            }
        }

        StatusCard(agentState)

        Button(
            onClick = onOpenAccessibility,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Accessibility Settings")
        }

        OutlinedButton(
            onClick = onOpenOverlay,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Overlay Permission")
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

@Composable
private fun StatusCard(state: AgentState) {
    val label: String = when (state) {
        is AgentState.Idle -> "Idle"
        is AgentState.Running -> "Running: ${state.step}"
        is AgentState.Done -> "Done: ${state.result}"
        is AgentState.Error -> "Error: ${state.message}"
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
