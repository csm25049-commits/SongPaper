package com.example.songpaper.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Helper to enable/disable the [MediaListenerService] component based on the user's
 * preference. Disabling the component prevents Android from binding the notification
 * listener, effectively stopping the service without killing the process.
 */
object ServiceToggleHelper {
    private const val TAG = "ServiceToggleHelper"

    fun setEnabled(context: Context, enabled: Boolean) {
        val component = ComponentName(context, com.example.songpaper.MediaListenerService::class.java)
        val newState = if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        context.packageManager.setComponentEnabledSetting(
            component,
            newState,
            PackageManager.DONT_KILL_APP
        )
        Log.d(TAG, "MediaListenerService component set to ${if (enabled) "ENABLED" else "DISABLED"}")
    }
}
