package com.example.recipeapp.features.dashboard.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.model.UserDetailsDto
import com.example.recipeapp.features.recipes.data.RecipeRepository
import com.example.recipeapp.features.recipes.model.RecipeCardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Profile screen.
 * Responsible for fetching user details and their saved recipes.
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserDetailsDto>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserDetailsDto>> = _uiState

    private val _recipesUiState = MutableStateFlow<UiState<List<RecipeCardUiModel>>>(UiState.Idle)
    val recipesUiState: StateFlow<UiState<List<RecipeCardUiModel>>> = _recipesUiState

    init {
        loadProfile()
        loadSavedRecipes()
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
