plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.recipeapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.recipeapp"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // Jetpack Navigation Fragment
    implementation(libs.androidx.navigation.fragment.ktx)
    // Jetpack Navigation UI (for ActionBar, BottomNavigationView, Drawers)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.koin.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
    //Kotlin Serialzation dependency
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
