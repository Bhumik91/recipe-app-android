package com.example.recipeapp.features.recipes.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailDto(
    val id: Int,
    val title: String? = null,
    val image: String? = null,
    val servings: Int? = null,
    val readyInMinutes: Int? = null,
    val sourceName: String? = null,
    val spoonacularSourceUrl: String? = null,
    val extendedIngredients: List<IngredientDto>? = null,
    val analyzedInstructions: List<InstructionGroupDto>? = null
)

@Serializable
data class IngredientDto(
    val id: Int,
    val name: String? = null,
    val image: String? = null,
    val measures: MeasuresDto? = null
)

@Serializable
data class MeasuresDto(
    val metric: MetricAmountDto? = null
)

@Serializable
data class MetricAmountDto(
    val amount: Float? = null,
    val unitShort: String? = null,
    val unitLong: String? = null
)

@Serializable
data class InstructionGroupDto(
    val name: String? = null,
    val steps: List<StepDto>? = null
)

@Serializable
data class StepDto(
    val number: Int? = null,
    val step: String? = null,
    val ingredients: List<StepIngredientDto>? = null
)

@Serializable
data class StepIngredientDto(
    val id: Int,
    val name: String? = null
)
