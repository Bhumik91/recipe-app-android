package com.example.recipeapp.features.auth.data

import com.example.recipeapp.core.network.ApiErrorHandler
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.model.LoginRequest
import com.example.recipeapp.features.auth.model.LoginResponse
import com.example.recipeapp.features.auth.model.UserDetailsDto

class AuthRepositoryImpl(
    private val sessionManager: SessionManager,
    private val authApiService: AuthApiService
) : AuthRepository {
    override suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse> {
        val result = ApiErrorHandler.safeApiCall {
            authApiService.login(request)
        }
        if (result is NetworkResult.Success) {
            sessionManager.saveAuthSession(result.data)
        }
        return result
    }

    override suspend fun getCurrentUser(): NetworkResult<UserDetailsDto> =
        ApiErrorHandler.safeApiCall {
            authApiService.getCurrentUser()
        }
}