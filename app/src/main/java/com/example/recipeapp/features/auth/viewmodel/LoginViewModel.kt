package com.example.recipeapp.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.AuthField
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.model.LoginRequest
import com.example.recipeapp.features.auth.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository): ViewModel() {
    private val _uiState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val uiState: StateFlow<UiState<LoginResponse>> = _uiState

    fun login(userName: String, password: String) {
        val fieldErrors = mutableMapOf<AuthField, String>()
        if (userName.isEmpty()) {
            fieldErrors[AuthField.UserName] = "Username cannot be empty"
        }
        if (password.isEmpty()) {
            fieldErrors[AuthField.Password] = "Password cannot be empty"
        }
        if (fieldErrors.isNotEmpty()) {
            _uiState.value = UiState.Error("Validation failed", fieldErrors)
            return
        }

        val request = LoginRequest(userName, password)
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = repository.login(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
                is NetworkResult.Loading -> { }
            }
        }
    }
}

class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


