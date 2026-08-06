package com.example.recipeapp.domain.auth.repository

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.data.auth.LoginRequest
import com.example.recipeapp.data.auth.LoginResponse
import com.example.recipeapp.data.auth.UserDetailsDto

interface AuthRepository {
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
    suspend fun getCurrentUser(): NetworkResult<UserDetailsDto>
}
