package com.example.recipeapp.core.di

import com.example.recipeapp.core.network.AuthInterceptor
import com.example.recipeapp.core.network.RetrofitClient
import com.example.recipeapp.core.network.TokenAuthenticator
import com.example.recipeapp.core.session.AssetJsonLoader
import com.example.recipeapp.core.session.RecentSearchesManager
import com.example.recipeapp.core.session.SavedRecipesManager
import com.example.recipeapp.core.session.SessionManager
import com.example.recipeapp.features.auth.data.AuthApiService
import com.example.recipeapp.features.auth.data.AuthRepository
import com.example.recipeapp.features.auth.data.AuthRepositoryImpl
import com.example.recipeapp.features.auth.viewmodel.LoginViewModel
import com.example.recipeapp.features.auth.viewmodel.SignupViewModel
import com.example.recipeapp.features.dashboard.home.viewmodel.HomeViewModel
import com.example.recipeapp.features.recipeDetail.viewmodel.RecipeDetailViewModel
import com.example.recipeapp.features.dashboard.saved.viewmodel.SavedViewModel
import com.example.recipeapp.features.recipes.data.DummyRecipeRepositoryImpl
import com.example.recipeapp.features.recipes.data.FallbackRecipeRepository
import com.example.recipeapp.features.recipes.data.RecipeApiService
import com.example.recipeapp.features.recipes.data.RecipeRepository
import com.example.recipeapp.features.recipes.data.RemoteRecipeRepositoryImpl
import com.example.recipeapp.features.search.viewmodel.SearchViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val REFRESH_API_QUALIFIER = named("refreshAuthApi")
private val REMOTE_RECIPE_REPO = named("remoteRecipeRepository")
private val DUMMY_RECIPE_REPO = named("dummyRecipeRepository")

val appModule = module {
    // SessionManager dependency (singleton)
    single { SessionManager(androidContext()) }

    // Plain AuthApiService (no interceptor/authenticator) — used only by TokenAuthenticator
    // to perform the refresh call itself without re-triggering authentication.
    single(REFRESH_API_QUALIFIER) { RetrofitClient.dummyJsonPlain.create(AuthApiService::class.java) }

    single { AuthInterceptor(get()) }
    single { TokenAuthenticator(get(), get(REFRESH_API_QUALIFIER)) }

    // OkHttpClient for dummyJson — attaches the Bearer token and auto-refreshes it on 401
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    // AuthApiService dependency (singleton)
    single { RetrofitClient.dummyJson(get()).create(AuthApiService::class.java) }

    // AuthRepository dependency (singleton)
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // RecipeApiService dependency (singleton)
    single { RetrofitClient.spoonacular.create(RecipeApiService::class.java) }

    // AssetJsonLoader (singleton) — reads bundled dummy JSON used as a 402-quota fallback
    single { AssetJsonLoader(androidContext()) }

    // Manual switch: comment/uncomment to pick which impl every ViewModel's RecipeRepository resolves to.
//    single<RecipeRepository> { DummyRecipeRepositoryImpl(get(), get(), get()) }
    single<RecipeRepository> { RemoteRecipeRepositoryImpl(get(), get(), get()) }
//    single<RecipeRepository> { FallbackRecipeRepository(get(REMOTE_RECIPE_REPO), get(DUMMY_RECIPE_REPO)) }

    // SavedRecipesManager (singleton, scoped per logged-in user)
    single { SavedRecipesManager(androidContext(), get<SessionManager>().getUserId().toString()) }

    // RecentSearchesManager (singleton, scoped per logged-in user)
    single { RecentSearchesManager(androidContext(), get<SessionManager>().getUserId().toString()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { RecipeDetailViewModel(get()) }
    viewModel { SavedViewModel(get()) }
    viewModel { SearchViewModel(get(), get()) }
}
