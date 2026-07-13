package com.example.recipeapp.features.auth.data

import com.example.recipeapp.features.auth.model.LoginRequest
import com.example.recipeapp.features.auth.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}