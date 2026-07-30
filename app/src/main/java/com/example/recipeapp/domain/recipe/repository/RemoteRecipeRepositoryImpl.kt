package com.example.recipeapp.domain.recipe.repository

import com.example.recipeapp.core.network.ApiErrorHandler
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.storage.savedrecipes.SavedRecipesStorage
import com.example.recipeapp.storage.session.SessionStorage
import com.example.recipeapp.core.network.RecipeApiService
import com.example.recipeapp.data.recipes.mapper.toUiModel
import com.example.recipeapp.data.recipes.options.CuisineOptions
import com.example.recipeapp.data.recipes.uimodel.PaginatedRecipes
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.example.recipeapp.data.recipes.uimodel.RecipeDetailUiModel
import kotlinx.coroutines.flow.first


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

    // Room-backed: recipe details were persisted locally when saved, so this never
    // needs the network — the Saved tab works offline.
    override suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>> =
        NetworkResult.Success(savedRecipesManager.observeSavedRecipes().first())

    override suspend fun toggleSavedRecipe(recipeId: Int, recipeName: String?, recipeImageUrl: String?, readyInMinutes: Int?) {
        savedRecipesManager.toggleSaved(recipeId, recipeName, recipeImageUrl, readyInMinutes)
    }

    override suspend fun removeSavedRecipe(recipeId: Int, recipeName: String?, recipeImageUrl: String?): NetworkResult<Unit> =
        ApiErrorHandler.safeApiCall {
            savedRecipesManager.removeSaved(recipeId, recipeName, recipeImageUrl)
        }

    override suspend fun isRecipeSaved(recipeId: Int): Boolean = savedRecipesManager.isSaved(recipeId)

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
