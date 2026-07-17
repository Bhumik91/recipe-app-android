package com.example.recipeapp.features.recipes.data

import com.example.recipeapp.features.recipes.model.ComplexSearchResponseDto
import com.example.recipeapp.features.recipes.model.SpoonacularRecipeDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {

    @GET("recipes/complexSearch")
    suspend fun getExploreRecipes(
        @Query("cuisine") cuisine: String? = null,
        @Query("offset") offset: Int,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true
    ): ComplexSearchResponseDto

    @GET("recipes/informationBulk")
    suspend fun getRecipesByIds(
        @Query("ids") ids: String
    ): List<SpoonacularRecipeDto>
}
