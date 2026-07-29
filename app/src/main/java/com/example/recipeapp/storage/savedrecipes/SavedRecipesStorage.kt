package com.example.recipeapp.storage.savedrecipes

import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import kotlinx.coroutines.flow.Flow

interface SavedRecipesStorage {
    suspend fun isSaved(recipeId: Int): Boolean
    suspend fun toggleSaved(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null, readyInMinutes: Int? = null)
    suspend fun removeSaved(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null)
    fun observeSavedRecipes(): Flow<List<RecipeCardUiModel>>
}
