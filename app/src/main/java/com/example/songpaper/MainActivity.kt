package com.example.songpaper

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.songpaper.ui.theme.SongPaperTheme
import com.example.songpaper.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SongPaperTheme {
                var showSettings by remember { mutableStateOf(false) }
                if (showSettings) {
                    SettingsScreen()
                } else {
                    OnboardingScreen(onProceed = { showSettings = true })
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(onProceed: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SongPaper needs Notification Access to detect currently playing music.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = {
            // Open system notification listener settings
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        }) {
            Text("Grant Notification Access")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onProceed) {
            Text("Open Settings")
        }
    }
}
