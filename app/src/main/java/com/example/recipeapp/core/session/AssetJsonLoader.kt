package com.example.recipeapp.core.session

interface AssetJsonLoader {
    fun readJson(fileName: String): String
}
