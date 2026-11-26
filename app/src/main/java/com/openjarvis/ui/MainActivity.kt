package com.openjarvis.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.openjarvis.accessibility.JarvisAccessibilityService
import com.openjarvis.agent.AgentCore
import com.openjarvis.agent.AgentState
import com.openjarvis.graphify.GraphifyRepository
import com.openjarvis.graphify.nodes.TaskNode
import com.openjarvis.ui.dashboard.DashboardScreen
import com.openjarvis.ui.settings.SettingsScreen
import com.openjarvis.ui.theme.OpenJarvisTheme
import com.openjarvis.ui.theme.VoidColor

class MainActivity : ComponentActivity() {
    
    private lateinit var graphifyRepo: GraphifyRepository
    private lateinit var agentCore: AgentCore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        graphifyRepo = GraphifyRepository(this)
        agentCore = AgentCore(this)
        
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
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        val componentName = ComponentName(this, JarvisAccessibilityService::class.java)
        return enabledServices.contains(componentName.flattenToString())
    }
    
    private fun startAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
    
    private fun startOverlaySettings() {
        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, 
            android.net.Uri.parse("package:$packageName")))
    }
    
    private fun startOverlayService() {
        startService(Intent(this, OverlayService::class.java))
        Toast.makeText(this, "Jarvis is active", Toast.LENGTH_SHORT).show()
    }
    
    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}

@Composable
fun PermissionScreen(
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    onEnableAccessibility: () -> Unit,
    onEnableOverlay: () -> Unit
) {
    val primaryColor = VoidColor.Violet
    val onSurfaceColor = VoidColor.TextPrimary
    val onSurfaceVariantColor = VoidColor.TextSecondary
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            color = onSurfaceColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Open Jarvis needs accessibility and overlay permissions to control your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariantColor,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (!accessibilityEnabled) {
            Button(
                onClick = onEnableAccessibility,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Accessibility Service")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (!overlayEnabled) {
            Button(
                onClick = onEnableOverlay,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Overlay Permission")
            }
        }
        
        if (accessibilityEnabled && overlayEnabled) {
            Text(
                text = "Jarvis is active",
                style = MaterialTheme.typography.titleMedium,
                color = primaryColor
            )
        }
    }
}