package com.example.recipeapp.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    val dummyJson: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        // auth bearer interceptor will be added later if needed globally .client()
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}