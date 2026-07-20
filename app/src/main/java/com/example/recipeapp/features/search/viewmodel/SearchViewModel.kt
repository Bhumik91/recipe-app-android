package com.example.recipeapp.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.RecentSearchesManager
import com.example.recipeapp.features.recipes.data.RecipeRepository
import kotlinx.coroutines.FlowPreview
import com.example.recipeapp.features.search.model.SearchRecipeUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val recipeRepository: RecipeRepository,
    private val recentSearchesManager: RecentSearchesManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<UiState<List<SearchRecipeUiModel>>>(UiState.Idle)
    val searchResults: StateFlow<UiState<List<SearchRecipeUiModel>>> = _searchResults

    private val _selectedDiets = MutableStateFlow<List<String>>(emptyList())
    val selectedDiets: StateFlow<List<String>> = _selectedDiets

    private val _recentSearches = MutableStateFlow<List<SearchRecipeUiModel>>(
        recentSearchesManager.getRecentSearches()
    )
    val recentSearches: StateFlow<List<SearchRecipeUiModel>> = _recentSearches

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _searchResults.value = UiState.Idle
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun applyDietFilter(diets: List<String>) {
        if (diets == _selectedDiets.value) return
        _selectedDiets.value = diets
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            viewModelScope.launch { performSearch(query) }
        }
    }

    private suspend fun performSearch(query: String) {
        _searchResults.value = UiState.Loading
        val diet = _selectedDiets.value.takeIf { it.isNotEmpty() }?.joinToString(",")
        when (val result = recipeRepository.searchRecipes(query, diet)) {
            is NetworkResult.Success -> {
                val mappedData = result.data.map { SearchRecipeUiModel(it.id, it.title, it.imageUrl) }
                _searchResults.value = UiState.Success(mappedData)
                recentSearchesManager.addRecentSearches(mappedData)
                _recentSearches.value = recentSearchesManager.getRecentSearches()
            }
            is NetworkResult.Error -> _searchResults.value = UiState.Error(result.message)
            is NetworkResult.Loading -> Unit
        }
    }


}
