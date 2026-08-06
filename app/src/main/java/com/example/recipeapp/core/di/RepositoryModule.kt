package com.example.recipeapp.core.di

import com.example.recipeapp.domain.auth.repository.AuthRepository
import com.example.recipeapp.domain.auth.repository.AuthRepositoryImpl
import com.example.recipeapp.domain.recipe.repository.DummyRecipeRepositoryImpl
import com.example.recipeapp.domain.recipe.repository.FallbackRecipeRepository
import com.example.recipeapp.core.network.RecipeApiService
import com.example.recipeapp.domain.recipe.repository.RecipeRepository
import com.example.recipeapp.domain.recipe.repository.RemoteRecipeRepositoryImpl
import com.example.recipeapp.core.network.RetrofitClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val REMOTE_RECIPE_REPO = named("remoteRecipeRepository")
private val DUMMY_RECIPE_REPO = named("dummyRecipeRepository")

val repositoryModule = module {
    // RecipeApiService dependency (singleton)
    single { RetrofitClient.spoonacular.create(RecipeApiService::class.java) }

    // AuthRepository dependency (singleton)
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Qualified impls, wrapped by the unqualified FallbackRecipeRepository below — every
    // ViewModel resolves the unqualified RecipeRepository and automatically gets remote-first
    // with a dummy-data fallback on quota/network errors.
    single<RecipeRepository>(DUMMY_RECIPE_REPO) { DummyRecipeRepositoryImpl(get(), get(), get()) }
    single<RecipeRepository>(REMOTE_RECIPE_REPO) { RemoteRecipeRepositoryImpl(get(), get(), get()) }
    single<RecipeRepository> { FallbackRecipeRepository(get(REMOTE_RECIPE_REPO), get(DUMMY_RECIPE_REPO)) }
}
