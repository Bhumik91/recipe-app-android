package com.example.recipeapp.domain.auth.repository

import com.example.recipeapp.core.network.ApiErrorHandler
import com.example.recipeapp.core.network.AuthApiService
import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.data.auth.LoginRequest
import com.example.recipeapp.data.auth.LoginResponse
import com.example.recipeapp.data.auth.UserDetailsDto
import com.example.recipeapp.storage.session.SessionStorage

class AuthRepositoryImpl(
    private val sessionManager: SessionStorage,
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