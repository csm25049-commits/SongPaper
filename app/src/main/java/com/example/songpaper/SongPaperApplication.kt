package com.example.songpaper

import android.app.Application
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.example.songpaper.repo.SettingsRepository
import com.example.songpaper.util.*
import com.example.songpaper.util.MediaTrackManager
import com.example.songpaper.model.TrackInfo
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltAndroidApp
class SongPaperApplication : Application() {

    @Inject lateinit var appScope: CoroutineScope // from AppModule
    @Inject lateinit var settingsRepo: SettingsRepository

    private val tag = "SongPaperApplication"
    private var restoreJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Application started – initializing observers")
        captureOriginalWallpapers()
        startTrackObserver()
        startAutoRestoreObserver()
    }

    /** Capture the current home/lock wallpapers once for later restoration. */
    private fun captureOriginalWallpapers() {
        val wm = android.app.WallpaperManager.getInstance(this)
        runCatching {
            OriginalWallpaperHolder.homeBitmap = wm.drawable?.toBitmap()
            // Some OEMs don't allow LOCK wallpaper retrieval – ignore failures
            OriginalWallpaperHolder.lockBitmap = try {
                wm.getDrawable(android.app.WallpaperManager.FLAG_LOCK)?.toBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Observe track flow, apply wallpaper respecting user preferences. */
    private fun startTrackObserver() {
        appScope.launch {
            combine(
                MediaTrackManager.track.filterNotNull(),
                settingsRepo.styleFlow,
                settingsRepo.blurFlow,
                settingsRepo.targetFlow,
                settingsRepo.whitelistFlow,
                settingsRepo.restoreOnPauseFlow,
                settingsRepo.serviceEnabledFlow
            ) { track, style, blur, target, whitelist, restoreOnPause, serviceEnabled ->
                Triple(track, style, blur) to Triple(target, whitelist, restoreOnPause) to serviceEnabled
            }
                .debounce(2000) // ignore rapid changes
                .distinctUntilChangedBy { (trackInfo, _, _) -> trackInfo?.albumArt?.hashCode() }
                .collect { (trackStyleBlur, targetWhitelistRestore, serviceEnabled) ->
                    if (!serviceEnabled) return@collect
                    val (trackInfo, style, blur) = trackStyleBlur
                    val (target, whitelist, _) = targetWhitelistRestore
                    // Whitelist check – if list empty, all are allowed
                    val allowed = whitelist.isEmpty() || whitelist.contains(trackInfo?.sourcePackage)
                    if (!allowed) {
                        Log.d(tag, "Skipping track from non‑whitelisted package ${trackInfo?.sourcePackage}")
                        return@collect
                    }
                    trackInfo?.albumArt?.let { bitmap ->
                        WallpaperHelper.applyWallpaper(
                            context = this@SongPaperApplication,
                            trackInfo = trackInfo,
                            style = style,
                            blurRadius = blur,
                            target = target
                        )
                    }
                }
        }
    }

    /** Restore original wallpaper after a pause timeout if the user enabled the option. */
    private fun startAutoRestoreObserver() {
        appScope.launch {
            combine(
                MediaTrackManager.isPlaying,
                settingsRepo.pauseTimeoutFlow,
                settingsRepo.targetFlow,
                settingsRepo.restoreOnPauseFlow,
                settingsRepo.serviceEnabledFlow
            ) { playing, timeoutSec, target, restoreOnPause, serviceEnabled ->
                Quintuple(playing, timeoutSec, target, restoreOnPause, serviceEnabled)
            }.collect { (isPlaying, timeoutSec, target, restoreOnPause, serviceEnabled) ->
                if (!serviceEnabled) return@collect
                if (isPlaying) {
                    // Cancel any pending restore when playback resumes
                    restoreJob?.cancel()
                    restoreJob = null
                } else if (restoreOnPause) {
                    // Schedule restore after the configured timeout
                    restoreJob?.cancel()
                    restoreJob = launch {
                        delay(timeoutSec * 1000L)
                        if (!MediaTrackManager.isPlaying.value) {
                            Log.d(tag, "Auto‑restoring original wallpaper after $timeoutSec s pause")
                            WallpaperRestorer.restoreOriginal(this@SongPaperApplication, target)
                        }
                    }
                }
            }
        }
    }
}

// Small helper tuple classes (Kotlin only provides Pair & Triple)
private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
