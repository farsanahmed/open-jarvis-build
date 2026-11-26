package com.openjarvis.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
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
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = VoidColor.Violet
                        )

                        Divider(color = VoidColor.BorderSubtle)

                        Text("Providers", style = MaterialTheme.typography.titleMedium)

                        ProvidersList(onSelect = { name, baseUrl, apiKey, model ->
                            startActivity(Intent(this, OnboardingActivity::class.java))
                        })

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { finish() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }
                    }
                )
            }
        }
    }
}
