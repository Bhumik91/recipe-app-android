package com.example.recipeapp.core.di

import android.content.Context
import com.example.recipeapp.core.network.RetrofitClient
import com.example.recipeapp.features.auth.data.AuthApiService
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.data.AuthRepositoryImpl
import com.example.recipeapp.core.session.SessionManager
import kotlin.getValue

class AppContainer(private val context: Context) {
    private val authApi by lazy { RetrofitClient.dummyJson.create(AuthApiService::class.java) }
    val sessionManager: SessionManager by lazy { SessionManager(context) }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(sessionManager, authApi) }
}