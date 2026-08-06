package com.example.recipeapp.data.recipes.uimodel

import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String?
)
