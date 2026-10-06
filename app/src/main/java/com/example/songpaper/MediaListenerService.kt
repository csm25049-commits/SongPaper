package com.example.songpaper

import android.graphics.BitmapFactory
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
import android.media.session.PlaybackState
import android.util.Log
import com.example.songpaper.model.TrackInfo
import com.example.songpaper.util.MediaTrackManager
import com.example.songpaper.service.ForegroundServiceHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaListenerService :
    android.service.notification.NotificationListenerService(),
    OnActiveSessionsChangedListener {

    @Inject
    lateinit var mediaSessionManager: MediaSessionManager

    @Inject
    lateinit var appScope: CoroutineScope // injected from AppModule

    private val tag = "MediaListenerService"
    private var currentController: MediaController? = null
    private var foregroundStarted = false

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(tag, "Notification listener connected")
        // Start foreground service to satisfy Android background limits
        if (!foregroundStarted) {
            ForegroundServiceHelper.startForeground(this)
            foregroundStarted = true
        }
        mediaSessionManager.addOnActiveSessionsChangedListener(this, componentName)
        onActiveSessionsChanged(mediaSessionManager.getActiveSessions(componentName))
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSessionManager.removeOnActiveSessionsChangedListener(this)
        Log.d(tag, "Listener destroyed")
    }

    override fun onActiveSessionsChanged(controllers: List<MediaController>?) {
        Log.d(tag, "Active sessions changed: ${controllers?.size ?: 0}")
        val playing = controllers?.firstOrNull { ctrl ->
            val state = ctrl.playbackState?.state
            state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_PAUSED
        }
        if (playing != null && playing != currentController) {
            currentController?.unregisterCallback(mediaCallback)
            currentController = playing
            playing.registerCallback(mediaCallback)
            // Emit initial info
            emitTrackInfo(playing)
            MediaTrackManager.updatePlayback(playing.playbackState?.state == PlaybackState.STATE_PLAYING)
        }
    }

    private val mediaCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: android.media.MediaMetadata?) {
            super.onMetadataChanged(metadata)
            Log.d(tag, "Metadata changed")
            currentController?.let { emitTrackInfo(it, metadata) }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            Log.d(tag, "Playback state changed: ${state?.state}")
            MediaTrackManager.updatePlayback(state?.state == PlaybackState.STATE_PLAYING)
        }
    }

    private fun emitTrackInfo(controller: MediaController, metadata: android.media.MediaMetadata? = null) {
        val md = metadata ?: controller.metadata ?: run {
            Log.d(tag, "No metadata available")
            return
        }
        val title = md.getString(android.media.MediaMetadata.METADATA_KEY_TITLE)
        val artist = md.getString(android.media.MediaMetadata.METADATA_KEY_ARTIST)
        val albumArtBmp = md.getBitmap(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: md.getBitmap(android.media.MediaMetadata.METADATA_KEY_ART)
        val artUri = md.getString(android.media.MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
            ?: md.getString(android.media.MediaMetadata.METADATA_KEY_ART_URI)
        // Resolve bitmap if we only have a URI
        val bitmap = albumArtBmp ?: artUri?.let {
            try {
                val stream = contentResolver.openInputStream(android.net.Uri.parse(it))
                stream?.use { s -> BitmapFactory.decodeStream(s) }
            } catch (e: Exception) {
                null
            }
        }
        val info = TrackInfo(
            title = title,
            artist = artist,
            albumArt = bitmap,
            albumArtUri = artUri,
            sourcePackage = controller.packageName
        )
        MediaTrackManager.updateTrack(info)
        Log.d(tag, "Emitted track: ${title ?: "<no title>"} - ${artist ?: "<no artist>"} from ${controller.packageName}")
    }
}
