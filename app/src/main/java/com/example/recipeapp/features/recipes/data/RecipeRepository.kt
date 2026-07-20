package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.recipes.model.PaginatedRecipes
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.model.RecipeDetailUiModel

// Single contract implemented by RemoteRecipeRepositoryImpl (network),
// DummyRecipeRepositoryImpl (bundled JSON), and FallbackRecipeRepository (decorator
// that calls remote and falls back to dummy on a 402 quota error). ViewModels only
// ever depend on this interface — Koin decides which impl is injected.
interface RecipeRepository {

    // --- Network-backed reads (fall back to dummy data on quota exhaustion) ---
    suspend fun getExploreRecipes(cuisine: String?, diet: String?, offset: Int): NetworkResult<PaginatedRecipes>
    suspend fun getRecipesByIds(ids: List<Int>): NetworkResult<List<RecipeCardUiModel>>
    suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>>
    suspend fun searchRecipes(query: String, diet: String?): NetworkResult<List<RecipeCardUiModel>>
    suspend fun getRecipeDetail(recipeId: Int): NetworkResult<RecipeDetailUiModel>

    // --- Local-only operations (SharedPreferences-backed, never hit the network) ---
    fun toggleSavedRecipe(recipeId: Int)
    suspend fun removeSavedRecipe(recipeId: Int): NetworkResult<Unit>
    fun isRecipeSaved(recipeId: Int): Boolean
    fun getCuisines(): List<String>
    fun getUserName(): String
}
