package com.example.recipeapp.storage.notificationlog

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.recipeapp.models.recipes.RecipeAction

/**
 * Room record of a single save/remove action, shown as a log entry in NotificationFragment.
 * Persisted independently of the SharedPreferences-backed saved-state so history survives
 * a recipe being un-saved.
 */
@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Int,
    val recipeName: String,
    val recipeImageUrl: String,
    val action: RecipeAction,
    val timestamp: Long,
    val userId: String
)

/** Room can't persist enums directly — stores the action by its name. */
class RecipeActionConverter {
    @TypeConverter
    fun fromRecipeAction(action: RecipeAction): String = action.name

    @TypeConverter
    fun toRecipeAction(value: String): RecipeAction = RecipeAction.valueOf(value)
}
