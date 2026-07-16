package com.example.recipeapp.features.dashboard.home.model

data class RecipeCardUiModel(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val imageRes: Int,
    val isSaved: Boolean
)

data class ChipUiModel(
    val id: Int,
    val label: String,
    val isSelected: Boolean = false
)
