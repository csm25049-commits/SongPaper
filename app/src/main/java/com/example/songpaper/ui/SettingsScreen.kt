package com.example.songpaper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.songpaper.util.WallpaperStyle
import com.example.songpaper.util.WallpaperTarget
import com.example.songpaper.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val style by viewModel.style.collectAsState(initial = WallpaperStyle.BLURRED)
    val blur by viewModel.blur.collectAsState(initial = 12f)
    val target by viewModel.target.collectAsState(initial = WallpaperTarget.BOTH)
    val whitelist by viewModel.whitelist.collectAsState(initial = emptySet<String>())
    val serviceEnabled by viewModel.serviceEnabled.collectAsState(initial = true)
    val restoreOnPause by viewModel.restoreOnPause.collectAsState(initial = true)
    val pauseTimeout by viewModel.pauseTimeout.collectAsState(initial = 30)

    var whitelistInput by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("SongPaper Settings", style = MaterialTheme.typography.titleLarge)

        // Service enable toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Service")
            Switch(checked = serviceEnabled, onCheckedChange = { viewModel.setServiceEnabled(it) })
        }

        // Style picker
        Text("Wallpaper Style")
        WallpaperStyle.values().forEach { s ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = style == s, onClick = { viewModel.setStyle(s) })
                Text(s.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() })
            }
        }

        // Blur slider (only visible for BLURRED style)
        if (style == WallpaperStyle.BLURRED) {
            Text("Blur intensity (${blur.toInt()}dp)")
            Slider(value = blur, onValueChange = { viewModel.setBlur(it) }, valueRange = 0f..30f)
        }

        // Target selector
        Text("Target")
        WallpaperTarget.values().forEach { t ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = target == t, onClick = { viewModel.setTarget(t) })
                Text(t.name.lowercase().replaceFirstChar { it.titlecase() })
            }
        }

        // Whitelist input (comma‑separated package names)
        OutlinedTextField(
            value = whitelistInput,
            onValueChange = { whitelistInput = it },
            label = { Text("Whitelist (comma‑separated package names)") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            val list = whitelistInput.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()
            viewModel.setWhitelist(list)
        }) {
            Text("Save Whitelist")
        }

        // Restore on pause toggle & timeout slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Restore original on pause")
            Switch(checked = restoreOnPause, onCheckedChange = { viewModel.setRestoreOnPause(it) })
        }
        if (restoreOnPause) {
            Text("Pause timeout (${pauseTimeout}s)")
            Slider(value = pauseTimeout.toFloat(), onValueChange = { viewModel.setPauseTimeout(it.toInt()) }, valueRange = 5f..120f)
        }

        // Button to manually restore original wallpaper
        Button(onClick = { viewModel.restoreOriginalWallpaper() }) {
            Text("Restore Original Wallpaper")
        }
    }
}
