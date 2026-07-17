package com.example.recipeapp.core.di

import com.example.recipeapp.core.network.RetrofitClient
import com.example.recipeapp.core.session.SavedRecipesManager
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.data.AuthApiService
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.data.AuthRepositoryImpl
import com.example.recipeapp.features.auth.viewmodel.LoginViewModel
import com.example.recipeapp.features.auth.viewmodel.SignupViewModel
import com.example.recipeapp.features.dashboard.home.viewmodel.HomeViewModel
import com.example.recipeapp.features.recipes.data.RecipeApiService
import com.example.recipeapp.features.recipes.data.RecipeRepository
import com.example.recipeapp.features.recipes.data.RecipeRepositoryImpl
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

    // RecipeApiService dependency (singleton)
    single { RetrofitClient.spoonacular.create(RecipeApiService::class.java) }

    // RecipeRepository dependency (singleton)
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get(), get()) }

    // SavedRecipesManager (singleton, scoped per logged-in user)
    single { SavedRecipesManager(androidContext(), get<SessionManager>().getUserId().toString()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel() }
    viewModel { HomeViewModel(get()) }
}
