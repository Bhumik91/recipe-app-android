package com.example.recipeapp.features.auth.data

import com.example.recipeapp.features.auth.model.LoginRequest
import com.example.recipeapp.features.auth.model.LoginResponse
import com.example.recipeapp.features.auth.model.RefreshTokenRequest
import com.example.recipeapp.features.auth.model.RefreshTokenResponse
import com.example.recipeapp.features.auth.model.UserDetailsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

    @GET("auth/me")
    suspend fun getCurrentUser(): UserDetailsDto
}