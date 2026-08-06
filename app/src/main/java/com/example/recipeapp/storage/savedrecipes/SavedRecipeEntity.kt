package com.example.recipeapp.storage.savedrecipes

import androidx.room.Entity

@Entity(tableName = "saved_recipes", primaryKeys = ["recipeId", "userId"])
data class SavedRecipeEntity(
    val recipeId: Int,
    val userId: String,
    val title: String,
    val imageUrl: String,
    val readyInMinutes: Int,
    val savedAt: Long
)
