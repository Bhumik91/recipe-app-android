package com.example.recipeapp.core.di

import org.koin.dsl.module

val appModule = module {
    includes(
        networkModule,
        storageModule,
        repositoryModule,
        viewModelModule
    )
}
