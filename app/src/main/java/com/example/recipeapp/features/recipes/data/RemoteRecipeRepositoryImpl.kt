package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.core.network.ApiErrorHandler
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.SavedRecipesStorage
import com.example.recipeapp.core.session.SessionStorage
import com.example.recipeapp.features.recipes.data.mapper.toUiModel
import com.example.recipeapp.features.recipes.model.CuisineOptions
import com.example.recipeapp.features.recipes.model.PaginatedRecipes
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.model.RecipeDetailUiModel


class RemoteRecipeRepositoryImpl(
    private val api: RecipeApiService,
    private val savedRecipesManager: SavedRecipesStorage,
    private val sessionManager: SessionStorage
) : RecipeRepository {

    override suspend fun getExploreRecipes(
        cuisine: String?,
        diet: String?,
        offset: Int
    ): NetworkResult<PaginatedRecipes> =
        ApiErrorHandler.safeApiCall {
            val dto = api.getExploreRecipes(cuisine = cuisine, diet = diet, offset = offset)
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

    override suspend fun removeSavedRecipe(recipeId: Int): NetworkResult<Unit> =
        ApiErrorHandler.safeApiCall {
            savedRecipesManager.removeSaved(recipeId)
        }

    override fun isRecipeSaved(recipeId: Int): Boolean = savedRecipesManager.isSaved(recipeId)

    override fun getCuisines(): List<String> = CuisineOptions.cuisines

    override suspend fun searchRecipes(
        query: String,
        diet: String?
    ): NetworkResult<List<RecipeCardUiModel>> =
        ApiErrorHandler.safeApiCall {
            val dto = api.searchRecipes(query = query, diet = diet)
            dto.results.map { it.toUiModel(savedRecipesManager.isSaved(it.id)) }
        }

    override suspend fun getRecipeDetail(recipeId: Int): NetworkResult<RecipeDetailUiModel> =
        ApiErrorHandler.safeApiCall {
            val dto = api.getRecipeInformation(recipeId)
            dto.toUiModel(savedRecipesManager.isSaved(recipeId))
        }

    override fun getUserName(): String = sessionManager.getUserName()
}
