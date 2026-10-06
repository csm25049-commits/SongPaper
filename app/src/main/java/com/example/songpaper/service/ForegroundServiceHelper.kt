package com.example.songpaper.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.songpaper.MediaListenerService

/**
 * Helper that creates and starts a low‑priority foreground notification for the
 * MediaListenerService. This complies with Android 12+ background execution limits.
 */
object ForegroundServiceHelper {
    private const val CHANNEL_ID = "songpaper_foreground"
    private const val CHANNEL_NAME = "SongPaper Service"
    private const val NOTIF_ID = 101

    fun startForeground(service: MediaListenerService) {
        val ctx = service.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Keeps SongPaper listening for now‑playing music" }
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setContentTitle("SongPaper is active")
            .setContentText("Listening for music to update wallpaper")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
        service.startForeground(NOTIF_ID, notification)
    }
}
