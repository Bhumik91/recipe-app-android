package com.example.recipeapp.domain.recipe.repository

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.data.recipes.uimodel.PaginatedRecipes
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.example.recipeapp.data.recipes.uimodel.RecipeDetailUiModel

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

    // --- Local-only operations (Room-backed, never hit the network) ---
    suspend fun toggleSavedRecipe(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null, readyInMinutes: Int? = null)
    suspend fun removeSavedRecipe(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null): NetworkResult<Unit>
    suspend fun isRecipeSaved(recipeId: Int): Boolean
    fun getCuisines(): List<String>
    fun getUserName(): String
}
