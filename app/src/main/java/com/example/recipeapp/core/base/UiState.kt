package com.example.recipeapp.core.base

enum class AuthField {
    UserName,
    Password,
    Name,
    Email,
    ConfirmPassword,
    Terms
}


sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message: String,
        val fieldErrors: Map<AuthField, String> = emptyMap()
    ) : UiState<Nothing>()
}