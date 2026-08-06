package com.example.recipeapp.storage.recentsearches

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.recipeapp.data.recipes.uimodel.SearchRecipeUiModel
import com.example.recipeapp.storage.session.SessionStorage
import kotlinx.serialization.json.Json

/**
 * Persists the actual recipes the user has previously searched (not just the raw
 * query text), so returning to the search screen can show real dish cards instead
 * of plain text chips.
 *
 * Resolves the userId-suffixed prefs file from SessionStorage on every access rather than
 * once at construction — this storage is a Koin singleton that outlives any single logged-in
 * session, so a userId baked into the constructor would still point at the previous user's
 * file after a logout/login switch within the same process.
 */
class SharedPrefRecentSearchesStorage(
    private val context: Context,
    private val sessionStorage: SessionStorage
) : RecentSearchesStorage {

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences("recent_searches_${sessionStorage.getUserId()}", Context.MODE_PRIVATE)

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
