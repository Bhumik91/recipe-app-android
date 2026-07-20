package com.example.recipeapp.features.recipes.model

data class RecipeCardUiModel(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val imageUrl: String,
    val isSaved: Boolean
)

data class PaginatedRecipes(
    val results: List<RecipeCardUiModel>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)