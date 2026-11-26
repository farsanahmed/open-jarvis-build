package com.openjarvis.ui.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openjarvis.llm.UniversalAdapter
import com.openjarvis.ui.theme.VoidColor

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onSaveProvider: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProvider by remember { mutableStateOf("Groq") }
    var baseUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("llama-3.1-70b-versatile") }
    var showBaseUrlField by remember { mutableStateOf(false) }
    var showProvidersDropdown by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var lastSaved by remember { mutableStateOf(0L) }
    var savedVisible by remember { mutableStateOf(false) }
    var voiceEnabled by remember { mutableStateOf(false) }
    var speakResults by remember { mutableStateOf(true) }
    var speechRate by remember { mutableStateOf(1.05f) }
    var sttMode by remember { mutableStateOf("Push to Talk") }

    LaunchedEffect(selectedProvider) {
        showBaseUrlField = selectedProvider == "Custom"
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidColor.Void950)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SettingsHeader(onNavigateBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                SectionLabel("AI PROVIDER")

                Spacer(modifier = Modifier.height(12.dp))

                ProviderSelectorCard(
                    selectedProvider = selectedProvider,
                    isExpanded = showProvidersDropdown,
                    onToggle = { showProvidersDropdown = !showProvidersDropdown },
                    onSelect = { provider ->
                        selectedProvider = provider
                        showProvidersDropdown = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingLabelTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = "Base URL",
                    isFocused = showBaseUrlField,
                    visible = showBaseUrlField
                )

                FloatingLabelTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = "API Key",
                    isFocused = false,
                    isPassword = !showPassword,
                    onTogglePassword = { showPassword = !showPassword }
                )

                FloatingLabelTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = "Model",
                    isFocused = false
                )

                if (selectedProvider == "Custom") {
                    FloatingLabelTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = "Custom URL",
                        isFocused = false,
                        visible = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProviderCard(
                    title = "Groq",
                    description = "Fast inference with Llama models",
                    isSelected = selectedProvider == "Groq",
                    onSelect = { selectedProvider = "Groq"; onSaveProvider("Groq", "https://api.groq.com/openai/v1", apiKey, model) }
                )

                ProviderCard(
                    title = "Google Gemini",
                    description = "Google's flash model",
                    isSelected = selectedProvider == "Google Gemini" || selectedProvider == "gemini",
                    onSelect = { selectedProvider = "Google Gemini"; onSaveProvider("Google Gemini", "https://generativelanguage.googleapis.com/v1beta", apiKey, model) }
                )

                ProviderCard(
                    title = "OpenRouter",
                    description = "Multiple models through one API",
                    isSelected = selectedProvider == "OpenRouter",
                    onSelect = { selectedProvider = "OpenRouter"; onSaveProvider("OpenRouter", "https://openrouter.ai/api/v1", apiKey, model) }
                )

                ProviderCard(
                    title = "Anthropic Claude",
                    description = "Safe and reliable Claude models",
                    isSelected = selectedProvider == "Anthropic Claude",
                    onSelect = { selectedProvider = "Anthropic Claude"; onSaveProvider("Anthropic Claude", "https://api.anthropic.com/v1", apiKey, model) }
                )

                ProviderCard(
                    title = "OpenAI",
                    description = "GPT-4 and GPT-3.5",
                    isSelected = selectedProvider == "OpenAI",
                    onSelect = { selectedProvider = "OpenAI"; onSaveProvider("OpenAI", "https://api.openai.com/v1", apiKey, model) }
                )

                ProviderCard(
                    title = "Ollama (Local)",
                    description = "Run LLMs locally",
                    isSelected = selectedProvider == "Ollama (Local)",
                    onSelect = { selectedProvider = "Ollama (Local)"; onSaveProvider("Ollama (Local)", "", apiKey, model) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                SavedToast(visible = savedVisible, onDismiss = { savedVisible = false })

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (apiKey.isNotBlank() && model.isNotBlank()) {
                            onSaveProvider(selectedProvider, baseUrl, apiKey, model)
                            savedVisible = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Provider", modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
        }
    }
}
