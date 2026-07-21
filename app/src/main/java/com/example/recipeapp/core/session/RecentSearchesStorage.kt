package com.example.recipeapp.core.session

import com.example.recipeapp.features.search.model.SearchRecipeUiModel

interface RecentSearchesStorage {
    fun getRecentSearches(): List<SearchRecipeUiModel>
    fun addRecentSearches(recipes: List<SearchRecipeUiModel>)
    fun clear()
}
