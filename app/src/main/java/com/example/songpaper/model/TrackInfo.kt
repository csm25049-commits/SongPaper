package com.example.songpaper.model

import android.graphics.Bitmap

/**
 * Data holder for the currently playing track.
 * Added `sourcePackage` for whitelist checks.
 */
data class TrackInfo(
    val title: String?,
    val artist: String?,
    val albumArt: Bitmap?,
    val albumArtUri: String?,
    val sourcePackage: String? // package name of the media app
)
