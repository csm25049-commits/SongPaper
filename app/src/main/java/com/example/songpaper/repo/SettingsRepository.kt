package com.example.songpaper.repo

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.songpaper.util.WallpaperStyle
import com.example.songpaper.util.WallpaperTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "songpaper_prefs")

@Singleton
class SettingsRepository @Inject constructor(
    private val context: Context
) {
    // Preference keys
    private object Keys {
        val STYLE = stringPreferencesKey("style")
        val BLUR = floatPreferencesKey("blur")
        val TARGET = stringPreferencesKey("target")
        val WHITELIST = stringSetPreferencesKey("whitelist")
        val RESTORE_ON_PAUSE = booleanPreferencesKey("restore_on_pause")
        val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        val PAUSE_TIMEOUT = intPreferencesKey("pause_timeout_seconds")
    }

    // ----- Read -----
    val styleFlow: Flow<WallpaperStyle> = context.dataStore.data
        .map { prefs -> WallpaperStyle.valueOf(prefs[Keys.STYLE] ?: WallpaperStyle.BLURRED.name) }

    val blurFlow: Flow<Float> = context.dataStore.data
        .map { prefs -> prefs[Keys.BLUR] ?: 12f }

    val targetFlow: Flow<WallpaperTarget> = context.dataStore.data
        .map { prefs -> WallpaperTarget.valueOf(prefs[Keys.TARGET] ?: WallpaperTarget.BOTH.name) }

    val whitelistFlow: Flow<Set<String>> = context.dataStore.data
        .map { prefs -> prefs[Keys.WHITELIST] ?: emptySet() }

    val restoreOnPauseFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[Keys.RESTORE_ON_PAUSE] ?: true }

    val serviceEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[Keys.SERVICE_ENABLED] ?: true }

    val pauseTimeoutFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[Keys.PAUSE_TIMEOUT] ?: 30 }

    // ----- Write -----
    suspend fun setStyle(style: WallpaperStyle) {
        context.dataStore.edit { it[Keys.STYLE] = style.name }
    }

    suspend fun setBlur(radius: Float) {
        context.dataStore.edit { it[Keys.BLUR] = radius }
    }

    suspend fun setTarget(target: WallpaperTarget) {
        context.dataStore.edit { it[Keys.TARGET] = target.name }
    }

    suspend fun setWhitelist(packages: Set<String>) {
        context.dataStore.edit { it[Keys.WHITELIST] = packages }
    }

    suspend fun setRestoreOnPause(flag: Boolean) {
        context.dataStore.edit { it[Keys.RESTORE_ON_PAUSE] = flag }
    }

    suspend fun setServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SERVICE_ENABLED] = enabled }
    }

    suspend fun setPauseTimeout(seconds: Int) {
        context.dataStore.edit { it[Keys.PAUSE_TIMEOUT] = seconds }
    }
}
