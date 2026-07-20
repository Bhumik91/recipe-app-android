package com.example.recipeapp.features.recipeDetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.recipes.data.RecipeRepository
import com.example.recipeapp.features.recipes.model.RecipeDetailUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<RecipeDetailUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<RecipeDetailUiModel>> = _uiState

    private val _targetServings = MutableStateFlow(MIN_SERVINGS)
    val targetServings: StateFlow<Int> = _targetServings

    fun loadRecipeDetail(recipeId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = recipeRepository.getRecipeDetail(recipeId)) {
                is NetworkResult.Success -> {
                    _uiState.value = UiState.Success(result.data)
                    _targetServings.value = result.data.servings.coerceAtLeast(MIN_SERVINGS)
                }
                is NetworkResult.Error -> _uiState.value = UiState.Error(result.message)
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun incrementServings() {
        _targetServings.value = (_targetServings.value + 1).coerceAtMost(MAX_SERVINGS)
    }

    fun decrementServings() {
        _targetServings.value = (_targetServings.value - 1).coerceAtLeast(MIN_SERVINGS)
    }

    fun onSaveToggled() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        recipeRepository.toggleSavedRecipe(currentState.data.id)
        _uiState.value = UiState.Success(currentState.data.copy(isSaved = !currentState.data.isSaved))
    }

    private companion object {
        const val MIN_SERVINGS = 1
        const val MAX_SERVINGS = 10
    }
}
