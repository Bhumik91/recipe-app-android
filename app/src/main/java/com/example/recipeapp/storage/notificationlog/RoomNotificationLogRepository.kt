package com.example.recipeapp.storage.notificationlog

import com.example.recipeapp.models.recipes.RecipeAction
import kotlinx.coroutines.flow.Flow

class RoomNotificationLogRepository(
    private val dao: NotificationLogDao,
    private val userId: String
) : NotificationLogRepository {

    override suspend fun log(recipeId: Int, recipeName: String, recipeImageUrl: String, action: RecipeAction) {
        dao.insert(
            NotificationLogEntity(
                recipeId = recipeId,
                recipeName = recipeName,
                recipeImageUrl = recipeImageUrl,
                action = action,
                timestamp = System.currentTimeMillis(),
                userId = userId
            )
        )
    }

    override fun observeLogs(): Flow<List<NotificationLogEntity>> = dao.getLogsForUser(userId)
}
