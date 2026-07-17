package com.example.recipeapp.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {

    private const val SPOONACULAR_API_KEY = "c2767743e1f54f828fd0f5f5ce1428be"

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    val dummyJson: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        // auth bearer interceptor will be added later if needed globally .client()
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    private val spoonacularClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("apiKey", SPOONACULAR_API_KEY)
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