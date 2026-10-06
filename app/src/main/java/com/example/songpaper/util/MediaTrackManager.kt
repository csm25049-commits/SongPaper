package com.example.songpaper.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.songpaper.model.TrackInfo

/**
 * Holds the current track and playback state.
 */
object MediaTrackManager {
    private val _track = MutableStateFlow<TrackInfo?>(null)
    val track: StateFlow<TrackInfo?> = _track

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun updateTrack(info: TrackInfo) {
        _track.value = info
    }

    fun updatePlayback(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }
}
