package com.example.recipeapp.core.di

import com.example.recipeapp.core.network.AuthInterceptor
import com.example.recipeapp.core.network.RetrofitClient
import com.example.recipeapp.core.network.TokenAuthenticator
import com.example.recipeapp.core.network.AuthApiService
import com.example.recipeapp.storage.session.SessionStorage
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val REFRESH_API_QUALIFIER = named("refreshAuthApi")

val networkModule = module {
    // Plain AuthApiService (no interceptor/authenticator) — used only by TokenAuthenticator
    // to perform the refresh call itself without re-triggering authentication.
    single(REFRESH_API_QUALIFIER) { RetrofitClient.dummyJsonPlain.create(AuthApiService::class.java) }

    single { AuthInterceptor(get<SessionStorage>()) }
    single { TokenAuthenticator(get<SessionStorage>(), get(REFRESH_API_QUALIFIER)) }

    // OkHttpClient for dummyJson — attaches the Bearer token and auto-refreshes it on 401
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    // AuthApiService dependency (singleton)
    single { RetrofitClient.dummyJson(get()).create(AuthApiService::class.java) }
}
