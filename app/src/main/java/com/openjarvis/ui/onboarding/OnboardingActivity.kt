package com.openjarvis.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openjarvis.ui.theme.VoidColor

class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isOnboardingComplete()) {
            finish()
            return
        }

        setContent { OnboardingScreen(onComplete = { finish() }) }
    }

    private fun isOnboardingComplete(): Boolean {
        val prefs = getSharedPreferences("jarvis_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboarding_complete", false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var currentScreen by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        containerColor = VoidColor.Void950
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            when (currentScreen) {
                0 -> WelcomeScreen(onGetStarted = { currentScreen++ })
                1 -> ProviderScreen(
                    onBack = { currentScreen-- },
                    onContinue = { currentScreen++ },
                    onSkip = { currentScreen = 3 }
                )
                2 -> StTTScreen(
                    onBack = { currentScreen-- },
                    onContinue = { currentScreen++ }
                )
                3 -> ReadyScreen(onStart = { completeOnboarding(context); onComplete() })
            }

            if (currentScreen in 1..3) {
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentScreen > 0 && currentScreen < 4) {
                        TextButton(onClick = { currentScreen-- }) {
                            Text("Back", color = VoidColor.TextDisabled)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (currentScreen < 4) {
                        TextButton(onClick = { currentScreen++ }) {
                            Text("Skip", color = VoidColor.TextDisabled)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = androidx.compose.R.drawable.ic_launcher_foreground),
            contentDescription = "Open Jarvis",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Open Jarvis",
            style = MaterialTheme.typography.headlineMedium,
            color = VoidColor.Violet
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your personal AI assistant for device control",
            style = MaterialTheme.typography.bodyMedium,
            color = VoidColor.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }
}

@Composable
fun ProviderScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    var selectedProvider by remember { mutableStateOf("Groq") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Provider",
            style = MaterialTheme.typography.headlineSmall,
            color = VoidColor.Violet
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Groq", style = MaterialTheme.typography.bodyLarge, color = if (selectedProvider == "Groq") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Fast inference with Llama models", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Google Gemini", style = MaterialTheme.typography.bodyLarge, color = if (selectedProvider == "Google Gemini" || selectedProvider == "gemini") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Google's flash model", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("OpenRouter", style = MaterialTheme.typography.bodyLarge, color = if (selectedProvider == "OpenRouter") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Multiple models through one API", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Anthropic Claude", style = MaterialTheme.typography.bodyLarge, color = if (selectedProvider == "Anthropic Claude") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Safe and reliable Claude models", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("OpenAI", style = MaterialTheme.typography.bodyLarge, color = if (selectedProvider == "OpenAI") VoidColor.Violet else VoidColor.TextSecondary)
                Text("GPT-4 and GPT-3.5", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("Back", color = VoidColor.TextDisabled)
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onSkip) {
                Text("Skip", color = VoidColor.TextDisabled)
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onContinue) {
                Text("Continue", color = VoidColor.Violet)
            }
        }
    }
}

@Composable
fun StTTScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Text-to-Speech Setup",
            style = MaterialTheme.typography.headlineSmall,
            color = VoidColor.Violet
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Configure speech recognition and synthesis settings",
            style = MaterialTheme.typography.bodyMedium,
            color = VoidColor.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Push to Talk", style = MaterialTheme.typography.bodyLarge, color = if (sttMode == "Push to Talk") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Brief press to speak", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = VoidColor.Void900)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Continuous Listening", style = MaterialTheme.typography.bodyLarge, color = if (sttMode == "Continuous") VoidColor.Violet else VoidColor.TextSecondary)
                Text("Always listening for commands", style = MaterialTheme.typography.bodySmall, color = VoidColor.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("Back", color = VoidColor.TextDisabled)
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onContinue) {
                Text("Continue", color = VoidColor.Violet)
            }
        }
    }
}

@Composable
fun ReadyScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = androidx.compose.R.drawable.ic_launcher_foreground),
            contentDescription = "Open Jarvis",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Ready to go!",
            style = MaterialTheme.typography.headlineMedium,
            color = VoidColor.Violet
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Open Jarvis is ready for use",
            style = MaterialTheme.typography.bodyMedium,
            color = VoidColor.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Using Jarvis")
        }
    }
}

private fun completeOnboarding(context: Context) {
    val prefs = context.getSharedPreferences("jarvis_prefs", Context.MODE_PRIVATE).edit()
    prefs.putBoolean("onboarding_complete", true)
    prefs.apply()
}
