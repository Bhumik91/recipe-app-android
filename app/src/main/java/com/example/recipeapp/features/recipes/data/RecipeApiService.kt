package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.features.recipes.model.ComplexSearchResponseDto
import com.example.recipeapp.features.recipes.model.RecipeDetailDto
import com.example.recipeapp.features.recipes.model.SpoonacularRecipeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit HTTP interface for Spoonacular API calls only. Local operations
// (toggleSavedRecipe, removeSavedRecipe, getCuisines, etc.) do NOT belong here —
// they live in RecipeRepository's concrete implementations, which delegate to
// SavedRecipesStorage (SharedPreferences) and AssetJsonLoader (bundled JSON).
// RecipeApiService stays clean and focused: network calls, nothing else.
interface RecipeApiService {

    @GET("recipes/complexSearch")
    suspend fun getExploreRecipes(
        @Query("cuisine") cuisine: String? = null,
        @Query("diet") diet: String? = null,
        @Query("offset") offset: Int,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true
    ): ComplexSearchResponseDto

    @GET("recipes/informationBulk")
    suspend fun getRecipesByIds(
        @Query("ids") ids: String
    ): List<SpoonacularRecipeDto>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("diet") diet: String? = null,
        @Query("number") number: Int = 20,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true
    ): ComplexSearchResponseDto

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(@Path("id") id: Int): RecipeDetailDto
}
