package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.dashboard.home.model.PaginatedRecipes
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel

interface RecipeRepository {
    suspend fun getExploreRecipes(cuisine: String?, offset: Int): NetworkResult<PaginatedRecipes>
    suspend fun getRecipesByIds(ids: List<Int>): NetworkResult<List<RecipeCardUiModel>>
    suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>>
    fun toggleSavedRecipe(recipeId: Int)
    fun getCuisines(): List<String>
    fun getUserName(): String
}
