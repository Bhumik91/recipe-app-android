package com.example.recipeapp.features.dashboard.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.dashboard.home.model.RecipeCardUiModel
import com.example.recipeapp.features.recipes.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class  HomeViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _exploreUiState = MutableStateFlow<UiState<List<RecipeCardUiModel>>>(UiState.Idle)
    val exploreUiState: StateFlow<UiState<List<RecipeCardUiModel>>> = _exploreUiState

    private val _savedUiState = MutableStateFlow<UiState<List<RecipeCardUiModel>>>(UiState.Idle)
    val savedUiState: StateFlow<UiState<List<RecipeCardUiModel>>> = _savedUiState

    private val cuisines: List<String> = recipeRepository.getCuisines()
    val userName: String = recipeRepository.getUserName()

    fun getCuisines(): List<String> = cuisines

    private val _selectedCuisine = MutableStateFlow<String?>(null)
    val selectedCuisine: StateFlow<String?> = _selectedCuisine

    private var currentOffset = 0
    private var currentCuisine: String? = null
    private var isLastPage = false
    private var isLoadingMore = false
    private val exploreItems = mutableListOf<RecipeCardUiModel>()

    fun toggleFilter(cuisine: String) {
        val newSelection = if (cuisine == "All") null else cuisine
        if (newSelection == _selectedCuisine.value) return
        _selectedCuisine.value = newSelection
        loadInitial(newSelection)
    }

    fun loadInitial(cuisine: String? = null) {
        currentOffset = 0
        currentCuisine = cuisine
        isLastPage = false
        exploreItems.clear()
        loadNextExplorePage()
        loadSavedRecipes()
    }

    fun loadNextExplorePage() {
        if (isLoadingMore || isLastPage) return
        isLoadingMore = true
        viewModelScope.launch {
            if (exploreItems.isEmpty()) {
                _exploreUiState.value = UiState.Loading
            }
            when (val result = recipeRepository.getExploreRecipes(currentCuisine, currentOffset)) {
                is NetworkResult.Success -> {
                    val page = result.data
                    exploreItems.addAll(page.results)
                    currentOffset += page.number
                    isLastPage = currentOffset >= page.totalResults
                    _exploreUiState.value = UiState.Success(exploreItems.toList())
                }
                is NetworkResult.Error -> {
                    _exploreUiState.value = UiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
            isLoadingMore = false
        }
    }

    fun loadSavedRecipes() {
        viewModelScope.launch {
            _savedUiState.value = UiState.Loading
            when (val result = recipeRepository.getSavedRecipes()) {
                is NetworkResult.Success -> {
                    _savedUiState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _savedUiState.value = UiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun onSaveToggled(recipeId: Int) {
        recipeRepository.toggleSavedRecipe(recipeId)
        // Flip the flag in the already-loaded Explore list — no refetch needed
        val updatedIndex = exploreItems.indexOfFirst { it.id == recipeId }
        if (updatedIndex != -1) {
            exploreItems[updatedIndex] = exploreItems[updatedIndex].copy(
                isSaved = !exploreItems[updatedIndex].isSaved
            )
            _exploreUiState.value = UiState.Success(exploreItems.toList())
        }
        // Saved section refetches — small list, cheap to reload
        loadSavedRecipes()
    }
}
