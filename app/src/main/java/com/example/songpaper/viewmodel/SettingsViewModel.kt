package com.example.songpaper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songpaper.repo.SettingsRepository
import com.example.songpaper.util.WallpaperStyle
import com.example.songpaper.util.WallpaperTarget
import com.example.songpaper.util.OriginalWallpaperHolder
import com.example.songpaper.util.WallpaperRestorer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    // Expose flows as StateFlows for UI collection
    val style: StateFlow<WallpaperStyle> = settingsRepo.styleFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, WallpaperStyle.BLURRED
    )
    val blur: StateFlow<Float> = settingsRepo.blurFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 12f
    )
    val target: StateFlow<WallpaperTarget> = settingsRepo.targetFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, WallpaperTarget.BOTH
    )
    val whitelist: StateFlow<Set<String>> = settingsRepo.whitelistFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptySet()
    )
    val serviceEnabled: StateFlow<Boolean> = settingsRepo.serviceEnabledFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )
    val restoreOnPause: StateFlow<Boolean> = settingsRepo.restoreOnPauseFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, true
    )
    val pauseTimeout: StateFlow<Int> = settingsRepo.pauseTimeoutFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 30
    )

    // ------- write helpers -------
    fun setStyle(style: WallpaperStyle) = viewModelScope.launch { settingsRepo.setStyle(style) }
    fun setBlur(blur: Float) = viewModelScope.launch { settingsRepo.setBlur(blur) }
    fun setTarget(target: WallpaperTarget) = viewModelScope.launch { settingsRepo.setTarget(target) }
    fun setWhitelist(list: Set<String>) = viewModelScope.launch { settingsRepo.setWhitelist(list) }
    fun setServiceEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepo.setServiceEnabled(enabled) }
    fun setRestoreOnPause(flag: Boolean) = viewModelScope.launch { settingsRepo.setRestoreOnPause(flag) }
    fun setPauseTimeout(seconds: Int) = viewModelScope.launch { settingsRepo.setPauseTimeout(seconds) }

    // Restore original wallpaper (user‑initiated)
    fun restoreOriginalWallpaper() = viewModelScope.launch {
        WallpaperRestorer.restoreOriginal(
            context = com.example.songpaper.SongPaperApplication().applicationContext,
            target = target.value
        )
    }
}
