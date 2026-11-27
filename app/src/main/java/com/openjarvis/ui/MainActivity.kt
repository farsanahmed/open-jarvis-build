package com.openjarvis.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.openjarvis.accessibility.JarvisAccessibilityService
import com.openjarvis.agent.AgentCore
import com.openjarvis.agent.AgentState
import com.openjarvis.graphify.GraphifyRepository
import com.openjarvis.graphify.nodes.TaskNode
import com.openjarvis.ui.dashboard.DashboardScreen
import com.openjarvis.ui.onboarding.OnboardingActivity
import com.openjarvis.ui.settings.SettingsScreen
import com.openjarvis.ui.theme.OpenJarvisTheme

class MainActivity : ComponentActivity() {

    private lateinit var graphifyRepo: GraphifyRepository
    private lateinit var agentCore: AgentCore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        graphifyRepo = GraphifyRepository(this)
        agentCore = AgentCore(this)

        // Check if onboarding is complete; if not, show onboarding first
        val onboardingComplete = getSharedPreferences("jarvis_prefs", MODE_PRIVATE)
            .getBoolean("onboarding_complete", false)
        if (!onboardingComplete) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContent {
            OpenJarvisTheme {
                val context = LocalContext.current
                var recentTasks by remember { mutableStateOf<List<TaskNode>>(emptyList()) }

                LaunchedEffect(Unit) {
                    recentTasks = graphifyRepo.getRecentTasks(10)
                }

                val accessibilityEnabled = isAccessibilityServiceEnabled()
                val overlayEnabled = Settings.canDrawOverlays(this)

                val agentState by agentCore.state.collectAsState()

                if (!accessibilityEnabled || !overlayEnabled) {
                    PermissionScreen(
                        accessibilityEnabled = accessibilityEnabled,
                        overlayEnabled = overlayEnabled,
                        onEnableAccessibility = { startAccessibilitySettings() },
                        onEnableOverlay = { startOverlaySettings() }
                    )
                } else {
                    DashboardScreen(
                        onStartOverlay = { startOverlayService() },
                        onOpenSettings = { openSettings() },
                        graphifyRepo = graphifyRepo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun testConnection(apiKey: String, baseUrl: String, model: String) {
        val adapter = UniversalAdapter(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val result = adapter.testConnection()
            when (result) {
                is Result.Success -> {
                    val ms = result.getOrNull() ?: 0
                    runOnUiThread {
                        Toast.makeText(this, "Connected — ${ms}ms", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    val error = result.getOrThrow()
                    runOnUiThread {
                        Toast.makeText(this, "Connection failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(