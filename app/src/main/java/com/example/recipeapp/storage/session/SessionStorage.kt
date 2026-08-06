package com.example.recipeapp.storage.session

import com.example.recipeapp.data.auth.LoginResponse

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
