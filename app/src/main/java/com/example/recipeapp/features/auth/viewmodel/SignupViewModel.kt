package com.example.recipeapp.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.base.AuthField
import com.example.recipeapp.core.base.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SignupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isTermsChecked: Boolean
    ) {
        val fieldErrors = mutableMapOf<AuthField, String>()

        if (name.isEmpty()) {
            fieldErrors[AuthField.Name] = "Name cannot be empty"
        }

        if (email.isEmpty()) {
            fieldErrors[AuthField.Email] = "Email cannot be empty"
        } else if (!isValidEmail(email)) {
            fieldErrors[AuthField.Email] = "Invalid email address"
        }

        if (password.isEmpty()) {
            fieldErrors[AuthField.Password] = "Password cannot be empty"
        }

        if (confirmPassword.isEmpty()) {
            fieldErrors[AuthField.ConfirmPassword] = "Confirm password cannot be empty"
        } else if (password != confirmPassword) {
            fieldErrors[AuthField.ConfirmPassword] = "Passwords do not match"
        }

        if (!isTermsChecked) {
            fieldErrors[AuthField.Terms] = "You must accept the terms and conditions"
        }

        if (fieldErrors.isNotEmpty()) {
            _uiState.value = UiState.Error("Validation failed", fieldErrors)
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(3000.milliseconds)
            _uiState.value = UiState.Success(Unit)
        }
    }

    companion object {
        fun isValidEmail(email: String): Boolean {
            return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}
