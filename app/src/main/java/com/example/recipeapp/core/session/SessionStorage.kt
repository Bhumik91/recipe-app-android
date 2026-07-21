package com.example.recipeapp.core.session

import com.example.recipeapp.features.auth.model.LoginResponse

interface SessionStorage {
    var isLoggedIn: Boolean

    fun saveAuthSession(response: LoginResponse)

    fun getUserId(): Int
    fun getUserName(): String

    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun updateTokens(accessToken: String, refreshToken: String)

    fun clearSession()
}
