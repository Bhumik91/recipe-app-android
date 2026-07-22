package com.example.recipeapp.storage.notificationlog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationLogDao {

    @Insert
    suspend fun insert(log: NotificationLogEntity)

    // Reactive, newest first — the All/Saved/Removed tab split in NotificationFragment
    // filters this same stream by `action` instead of issuing separate queries.
    @Query("SELECT * FROM notification_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLogsForUser(userId: String): Flow<List<NotificationLogEntity>>
}
