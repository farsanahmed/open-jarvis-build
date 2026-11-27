package com.openjarvis.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.openjarvis.R
import com.openjarvis.ui.settings.SettingsScreen
import com.openjarvis.ui.theme.OpenJarvisTheme

class SettingsActivity : ComponentActivity() {

    private lateinit var masterKey: MasterKey
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            this,
            "jarvis_encrypted_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setContent {
            OpenJarvisTheme {
                SettingsScreen(
                    onNavigateBack = { finish() },
                    onSaveProvider = { name, baseUrl, apiKey, model ->
                        saveProviderSettings(name, baseUrl, apiKey, model)
                    },
                    onTestConnection = { apiKey, baseUrl, model ->
                        testConnection(apiKey, baseUrl, model)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private fun saveProviderSettings(name: String, baseUrl: String, apiKey: String, model: String) {
        prefs.edit().apply {
            putString("provider_name", name)
            putString("provider_base_url", baseUrl)
            putString("provider_api_key", apiKey)
            putString("provider_model", model)
            apply()
        }
    }

    private fun testConnection(apiKey: String, baseUrl: String, model: String) {
        val adapter = UniversalAdapter(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val result = adapter.testConnection()
            when (result) {
                is Result.Success -> {
                    
                    val ms = result.getOrNull() ?: 0
                    this@SettingsActivity.runOnUiThread {
                        Toast.makeText(this, "Connected — ${ms}ms", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    val error = result.getOrThrow()
                    this@SettingsActivity.runOnUiThread {
                        Toast.makeText(this, "Connection failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}