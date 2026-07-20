package com.example.recipeapp.features.recipes.model

import kotlinx.serialization.Serializable

@Serializable
data class ComplexSearchResponseDto(
    val results: List<SpoonacularRecipeDto>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

@Serializable
data class SpoonacularRecipeDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    val readyInMinutes: Int? = null
)

