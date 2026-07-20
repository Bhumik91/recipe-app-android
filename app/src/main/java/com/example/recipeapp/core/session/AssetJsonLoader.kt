package com.example.recipeapp.core.session

import android.content.Context

class AssetJsonLoader(private val context: Context) {
    fun readJson(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}