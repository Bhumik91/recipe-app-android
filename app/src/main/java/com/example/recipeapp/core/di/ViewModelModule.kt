package com.example.recipeapp.core.di

import com.example.recipeapp.ui.auth.viewmodel.LoginViewModel
import com.example.recipeapp.ui.auth.viewmodel.SignupViewModel
import com.example.recipeapp.ui.dashboard.home.viewmodel.HomeViewModel
import com.example.recipeapp.ui.dashboard.profile.viewmodel.ProfileViewModel
import com.example.recipeapp.ui.dashboard.saved.viewmodel.SavedViewModel
import com.example.recipeapp.ui.recipeDetail.viewmodel.RecipeDetailViewModel
import com.example.recipeapp.ui.search.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { RecipeDetailViewModel(get()) }
    viewModel { SavedViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
}
