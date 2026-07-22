package com.example.recipeapp.core.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recipeapp.R
import com.example.recipeapp.core.permissions.AppPermission
import com.example.recipeapp.core.permissions.PermissionManager
import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.ui.dashboard.DashboardActivity

/**
 * Interface responsible for dispatching system notifications related to recipes.
 * This abstracts away Android-specific Notification APIs from our storage or UI layers.
 */
interface RecipeNotifier {
    fun notify(recipeId: Int, recipeName: String, action: RecipeAction)
}

/**
 * Android implementation of [RecipeNotifier].
 * Builds and fires a system status bar notification when a recipe is saved or removed.
 */
class SystemRecipeNotifier(
    private val context: Context,
    private val permissionManager: PermissionManager
) : RecipeNotifier {

    /**
     * Posts a notification for the given recipe and action.
     * Silently bails out if POST_NOTIFICATIONS permission is not granted.
     * Clicking the notification will route the user to the Notification tab in DashboardActivity.
     */
    override fun notify(recipeId: Int, recipeName: String, action: RecipeAction) {
        // Respect the permission — never crash, never assume granted.
        if (!permissionManager.isGranted(AppPermission.NOTIFICATIONS)) return

        val title = if (action == RecipeAction.SAVED) "Recipe saved" else "Recipe removed"
        val text = if (action == RecipeAction.SAVED)
            "$recipeName was added to your saved recipes"
        else
            "$recipeName was removed from your saved recipes"

        val intent = Intent(context, DashboardActivity::class.java).apply {
            putExtra("open_notification_tab", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            recipeId, // unique per recipe so multiple notifications don't collide/overwrite each other's intent
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Using a default icon if R.drawable.ic_notification does not exist. 
        // For safety, let's use the app icon (ic_launcher_foreground if we can)
        // I will use R.mipmap.ic_launcher assuming it's available.
        val notification = NotificationCompat.Builder(context, NotificationChannels.RECIPE_ACTIVITY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Requires POST_NOTIFICATIONS permission
        try {
            NotificationManagerCompat.from(context).notify(recipeId, notification)
        } catch (e: SecurityException) {
            // Permission was revoked between check and notify
        }
    }
}
