package com.example.recipeapp.ui.dashboard.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.storage.session.SessionStorage
import com.example.recipeapp.domain.auth.repository.AuthRepository
import com.example.recipeapp.data.auth.UserDetailsDto
import com.example.recipeapp.domain.recipe.repository.RecipeRepository
import com.example.recipeapp.data.recipes.uimodel.RecipeCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Profile screen.
 * Responsible for fetching user details and their saved recipes.
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionStorage,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserDetailsDto>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserDetailsDto>> = _uiState

    private val _recipesUiState = MutableStateFlow<UiState<List<RecipeCardUiModel>>>(UiState.Idle)
    val recipesUiState: StateFlow<UiState<List<RecipeCardUiModel>>> = _recipesUiState

    init {
        loadProfile()
    }

    /**
     * Fetches current user profile information from the repository.
     */
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = authRepository.getCurrentUser()) {
                is NetworkResult.Success -> _uiState.value = UiState.Success(result.data)
                is NetworkResult.Error -> _uiState.value = UiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    /**
     * Fetches recipes saved by the user.
     */
    fun loadSavedRecipes() {
        viewModelScope.launch {
            _recipesUiState.value = UiState.Loading
            when (val result = recipeRepository.getSavedRecipes()) {
                is NetworkResult.Success -> _recipesUiState.value = UiState.Success(result.data)
                is NetworkResult.Error -> _recipesUiState.value = UiState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    /**
     * Clears user session and logs out.
     */
    fun logout() {
        sessionManager.clearSession()
    }
}
