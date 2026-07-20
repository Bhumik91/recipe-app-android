package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.AssetJsonLoader
import com.example.recipeapp.core.session.SavedRecipesManager
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.recipes.data.mapper.toUiModel
import com.example.recipeapp.features.recipes.model.ComplexSearchResponseDto
import com.example.recipeapp.features.recipes.model.CuisineOptions
import com.example.recipeapp.features.recipes.model.PaginatedRecipes
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.model.RecipeDetailDto
import com.example.recipeapp.features.recipes.model.RecipeDetailUiModel
import kotlinx.serialization.json.Json

class DummyRecipeRepositoryImpl(
    private val assetJsonLoader: AssetJsonLoader,
    private val savedRecipesManager: SavedRecipesManager,
    private val sessionManager: SessionManager
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

    override suspend fun getSavedRecipes(): NetworkResult<List<RecipeCardUiModel>> {
        val ids = savedRecipesManager.getSavedIds().toList()
        if (ids.isEmpty()) return NetworkResult.Success(emptyList())
        return getRecipesByIds(ids)
    }


    override fun toggleSavedRecipe(recipeId: Int) {
        savedRecipesManager.toggleSaved(recipeId)
    }

    override suspend fun removeSavedRecipe(recipeId: Int): NetworkResult<Unit> {
        savedRecipesManager.removeSaved(recipeId)
        return NetworkResult.Success(Unit)
    }

    override fun isRecipeSaved(recipeId: Int): Boolean = savedRecipesManager.isSaved(recipeId)

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
