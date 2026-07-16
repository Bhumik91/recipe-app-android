package com.example.recipeapp.features.dashboard.home.model

import android.content.Context
import com.example.recipeapp.R

object HomeDummyData {

    fun getCuisineChips(context: Context): List<ChipUiModel> = listOf(
        ChipUiModel(id = 1, label = context.getString(R.string.home_chip_all), isSelected = true),
        ChipUiModel(id = 2, label = context.getString(R.string.home_chip_indian)),
        ChipUiModel(id = 3, label = context.getString(R.string.home_chip_italian)),
        ChipUiModel(id = 4, label = context.getString(R.string.home_chip_asian)),
        ChipUiModel(id = 5, label = context.getString(R.string.home_chip_chinese)),
        ChipUiModel(id = 6, label = context.getString(R.string.home_chip_mexican))
    )

    fun getSavedRecipes(context: Context): List<RecipeCardUiModel> = listOf(
        RecipeCardUiModel(
            id = 1,
            title = context.getString(R.string.home_recipe_greek_salad),
            readyInMinutes = 15,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        ),
        RecipeCardUiModel(
            id = 2,
            title = context.getString(R.string.home_recipe_pesto_pasta),
            readyInMinutes = 18,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        ),
        RecipeCardUiModel(
            id = 3,
            title = context.getString(R.string.home_recipe_taco_bowl),
            readyInMinutes = 22,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        ),
        RecipeCardUiModel(
            id = 4,
            title = context.getString(R.string.home_recipe_sushi_stack),
            readyInMinutes = 12,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        )
    )

    fun getExploreRecipes(context: Context): List<RecipeCardUiModel> = listOf(
        RecipeCardUiModel(
            id = 101,
            title = context.getString(R.string.home_recipe_steak_tomato),
            readyInMinutes = 20,
            imageRes = R.drawable.ic_default_image,
            isSaved = false
        ),
        RecipeCardUiModel(
            id = 102,
            title = context.getString(R.string.home_recipe_pilaf),
            readyInMinutes = 25,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        ),
        RecipeCardUiModel(
            id = 103,
            title = context.getString(R.string.home_recipe_green_curry),
            readyInMinutes = 28,
            imageRes = R.drawable.ic_default_image,
            isSaved = false
        ),
        RecipeCardUiModel(
            id = 104,
            title = context.getString(R.string.home_recipe_chickpea_wrap),
            readyInMinutes = 16,
            imageRes = R.drawable.ic_default_image,
            isSaved = false
        ),
        RecipeCardUiModel(
            id = 105,
            title = context.getString(R.string.home_recipe_roasted_salmon),
            readyInMinutes = 24,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        ),
        RecipeCardUiModel(
            id = 106,
            title = context.getString(R.string.home_recipe_creamy_pasta),
            readyInMinutes = 19,
            imageRes = R.drawable.ic_default_image,
            isSaved = false
        ),
        RecipeCardUiModel(
            id = 107,
            title = context.getString(R.string.home_recipe_herb_chicken),
            readyInMinutes = 21,
            imageRes = R.drawable.ic_default_image,
            isSaved = false
        ),
        RecipeCardUiModel(
            id = 108,
            title = context.getString(R.string.home_recipe_miso_rice),
            readyInMinutes = 14,
            imageRes = R.drawable.ic_default_image,
            isSaved = true
        )
    )
}
