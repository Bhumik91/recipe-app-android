package com.example.recipeapp.storage.savedrecipes

interface SavedRecipesStorage {
    fun getSavedIds(): Set<Int>
    fun isSaved(recipeId: Int): Boolean
    fun toggleSaved(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null)
    fun removeSaved(recipeId: Int, recipeName: String? = null, recipeImageUrl: String? = null)
}
