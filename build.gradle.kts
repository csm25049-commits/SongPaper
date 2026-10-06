// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("dagger.hilt.android.plugin") version "2.49" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.49")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
