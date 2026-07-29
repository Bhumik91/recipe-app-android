package com.example.recipeapp.storage.notificationlog

import com.example.recipeapp.models.recipes.RecipeAction
import com.example.recipeapp.storage.session.SessionStorage
import kotlinx.coroutines.flow.Flow

// Reads the userId from SessionStorage on every call rather than capturing it once at
// construction — this repository is a Koin singleton that outlives any single logged-in
// session, so a userId baked into the constructor would still point at the previous
// user's id after a logout/login switch within the same process.
class RoomNotificationLogRepository(
    private val dao: NotificationLogDao,
    private val sessionStorage: SessionStorage
) : NotificationLogRepository {

    override suspend fun log(recipeId: Int, recipeName: String, recipeImageUrl: String, action: RecipeAction) {
        dao.insert(
            NotificationLogEntity(
                recipeId = recipeId,
                recipeName = recipeName,
                recipeImageUrl = recipeImageUrl,
                action = action,
                timestamp = System.currentTimeMillis(),
                userId = sessionStorage.getUserId().toString()
            )
        )
    }

    override fun observeLogs(): Flow<List<NotificationLogEntity>> =
        dao.getLogsForUser(sessionStorage.getUserId().toString())
}
