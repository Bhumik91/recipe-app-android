package com.example.recipeapp.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Registry and manager for Android Notification Channels.
 * Ensures that channels are registered before posting any notifications on Android 8.0+ (Oreo).
 */
object NotificationChannels {
    const val RECIPE_ACTIVITY_CHANNEL_ID = "recipe_activity_channel"

    /**
     * Initializes the necessary channels with the system.
     * Safe to call multiple times or on older Android versions where channels aren't supported.
     */
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            RECIPE_ACTIVITY_CHANNEL_ID,
            "Recipe activity",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies you when a recipe is saved or removed"
        }
        manager.createNotificationChannel(channel)
    }
}
