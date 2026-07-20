package com.example.recipeapp.core.session

import android.content.Context
import androidx.core.content.edit

class SavedRecipesManager(context: Context, userId: String) {

    private val prefs = context.getSharedPreferences("saved_recipes_$userId", Context.MODE_PRIVATE)

    fun getSavedIds(): Set<Int> =
        prefs.getStringSet(KEY_SAVED_IDS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

    fun isSaved(recipeId: Int): Boolean = recipeId in getSavedIds()

    fun toggleSaved(recipeId: Int) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        val idStr = recipeId.toString()
        if (idStr in current) current.remove(idStr) else current.add(idStr)
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }
    }

    fun removeSaved(recipeId: Int) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        current.remove(recipeId.toString())
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }
    }

    companion object {
        private const val KEY_SAVED_IDS = "saved_recipe_ids"
    }
}
