package com.example.recipeapp.storage.assets

interface AssetJsonLoader {
    fun readJson(fileName: String): String
}
