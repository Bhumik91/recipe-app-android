import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
}

// Secrets live in local.properties (gitignored, never committed) so the key
// isn't readable by anyone browsing the public repo. Each dev sets their own
// SPOONACULAR_API_KEY there; CI would supply it as an env var instead.
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun secret(name: String): String =
    (localProperties.getProperty(name) ?: System.getenv(name) ?: "").also {
        if (it.isEmpty()) {
            logger.warn("Warning: $name is not set in local.properties or the environment; Spoonacular calls will fail.")
        }
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

        buildConfigField("String", "SPOONACULAR_API_KEY", "\"${secret("SPOONACULAR_API_KEY")}\"")

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
        buildConfig = true
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.navigation.fragment)
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
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
