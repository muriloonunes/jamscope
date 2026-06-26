// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.devtools.ksp") version "2.3.9" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    id("com.mikepenz.aboutlibraries.plugin.android") version "15.0.0" apply false
    id("androidx.room") version "2.8.4" apply false
    id("com.autonomousapps.dependency-analysis") version "3.16.0"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
    }
}