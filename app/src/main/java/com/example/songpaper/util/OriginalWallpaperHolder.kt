package com.example.songpaper.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import com.example.songpaper.util.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Holds the original home/lock screen bitmaps captured at app start.
 * Stored in memory for the app lifetime – enough for the restore feature.
 */
object OriginalWallpaperHolder {
    var homeBitmap: Bitmap? = null
    var lockBitmap: Bitmap? = null
}

/**
 * Extension on WallpaperManager to clear or restore original bitmaps.
 */
object WallpaperRestorer {
    suspend fun restoreOriginal(context: Context, target: WallpaperTarget) {
        val wm = WallpaperManager.getInstance(context)
        withContext(Dispatchers.Default) {
            when (target) {
                WallpaperTarget.HOME -> {
                    OriginalWallpaperHolder.homeBitmap?.let { wm.setBitmap(it, null, true, WallpaperManager.FLAG_SYSTEM) }
                }
                WallpaperTarget.LOCK -> {
                    OriginalWallpaperHolder.lockBitmap?.let { wm.setBitmap(it, null, true, WallpaperManager.FLAG_LOCK) }
                }
                WallpaperTarget.BOTH -> {
                    OriginalWallpaperHolder.homeBitmap?.let { wm.setBitmap(it, null, true, WallpaperManager.FLAG_SYSTEM) }
                    OriginalWallpaperHolder.lockBitmap?.let { wm.setBitmap(it, null, true, WallpaperManager.FLAG_LOCK) }
                }
            }
        }
    }
}
