package com.example.recipeapp.core.network

import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.data.AuthApiService
import com.example.recipeapp.features.auth.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * [refreshApiService] must be built from a client with no [AuthInterceptor]/authenticator of its
 * own — reusing the authenticated client here would re-trigger this authenticator on the refresh
 * call itself.
 */
class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val refreshApiService: AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) > MAX_RETRY_COUNT) return null

        val refreshToken = sessionManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) return null

        val newTokens = runBlocking {
            runCatching { refreshApiService.refreshToken(RefreshTokenRequest(refreshToken)) }.getOrNull()
        } ?: return null

        sessionManager.updateTokens(newTokens.accessToken, newTokens.refreshToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
