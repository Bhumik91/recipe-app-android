package com.example.recipeapp.storage.savedrecipes

import android.content.Context
import androidx.core.content.edit

class SharedPrefSavedRecipesStorage(context: Context, userId: String) : SavedRecipesStorage {

    private val prefs = context.getSharedPreferences("saved_recipes_$userId", Context.MODE_PRIVATE)

    override fun getSavedIds(): Set<Int> =
        prefs.getStringSet(KEY_SAVED_IDS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

    override fun isSaved(recipeId: Int): Boolean = recipeId in getSavedIds()

    override fun toggleSaved(recipeId: Int) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        val idStr = recipeId.toString()
        if (idStr in current) current.remove(idStr) else current.add(idStr)
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }
    }

    override fun removeSaved(recipeId: Int) {
        val current = getSavedIds().map { it.toString() }.toMutableSet()
        current.remove(recipeId.toString())
        prefs.edit { putStringSet(KEY_SAVED_IDS, current) }
    }

    companion object {
        private const val KEY_SAVED_IDS = "saved_recipe_ids"
    }
}
