package com.example.recipeapp.domain.recipe.repository

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.storage.assets.AssetJsonLoader
import com.example.recipeapp.storage.savedrecipes.SavedRecipesStorage
import com.example.recipeapp.storage.session.SessionStorage
import com.example.recipeapp.data.recipes.mapper.toUiModel
import com.example.recipeapp.data.recipes.dto.ComplexSearchResponseDto
import com.example.recipeapp.data.recipes.options.CuisineOptions
import com.example.recipeapp.data.recipes.uimodel.PaginatedRecipes
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.example.recipeapp.data.recipes.dto.RecipeDetailDto
import com.example.recipeapp.data.recipes.uimodel.RecipeDetailUiModel
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class DummyRecipeRepositoryImpl(
    private val assetJsonLoader: AssetJsonLoader,
    private val savedRecipesManager: SavedRecipesStorage,
    private val sessionManager: SessionStorage
) : RecipeRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private fun loadSearchResponse(): ComplexSearchResponseDto {
        val raw = assetJsonLoader.readJson("dummy/dummy_recipes.json")
        return json.decodeFromString(raw)
    }

    private fun loadRecipeDetail(): RecipeDetailDto {
        val raw = assetJsonLoader.readJson("dummy/dummy_recipe_detail.json")
        return json.decodeFromString(raw)
    }


    override suspend fun getExploreRecipes(
        cuisine: String?,
        diet: String?,
        offset: Int
    ): NetworkResult<PaginatedRecipes> {
        val dto = loadSearchResponse()
        return NetworkResult.Success(
            PaginatedRecipes(
                results = dto.results.map { it.toUiModel(savedRecipesManager.isSaved(it.id)) },
                offset = dto.offset,
                number = dto.number,
                totalResults = dto.totalResults
            )
        )
    }

    override suspend fun getRecipesByIds(ids: List<Int>): NetworkResult<List<RecipeCardUiModel>> {
        val results = loadSearchResponse().results
            .filter { it.id in ids }
            .map { it.toUiModel(isSaved = true) }
        return NetworkResult.Success(results)
    }

    override suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>> =
        NetworkResult.Success(savedRecipesManager.observeSavedRecipes().first())

    override suspend fun toggleSavedRecipe(recipeId: Int, recipeName: String?, recipeImageUrl: String?, readyInMinutes: Int?) {
        savedRecipesManager.toggleSaved(recipeId, recipeName, recipeImageUrl, readyInMinutes)
    }

    override suspend fun removeSavedRecipe(recipeId: Int, recipeName: String?, recipeImageUrl: String?): NetworkResult<Unit> {
        savedRecipesManager.removeSaved(recipeId, recipeName, recipeImageUrl)
        return NetworkResult.Success(Unit)
    }

    override suspend fun isRecipeSaved(recipeId: Int): Boolean = savedRecipesManager.isSaved(recipeId)

    override fun getCuisines(): List<String> = CuisineOptions.cuisines

    override suspend fun searchRecipes(
        query: String,
        diet: String?
    ): NetworkResult<List<RecipeCardUiModel>> {
        val results = loadSearchResponse().results
            .map { it.toUiModel(savedRecipesManager.isSaved(it.id)) }
        return NetworkResult.Success(results)
    }

    override suspend fun getRecipeDetail(recipeId: Int): NetworkResult<RecipeDetailUiModel> {
        val dto = loadRecipeDetail()
        return NetworkResult.Success(dto.toUiModel(savedRecipesManager.isSaved(recipeId)))
    }

    override fun getUserName(): String = sessionManager.getUserName()
}
