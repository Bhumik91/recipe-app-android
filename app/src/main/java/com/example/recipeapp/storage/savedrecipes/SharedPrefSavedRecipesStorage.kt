package com.example.recipeapp.storage.savedrecipes

import android.content.Context
import androidx.core.content.edit

import com.example.recipeapp.core.notifications.RecipeNotifier
import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.storage.notificationlog.NotificationLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SharedPrefSavedRecipesStorage(
    context: Context,
    private val userId: String,
    private val recipeNotifier: RecipeNotifier? = null,
    private val notificationLogRepository: NotificationLogRepository? = null,
    private val appScope: CoroutineScope? = null
) : SavedRecipesStorage {

    private val prefs = context.getSharedPreferences("saved_recipes_$userId", Context.MODE_PRIVATE)

    override fun getSavedIds(): Set<Int> =
        prefs.getStringSet(KEY_SAVED_IDS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

    override fun isSaved(recipeId: Int): Boolean = recipeId in getSavedIds()

    /**
     * Toggles the saved status of a recipe.
     * Optionally accepts the recipe name and image URL to be used by the RecipeNotifier.
     * The RecipeNotifier is responsible for displaying a system-level push notification 
     * confirming the action. This keeps our UI and Notification logic decoupled.
     */
    override fun toggleSaved(recipeId: Int, recipeName: String?, recipeImageUrl: String?) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        val idStr = recipeId.toString()
        val wasAdded = idStr !in current
        if (wasAdded) current.add(idStr) else current.remove(idStr)
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }

        val action = if (wasAdded) RecipeAction.SAVED else RecipeAction.REMOVED
        notifyAndLog(recipeId, recipeName, recipeImageUrl, action)
    }

    override fun removeSaved(recipeId: Int, recipeName: String?, recipeImageUrl: String?) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        current.remove(recipeId.toString())
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }

        notifyAndLog(recipeId, recipeName, recipeImageUrl, RecipeAction.REMOVED)
    }

    // Posts the system status-bar notification (if RecipeNotifier is wired and permission
    // allows it) and persists the action to the notification log, independent of that
    // permission — the log is an in-app history, not a system-level notification.
    private fun notifyAndLog(recipeId: Int, recipeName: String?, recipeImageUrl: String?, action: RecipeAction) {
        val displayName = recipeName ?: "Recipe #$recipeId"
        recipeNotifier?.notify(recipeId, displayName, action)
        appScope?.launch {
            notificationLogRepository?.log(recipeId, displayName, recipeImageUrl.orEmpty(), action)
        }
    }

    companion object {
        private const val KEY_SAVED_IDS = "saved_recipe_ids"
    }
}
