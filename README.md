<div align="center">

# 🍲 Recipe App - Showcase Mobile Application

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)
![Koin](https://img.shields.io/badge/DI-Koin-orange?style=for-the-badge)

*A showcase mobile application built to demonstrate modern Android development practices using Kotlin, XML Widgets, and MVVM Architecture.*

</div>

## 📖 About the Project

The **Recipe App** focuses on implementing real-world mobile application features such as authentication, API integration, advanced searching & filtering, data handling, and robust UI state management.

This project is part of a Mobile Development learning program and is intended to showcase clean architecture, scalable code organization, and best-in-class Android development practices.

---

## ✨ Key Features

- **Authentication & User Profiles**: Login/signup against DummyJSON, with auto-refreshing session tokens and secure (Keystore-backed) session storage.
- **Discover & Search**: Browse curated lists of recipes, and use advanced search with interactive **Diet & Cuisine Filters** (via Bottom Sheets & Chips), plus recent-search history.
- **Recipe Detail**: Full recipe view — ingredients, steps, nutrition/diet info.
- **Saved Recipes**: Save/remove favorite recipes locally (Room-backed), per logged-in user, with undo-on-remove.
- **Notifications**: Local notifications on save/remove, with an in-app log (All/Saved/Removed tabs).
- **Offline Support & Dummy Data Fallback**: If the Spoonacular API is unreachable or its quota is exhausted (402), the app transparently falls back to bundled dummy recipe data so the UI never goes empty.
- **Robust Error Handling**: Centralized error state UI for clean and standardized error recovery across screens.
- **Add Recipe (Coming Soon)**: The Add button (FAB) on the dashboard is a placeholder — tapping it shows a "Coming soon" toast. The real add-recipe flow hasn't been designed yet.
- **Smooth Navigation**: Managed via the Jetpack Navigation component with a Bottom Navigation Bar + Bottom App Bar.

---

## 🛠 Tech Stack & Libraries

| Category          | Library                                                                                                                | Version        |
|-------------------|------------------------------------------------------------------------------------------------------------------------|----------------|
| Language          | [Kotlin](https://kotlinlang.org/)                                                                                      | —              |
| UI                | XML Layouts + [Material Components](https://github.com/material-components/material-components-android)                | 1.14.0         |
| Architecture      | MVVM + Clean Architecture (UI → Domain → Data)                                                                         | —              |
| DI                | [Koin](https://insert-koin.io/) (`koin-android`)                                                                       | 4.2.2          |
| Networking        | [Retrofit](https://square.github.io/retrofit/) + OkHttp                                                                | 3.0.0          |
| Serialization     | [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) + `retrofit2-kotlinx-serialization-converter` | 1.11.0         |
| Local persistence | [Room](https://developer.android.com/training/data-storage/room) (saved recipes, notification log)                     | 2.8.4          |
| Secure storage    | `EncryptedSharedPreferences` (Android Keystore) for session tokens                                                     | —              |
| Async / reactive  | Kotlin Coroutines + `StateFlow`                                                                                        | —              |
| Image loading     | [Coil](https://coil-kt.github.io/coil/) 3                                                                              | 3.5.0          |
| Navigation        | [Jetpack Navigation Component](https://developer.android.com/guide/navigation) (Safe Args)                             | 2.9.8          |
| Build             | AGP / KSP                                                                                                              | 9.2.1 / 2.3.10 |

Full dependency list with exact versions: [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## 🌐 Data Sources

| API | Used for | Notes |
|---|---|---|
| [DummyJSON](https://dummyjson.com/) | Auth (login/signup, token refresh) | `AuthApiService`, wired through `AuthInterceptor` + `TokenAuthenticator` for auto-refresh on 401 |
| [Spoonacular](https://spoonacular.com/food-api) | Recipe search, detail, diet/cuisine data | `RecipeApiService`. Requires your own API key — see [Setup](#-getting-started) |
| Bundled JSON assets | Fallback recipe data | Loaded via `AssetJsonLoader` when Spoonacular returns 402 (quota) or the network is down, so the app is still usable offline/without a key |

---

## 🏗 Architecture

MVVM + Clean Architecture, with dependency injection (KOIN) wiring it all together.

```
UI (Fragments/Activities)
   │  observes StateFlow
   ▼
ViewModel            — business logic, formats UiState (Loading / Success / Error)
   │  calls
   ▼
Repository (domain)  — e.g. RecipeRepository, AuthRepository
   │  delegates to
   ▼
Data sources         — Retrofit (remote) · Room / SharedPreferences / Keystore (local)
```

- `RecipeRepository` is itself a **Fallback** repository: it wraps a remote (Spoonacular) impl and a dummy-data impl, so every ViewModel gets remote-first-with-fallback for free without knowing about the fallback logic.
- KOIN modules (`app/src/main/java/com/example/recipeapp/core/di/`) are split by concern: `NetworkModule`, `StorageModule`, `RepositoryModule`, `ViewModelModule`, composed into `appModule`.

---

## 📁 Project Structure

```
app/src/main/java/com/example/recipeapp/
├── ui/                     # Screens (Fragments/Activities) + ViewModels, grouped by feature
│   ├── auth/                  # Login / Signup
│   ├── onboarding/
│   ├── dashboard/              # Hosts the bottom-nav tabs
│   │   ├── home/                  # Explore / recipe feed
│   │   ├── search/                 # (see ui/search below)
│   │   ├── saved/                  # Saved recipes list
│   │   ├── profile/                # User profile
│   │   └── notification/           # Notification log (All/Saved/Removed)
│   ├── search/                 # Search + diet/cuisine filters
│   └── recipeDetail/           # Recipe detail screen
├── domain/                 # Repository interfaces + impls (business rules, UIKit/Android-agnostic)
│   ├── auth/
│   └── recipe/                 # RemoteRecipeRepositoryImpl, DummyRecipeRepositoryImpl, FallbackRecipeRepository
├── data/                    # DTOs, mappers, request option builders (network-shape ↔ UI-shape)
│   └── recipes/
├── storage/                 # Local persistence implementations
│   ├── session/                # KeystoreSessionStorage
│   ├── savedrecipes/            # Room-backed
│   ├── notificationlog/         # Room-backed
│   ├── recentsearches/          # SharedPreferences-backed
│   └── assets/                  # Bundled dummy-data JSON loader
├── core/
│   ├── di/                     # Koin modules
│   ├── network/                 # RetrofitClient, interceptors, authenticator, API services
│   ├── db/                      # Room AppDatabase
│   ├── notifications/            # Local notification posting
│   ├── permissions/              # Runtime permission handling
│   └── base/                     # Shared base classes
├── models/                  # Plain domain/UI models
└── common/                  # Cross-cutting UI helpers: error views, filters, gestures, popups, toasts
```

---

## 📸 Screenshots

<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/54e49cb9-6ae0-48ce-8e0b-b646f9b1fbcb" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/00ffe3a5-809c-48b0-bb16-ab8359496ca7" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/a6703df2-bdd7-40c9-beab-2b3ba6bc27e5" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/334f705f-c166-4e16-9617-bb0bb53e6ec2" />
<br><br>
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/599ce586-196f-4500-bd96-63b4cce0c048" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/9ae45998-ea69-409b-ba36-e5e73790a84b" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/a937011d-c37b-4375-a7fb-62bdf57e4dbf" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img width="184" height="389" alt="Image" src="https://github.com/user-attachments/assets/f00e72f4-787f-45f5-b877-bfc842da6bbd" />

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Giraffe or newer recommended)
- **JDK 17**
- A free [Spoonacular API](https://spoonacular.com/food-api) key

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Bhumik91/recipe-demo-android.git
   ```
2. Open the project in Android Studio.
3. Add your Spoonacular API key to your local (gitignored) `local.properties`:
   ```
   SPOONACULAR_API_KEY=your_key_here
   ```
   It's read at build time into `BuildConfig.SPOONACULAR_API_KEY` (see `app/build.gradle.kts`) and never committed to the repo. Without it, calls to Spoonacular return 401/402 and the app falls back to bundled dummy recipe data.
4. Build the project and let Gradle download the required dependencies.
5. Run the app on an emulator or a physical Android device.

---

## 🤝 Contributing

This project is a showcase piece, but contributions, suggestions, and feedback are always welcome! Feel free to open an issue or submit a pull request.
a
