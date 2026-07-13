package com.example.recipeapp

import android.app.Application
import com.example.recipeapp.core.di.AppContainer

class App : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
