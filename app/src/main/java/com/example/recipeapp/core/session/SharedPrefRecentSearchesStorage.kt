package com.example.recipeapp.core.session

import android.content.Context
import androidx.core.content.edit
import com.example.recipeapp.features.search.model.SearchRecipeUiModel
import kotlinx.serialization.json.Json

/**
 * Persists the actual recipes the user has previously searched (not just the raw
 * query text), so returning to the search screen can show real dish cards instead
 * of plain text chips.
 */
class SharedPrefRecentSearchesStorage(context: Context, userId: String) : RecentSearchesStorage {

    private val prefs = context.getSharedPreferences("recent_searches_$userId", Context.MODE_PRIVATE)

    override fun getRecentSearches(): List<SearchRecipeUiModel> {
        val raw = prefs.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<SearchRecipeUiModel>>(raw) }.getOrDefault(emptyList())
    }

    /**
     * Adds/moves the given recipes to the front of the recent list, de-duplicated by id,
     * capped at [MAX_RECENT_SEARCHES].
     */
    override fun addRecentSearches(recipes: List<SearchRecipeUiModel>) {
        if (recipes.isEmpty()) return

        val newIds = recipes.map { it.id }.toSet()
        val updated = recipes + getRecentSearches().filterNot { it.id in newIds }
        val trimmed = updated.take(MAX_RECENT_SEARCHES)

        prefs.edit { putString(KEY_RECENT_SEARCHES, json.encodeToString(trimmed)) }
    }

    override fun clear() {
        prefs.edit { remove(KEY_RECENT_SEARCHES) }
    }

    companion object {
        private const val KEY_RECENT_SEARCHES = "recent_search_recipes"
        private const val MAX_RECENT_SEARCHES = 10
        private val json = Json { ignoreUnknownKeys = true }
    }
}
