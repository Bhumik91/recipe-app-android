package com.example.recipeapp.data.recipes.uimodel

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