package com.example.recipeapp.core.network

import com.example.recipeapp.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

object RetrofitClient {

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    // Plain instance with no interceptor/authenticator — used only by TokenAuthenticator
    // to perform the refresh call itself without re-triggering authentication.
    val dummyJsonPlain: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    // Built per-client so the Bearer token interceptor + auto-refresh authenticator
    // (which both need SessionStorage) can be Koin-injected instead of hardcoded here.
    fun dummyJson(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    private val spoonacularClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("apiKey", BuildConfig.SPOONACULAR_API_KEY)
                .build()
            // On quota exhaustion (402), this propagates up to FallbackRecipeRepository,
            // which swaps in the bundled dummy recipe data.
            chain.proceed(original.newBuilder().url(url).build())
        }
        .build()

    val spoonacular: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .client(spoonacularClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}