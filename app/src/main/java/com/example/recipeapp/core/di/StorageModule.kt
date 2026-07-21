package com.example.recipeapp.core.di

import com.example.recipeapp.storage.assets.AndroidAssetJsonLoader
import com.example.recipeapp.storage.assets.AssetJsonLoader
import com.example.recipeapp.storage.recentsearches.RecentSearchesStorage
import com.example.recipeapp.storage.recentsearches.SharedPrefRecentSearchesStorage
import com.example.recipeapp.storage.savedrecipes.SavedRecipesStorage
import com.example.recipeapp.storage.savedrecipes.SharedPrefSavedRecipesStorage
import com.example.recipeapp.storage.session.SessionStorage
import com.example.recipeapp.storage.session.SharedPrefSessionStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    // SessionStorage dependency (singleton) — backed by SharedPreferences
    single<SessionStorage> { SharedPrefSessionStorage(androidContext()) }

    // AssetJsonLoader (singleton) — reads bundled dummy JSON used as a 402-quota fallback
    single<AssetJsonLoader> { AndroidAssetJsonLoader(androidContext()) }

    // SavedRecipesStorage (singleton, scoped per logged-in user) — backed by SharedPreferences
    single<SavedRecipesStorage> { SharedPrefSavedRecipesStorage(androidContext(), get<SessionStorage>().getUserId().toString()) }

    // RecentSearchesStorage (singleton, scoped per logged-in user) — backed by SharedPreferences
    single<RecentSearchesStorage> { SharedPrefRecentSearchesStorage(androidContext(), get<SessionStorage>().getUserId().toString()) }
}
