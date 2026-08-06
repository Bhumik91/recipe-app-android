# Notification Logging — Implementation Path

Room-backed persistence for the save/remove action history shown in `NotificationFragment`,
built on top of the already-committed local notification core (`d06cb17`: permission gate,
`NotificationChannels`, `RecipeNotifier`, deep-link routing in `DashboardActivity`).

## Architecture

```
User taps Save/Remove (Home explore list, RecipeDetail, or Saved tab)
        │
        ▼
RecipeRepository.toggleSavedRecipe(id, name, imageUrl)
RecipeRepository.removeSavedRecipe(id, name, imageUrl)
        │
        ▼
SharedPrefSavedRecipesStorage
        ├─→ SharedPreferences: update saved-id set (unchanged)
        ├─→ RecipeNotifier: post system status-bar notification (if POST_NOTIFICATIONS granted)
        └─→ NotificationLogRepository.log(...): persist to Room, via an app-scoped
            CoroutineScope so the caller (a RecyclerView click) is never blocked.
            Independent of notification permission — it's an in-app history.
        │
        ▼
NotificationLogDao.getLogsForUser(userId) — Flow<List<NotificationLogEntity>>
        │
        ▼
NotificationFragment / NotificationViewModel (not yet implemented — next commit)
        All / Saved / Removed tabs filter this same Flow client-side.
```

## Files added

| File | Purpose |
|---|---|
| `storage/notificationlog/NotificationLogEntity.kt` | Room `@Entity` (`notification_logs` table) + `RecipeActionConverter` (enum ⇄ String) |
| `storage/notificationlog/NotificationLogDao.kt` | `insert()`, reactive `getLogsForUser(userId): Flow<List<...>>` |
| `storage/notificationlog/NotificationLogRepository.kt` | Interface — `log(...)`, `observeLogs()`, scoped per logged-in user |
| `storage/notificationlog/RoomNotificationLogRepository.kt` | Room-backed impl |
| `core/db/AppDatabase.kt` | Room database, single entity, `exportSchema = false`, `fallbackToDestructiveMigration(true)` |
| `docs/notification-logging-implementation.md` | This file |

## Files modified

| File | Change |
|---|---|
| `storage/savedrecipes/SavedRecipesStorage.kt` | `removeSaved()` now also takes optional `recipeName`/`recipeImageUrl` |
| `storage/savedrecipes/SharedPrefSavedRecipesStorage.kt` | Constructor takes `NotificationLogRepository?` + `CoroutineScope?`; added private `notifyAndLog()` shared by `toggleSaved()` and `removeSaved()` — previously `removeSaved()` (the Saved tab's remove button) posted no notification and had no log entry at all |
| `core/notifications/RecipeNotifier.kt` | Notification body text: `"$name added to Saved Recipes."` / `"$name removed from Saved Recipes."` |
| `domain/recipe/repository/RecipeRepository.kt` | `toggleSavedRecipe`/`removeSavedRecipe` gained optional `recipeName`/`recipeImageUrl` params |
| `domain/recipe/repository/RemoteRecipeRepositoryImpl.kt`, `DummyRecipeRepositoryImpl.kt`, `FallbackRecipeRepository.kt` | Pass the new params through to storage |
| `ui/dashboard/home/viewmodel/HomeViewModel.kt` | `onSaveToggled()` looks up the tapped item's `title`/`imageUrl` from `exploreItems` and passes them along |
| `ui/recipeDetail/viewmodel/RecipeDetailViewModel.kt` | `onSaveToggled()` passes `title`/`imageUrl` from the loaded detail |
| `ui/dashboard/saved/viewmodel/SavedViewModel.kt` | `removeBookmark()` passes `recipe.title`/`recipe.imageUrl` to `removeSavedRecipe` |
| `core/di/StorageModule.kt` | Registers app-level `CoroutineScope`, `AppDatabase`, `NotificationLogDao`, `NotificationLogRepository`; injects the last two into `SharedPrefSavedRecipesStorage` |
| `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts` | Room + KSP dependencies (see chat for exact coordinates) |

## Commit 3: Completed (UI + ViewModel)

✅ `NotificationViewModel` — collects `NotificationLogRepository.observeLogs()`, exposes `StateFlow<UiState<>>`, tab selection via `selectTab()` + `getFilteredLogs()`

✅ `NotificationLogAdapter` — smart date/time formatting:
   - Today → `"Today, HH:MM"`
   - Yesterday → `"Yesterday, HH:MM"`
   - Within 7 days → `"Monday, HH:MM"` (weekday name)
   - After 7 days → `"DD-MM, HH:MM"`

✅ `item_notification_log.xml` — MaterialCardView with:
   - Recipe name (Poppins Semi Bold, 14sp, black)
   - Action message: `"<name> added/removed from Saved Recipes."`
   - Date/time (12sp)
   - Top-right badge: ic_saved_outlined (primary color on primary_40) or ic_trashed_outlined (warning color on warning_light)

✅ `fragment_notification.xml` — matches Profile tab design:
   - AppBarLayout with toolbar title "Notifications"
   - All / Saved / Removed tabs (same bg_recipe_detail_tab_indicator style)
   - RecyclerView (vertical, with item decoration spacing)
   - Empty state (ic_notification_outlined + text)

✅ `NotificationFragment` — wired:
   - ViewModel injection via Koin
   - RecyclerView adapter setup
   - Tab listener → updates ViewModel selection → filters adapter list
   - Flow observation with `repeatOnLifecycle`
   - Scroll listener for bottom-bar hide/show
   - Navigation on item click → `RecipeDetailActivity`

✅ DI — registered `NotificationViewModel(notificationLogRepository)` in `viewModelModule`

✅ Strings — added `notification_title`, `notification_tab_*`, `notification_empty_*`
