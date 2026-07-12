package com.example.recipeapp.features.auth.data

import com.example.recipeapp.core.network.NetworkResult
import com.example.recipeapp.features.auth.model.LoginRequest
import com.example.recipeapp.features.auth.model.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
}