// Module-level build file for the SongPaper app

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.example.songpaper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.songpaper"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/*")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")

    // Lifecycle & Flow
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Palette for colors
    implementation("androidx.palette:palette-ktx:1.0.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Media & Wallpaper
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}

kapt {
    correctErrorTypes = true
}
