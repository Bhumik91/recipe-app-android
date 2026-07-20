package com.example.recipeapp.features.recipes.model

data class RecipeDetailUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val servings: Int,
    val readyInMinutes: Int,
    val attribution: String?,
    val shareUrl: String?,
    val ingredients: List<IngredientUiModel>,
    val instructionSteps: List<StepUiModel>,
    val isSaved: Boolean
)

data class IngredientUiModel(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val baseAmount: Float,
    val unit: String
) {
    fun scaledAmount(baseServings: Int, targetServings: Int): Float =
        baseAmount * (targetServings.toFloat() / baseServings.coerceAtLeast(1))

    fun displayAmount(baseServings: Int, targetServings: Int): String {
        val scaled = scaledAmount(baseServings, targetServings)
        val trimmed = "%.2f".format(scaled).trimEnd('0').trimEnd('.')
        return "$trimmed $unit".trim()
    }
}

data class StepUiModel(
    val number: Int,
    val instruction: String,
    val requiredIngredientNames: List<String>
)
