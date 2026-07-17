package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.core.data.RecipeDummy
import com.example.recipeapp.core.network.ApiErrorHandler
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.SavedRecipesManager
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.dashboard.home.model.PaginatedRecipes
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.model.toUiModel

class RecipeRepositoryImpl(
    private val api: RecipeApiService,
    private val savedRecipesManager: SavedRecipesManager,
    private val sessionManager: SessionManager
) : RecipeRepository {

    override suspend fun getExploreRecipes(
        cuisine: String?,
        offset: Int
    ): NetworkResult<PaginatedRecipes> =
        ApiErrorHandler.safeApiCall {
            val dto = api.getExploreRecipes(cuisine = cuisine, offset = offset)
            PaginatedRecipes(
                results = dto.results.map { it.toUiModel(savedRecipesManager.isSaved(it.id)) },
                offset = dto.offset,
                number = dto.number,
                totalResults = dto.totalResults
            )
        }

    override suspend fun getRecipesByIds(
        ids: List<Int>
    ): NetworkResult<List<RecipeCardUiModel>> =
        ApiErrorHandler.safeApiCall { 
            api.getRecipesByIds(ids.joinToString(",")).map { it.toUiModel(isSaved = true) } 
        }

    override suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>> {
        val ids = savedRecipesManager.getSavedIds().toList()
        if (ids.isEmpty()) return NetworkResult.Success(emptyList())
        return getRecipesByIds(ids)
    }

    override fun toggleSavedRecipe(recipeId: Int) {
        savedRecipesManager.toggleSaved(recipeId)
    }

    override fun getCuisines(): List<String> = RecipeDummy.cuisines
    override fun getUserName(): String = sessionManager.getUserName()
}
