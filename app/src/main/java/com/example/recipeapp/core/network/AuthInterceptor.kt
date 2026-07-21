package com.example.recipeapp.core.network

import com.example.recipeapp.core.session.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionStorage) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sessionManager.getAccessToken()
        val original = chain.request()

        val request = if (accessToken.isNullOrBlank()) {
            original
        } else {
            original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }

        return chain.proceed(request)
    }
}
