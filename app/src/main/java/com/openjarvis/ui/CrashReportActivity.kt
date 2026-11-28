package com.openjarvis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.openjarvis.crash.CrashReporter
import com.openjarvis.ui.theme.OpenJarvisTheme

/** Shows the last uncaught exception with a Share button. */
class CrashReportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trace = CrashReporter.lastTrace(this)
        val stamp = CrashReporter.lastWhen(this)

        setContent {
            OpenJarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CrashScreen(
                        trace = trace,
                        stamp = stamp,
                        onShare = { share(trace) },
                        onClear = {
                            CrashReporter.clear(this)
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun share(trace: String?) {
        val send = CrashReporter.shareIntent()
        send.putExtra(Intent.EXTRA_TEXT, trace ?: "No crash recorded.")
        startActivity(Intent.createChooser(send, "Share crash report"))
    }
}

@Composable
private fun CrashScreen(
    trace: String?,
    stamp: String?,
    onShare: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Last crash",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (trace == null) {
            Text(
                text = "No crash has been recorded on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = stamp ?: "unknown time",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = trace,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share")
            }
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear")
            }
        }
    }
}
