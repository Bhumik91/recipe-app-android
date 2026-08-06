package com.example.recipeapp.data.recipes.mapper

import com.example.recipeapp.data.recipes.dto.IngredientDto
import com.example.recipeapp.data.recipes.dto.RecipeDetailDto
import com.example.recipeapp.data.recipes.dto.SpoonacularRecipeDto
import com.example.recipeapp.data.recipes.dto.StepDto
import com.example.recipeapp.data.recipes.uimodel.IngredientUiModel
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import com.example.recipeapp.data.recipes.uimodel.RecipeDetailUiModel
import com.example.recipeapp.data.recipes.uimodel.StepUiModel

fun SpoonacularRecipeDto.toUiModel(isSaved: Boolean): RecipeCardUiModel =
    RecipeCardUiModel(
        id = id,
        title = title,
        readyInMinutes = readyInMinutes ?: 0,
        imageUrl = image ?: "",
        isSaved = isSaved
    )

private fun IngredientDto.toUiModel(): IngredientUiModel =
    IngredientUiModel(
        id = id,
        name = name ?: "",
        imageUrl = image?.let { "https://img.spoonacular.com/ingredients_100x100/$it" } ?: "",
        baseAmount = measures?.metric?.amount ?: 0f,
        unit = measures?.metric?.unitShort ?: ""
    )

private fun StepDto.toUiModel(): StepUiModel =
    StepUiModel(
        number = number ?: 0,
        instruction = step ?: "",
        requiredIngredientNames = ingredients
            ?.mapNotNull { it.name }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    )

fun RecipeDetailDto.toUiModel(isSaved: Boolean): RecipeDetailUiModel =
    RecipeDetailUiModel(
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
