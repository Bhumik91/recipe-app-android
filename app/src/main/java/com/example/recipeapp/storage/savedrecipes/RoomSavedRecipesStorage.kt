package com.example.recipeapp.storage.savedrecipes

import com.example.recipeapp.core.notifications.RecipeNotifier
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.storage.notificationlog.NotificationLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private fun SavedRecipeEntity.toUiModel() = RecipeCardUiModel(
    id = recipeId,
    title = title,
    readyInMinutes = readyInMinutes,
    imageUrl = imageUrl,
    isSaved = true
)

class RoomSavedRecipesStorage(
    private val dao: SavedRecipeDao,
    private val userId: String,
    private val recipeNotifier: RecipeNotifier? = null,
    private val notificationLogRepository: NotificationLogRepository? = null,
    private val appScope: CoroutineScope? = null
) : SavedRecipesStorage {

    override suspend fun isSaved(recipeId: Int): Boolean = dao.isSaved(recipeId, userId)

    override fun observeSavedRecipes(): Flow<List<RecipeCardUiModel>> =
        dao.observeSavedRecipes(userId).map { entities -> entities.map { it.toUiModel() } }

    /**
     * Toggles the saved status of a recipe.
     * Optionally accepts the recipe name, image URL, and prep time to persist alongside the
     * id, so the Saved tab can render full recipe cards without a network round-trip.
     * The RecipeNotifier is responsible for displaying a system-level push notification
     * confirming the action. This keeps our UI and Notification logic decoupled.
     */
    override suspend fun toggleSaved(recipeId: Int, recipeName: String?, recipeImageUrl: String?, readyInMinutes: Int?) {
        val wasSaved = dao.isSaved(recipeId, userId)
        if (wasSaved) {
            dao.delete(recipeId, userId)
        } else {
            dao.upsert(
                SavedRecipeEntity(
                    recipeId = recipeId,
                    userId = userId,
                    title = recipeName ?: "Recipe #$recipeId",
                    imageUrl = recipeImageUrl.orEmpty(),
                    readyInMinutes = readyInMinutes ?: 0,
                    savedAt = System.currentTimeMillis()
                )
            )
        }

        val action = if (wasSaved) RecipeAction.REMOVED else RecipeAction.SAVED
        notifyAndLog(recipeId, recipeName, recipeImageUrl, action)
    }

    override suspend fun removeSaved(recipeId: Int, recipeName: String?, recipeImageUrl: String?) {
        dao.delete(recipeId, userId)
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
}
