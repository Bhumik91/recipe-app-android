package com.example.recipeapp.core.session

import android.content.Context

class AndroidAssetJsonLoader(private val context: Context) : AssetJsonLoader {
    override fun readJson(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
