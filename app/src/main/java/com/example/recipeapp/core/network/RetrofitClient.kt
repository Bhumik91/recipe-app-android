package com.example.recipeapp.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {

    private val SPOONACULAR_API_KEYS = arrayOf(
        "c2767743e1f54f828fd0f5f5ce1428be",
        "65c419295e3e43509b01d5a7720f3e43",
        "ed46c147ed734413b3b10e16a8fa0b93",
        "7355913421ea473d9889c7c50442c78a"
    )
    private const val SPOONACULAR_API_KEY_INDEX = 2

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    // Plain instance with no interceptor/authenticator — used only by TokenAuthenticator
    // to perform the refresh call itself without re-triggering authentication.
    val dummyJsonPlain: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    // Built per-client so the Bearer token interceptor + auto-refresh authenticator
    // (which both need SessionManager) can be Koin-injected instead of hardcoded here.
    fun dummyJson(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    private val spoonacularClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("apiKey", SPOONACULAR_API_KEYS[SPOONACULAR_API_KEY_INDEX])
                .build()
            chain.proceed(original.newBuilder().url(url).build())
        }
        .build()

    val spoonacular: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .client(spoonacularClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}