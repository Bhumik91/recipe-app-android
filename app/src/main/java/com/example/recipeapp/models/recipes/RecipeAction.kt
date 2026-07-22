package com.example.recipeapp.models.recipes

/**
 * Defines the possible actions a user can take regarding a recipe's saved state.
 * This is primarily used to tailor notification messaging depending on the action.
 */
enum class RecipeAction {
    SAVED,
    REMOVED
}
