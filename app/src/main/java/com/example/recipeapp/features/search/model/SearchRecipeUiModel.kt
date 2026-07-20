package com.example.recipeapp.features.search.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String?
)
