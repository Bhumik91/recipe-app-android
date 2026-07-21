package com.example.recipeapp.storage.recentsearches

import com.example.recipeapp.data.recipes.uimodel.SearchRecipeUiModel

interface RecentSearchesStorage {
    fun getRecentSearches(): List<SearchRecipeUiModel>
    fun addRecentSearches(recipes: List<SearchRecipeUiModel>)
    fun clear()
}
