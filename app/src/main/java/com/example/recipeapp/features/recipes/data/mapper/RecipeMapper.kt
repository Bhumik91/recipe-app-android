package com.example.recipeapp.features.recipes.data.mapper

import com.example.recipeapp.features.recipes.model.IngredientDto
import com.example.recipeapp.features.recipes.model.IngredientUiModel
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.model.RecipeDetailDto
import com.example.recipeapp.features.recipes.model.RecipeDetailUiModel
import com.example.recipeapp.features.recipes.model.SpoonacularRecipeDto
import com.example.recipeapp.features.recipes.model.StepDto
import com.example.recipeapp.features.recipes.model.StepUiModel

fun SpoonacularRecipeDto.toUiModel(isSaved: Boolean): RecipeCardUiModel =
    RecipeCardUiModel(
        id = id,
        title = title,
        readyInMinutes = readyInMinutes ?: 0,
        imageUrl = image ?: "",
        isSaved = isSaved
    )

private fun IngredientDto.toUiModel(): IngredientUiModel = IngredientUiModel(
    id = id,
    name = name ?: "",
    imageUrl = image?.let { "https://img.spoonacular.com/ingredients_100x100/$it" } ?: "",
    baseAmount = measures?.metric?.amount ?: 0f,
    unit = measures?.metric?.unitShort ?: ""
)

private fun StepDto.toUiModel(): StepUiModel = StepUiModel(
    number = number ?: 0,
    instruction = step ?: "",
    requiredIngredientNames = ingredients
        ?.mapNotNull { it.name }
        ?.filter { it.isNotBlank() }
        ?: emptyList()
)

fun RecipeDetailDto.toUiModel(isSaved: Boolean): RecipeDetailUiModel = RecipeDetailUiModel(
    id = id,
    title = title ?: "",
    imageUrl = image ?: "",
    servings = servings ?: 1,
    readyInMinutes = readyInMinutes ?: 0,
    attribution = sourceName,
    shareUrl = spoonacularSourceUrl,
    ingredients = extendedIngredients?.map { it.toUiModel() } ?: emptyList(),
    instructionSteps = analyzedInstructions
        ?.flatMap { it.steps ?: emptyList() }
        ?.map { it.toUiModel() }
        ?: emptyList(),
    isSaved = isSaved
)
