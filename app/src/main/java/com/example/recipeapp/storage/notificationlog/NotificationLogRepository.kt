package com.example.recipeapp.storage.notificationlog

import com.example.recipeapp.models.recipes.RecipeAction
import kotlinx.coroutines.flow.Flow

/**
 * Read/write access to the persisted save/remove history shown in NotificationFragment.
 * Kept separate from SavedRecipesStorage (which owns the current saved-state) since this
 * is an append-only log, not current state. Scoped per logged-in user, same as
 * SavedRecipesStorage.
 */
interface NotificationLogRepository {
    suspend fun log(recipeId: Int, recipeName: String, recipeImageUrl: String, action: RecipeAction)
    fun observeLogs(): Flow<List<NotificationLogEntity>>
}
