package com.example.recipeapp.core.network

import com.example.recipeapp.data.auth.LoginRequest
import com.example.recipeapp.data.auth.LoginResponse
import com.example.recipeapp.data.auth.RefreshTokenRequest
import com.example.recipeapp.data.auth.RefreshTokenResponse
import com.example.recipeapp.data.auth.UserDetailsDto
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