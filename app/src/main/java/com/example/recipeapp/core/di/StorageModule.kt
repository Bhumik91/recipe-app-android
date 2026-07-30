package com.example.recipeapp.core.di

import com.example.recipeapp.storage.assets.AndroidAssetJsonLoader
import com.example.recipeapp.storage.assets.AssetJsonLoader
import com.example.recipeapp.storage.recentsearches.RecentSearchesStorage
import com.example.recipeapp.storage.recentsearches.SharedPrefRecentSearchesStorage
import com.example.recipeapp.storage.savedrecipes.SavedRecipeDao
import com.example.recipeapp.storage.savedrecipes.SavedRecipesStorage
import com.example.recipeapp.storage.savedrecipes.RoomSavedRecipesStorage
import com.example.recipeapp.storage.session.SessionStorage
import com.example.recipeapp.storage.session.KeystoreSessionStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

import com.example.recipeapp.core.permissions.PermissionManager
import com.example.recipeapp.core.permissions.PermissionManagerImpl
import com.example.recipeapp.core.notifications.RecipeNotifier
import com.example.recipeapp.core.notifications.SystemRecipeNotifier
import com.example.recipeapp.core.db.AppDatabase
import com.example.recipeapp.storage.notificationlog.NotificationLogDao
import com.example.recipeapp.storage.notificationlog.NotificationLogRepository
import com.example.recipeapp.storage.notificationlog.RoomNotificationLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val storageModule = module {
    // SessionStorage dependency (singleton) — backed by EncryptedSharedPreferences (Android Keystore)
    single<SessionStorage> { KeystoreSessionStorage(androidContext()) }

    // AssetJsonLoader (singleton) — reads bundled dummy JSON used as a 402-quota fallback
    single<AssetJsonLoader> { AndroidAssetJsonLoader(androidContext()) }

    single<PermissionManager> { PermissionManagerImpl(androidContext()) }
    single<RecipeNotifier> { SystemRecipeNotifier(androidContext(), get()) }

    // App-lifetime scope for storage-layer work that must outlive a single UI event
    // (e.g. writing a notification-log row without blocking the caller).
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    // Room (singleton) — backs the persisted save/remove history shown in NotificationFragment
    single { AppDatabase.build(androidContext()) }
    single<NotificationLogDao> { get<AppDatabase>().notificationLogDao() }
    single<NotificationLogRepository> {
        RoomNotificationLogRepository(get(), get<SessionStorage>())
    }

    // SavedRecipesStorage (singleton) — backed by Room, reads the current userId from
    // SessionStorage per call so it stays correct across a logout/login switch (see
    // RoomSavedRecipesStorage's kdoc for why a fixed userId here would leak between users)
    single<SavedRecipeDao> { get<AppDatabase>().savedRecipeDao() }
    single<SavedRecipesStorage> {
        RoomSavedRecipesStorage(
            get<SavedRecipeDao>(),
            get<SessionStorage>(),
            get<RecipeNotifier>(),
            get<NotificationLogRepository>(),
            get<CoroutineScope>()
        )
    }

    // RecentSearchesStorage (singleton) — backed by SharedPreferences, reads the current
    // userId from SessionStorage per call so it stays correct across a logout/login switch
    single<RecentSearchesStorage> { SharedPrefRecentSearchesStorage(androidContext(), get<SessionStorage>()) }
}
