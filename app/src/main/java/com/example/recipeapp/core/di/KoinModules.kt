package com.example.recipeapp.core.di

import com.example.recipeapp.core.network.RetrofitClient
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.data.AuthApiService
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.data.AuthRepositoryImpl
import com.example.recipeapp.features.auth.viewmodel.LoginViewModel
import com.example.recipeapp.features.auth.viewmodel.SignupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // SessionManager dependency (singleton)
    single { SessionManager(androidContext()) }

    // AuthApiService dependency (singleton)
    single { RetrofitClient.dummyJson.create(AuthApiService::class.java) }

    // AuthRepository dependency (singleton)
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel() }
}
