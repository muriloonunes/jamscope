import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.20"
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.mikepenz.aboutlibraries.plugin.android")
    id("androidx.room")
    id("com.autonomousapps.dependency-analysis")
}

android {
    namespace = "com.mno.jamscope"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mno.jamscope"
        minSdk = 26
        targetSdk = 36
        versionCode = 14
        versionName = "1.3.0"

        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val releaseStoreFile: String =
                gradleLocalProperties(rootDir, providers).getProperty("RELEASE_STORE_FILE")
            val releaseStorePassword =
                gradleLocalProperties(rootDir, providers).getProperty("RELEASE_STORE_PASSWORD")
            val releaseKeyAlias =
                gradleLocalProperties(rootDir, providers).getProperty("RELEASE_KEY_ALIAS")
            val releaseKeyPassword =
                gradleLocalProperties(rootDir, providers).getProperty("RELEASE_KEY_PASSWORD")

            storeFile = File(releaseStoreFile)
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xlambdas=class")
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.register<Copy>("copyLicenseToRaw") {
    description = "Add the txt license file to the build apk"
    from(rootProject.file("LICENSE.txt"))
    into("src/main/res/raw")
    rename("LICENSE.txt", "gpl_license.txt")
}

tasks.named("preBuild") {
    dependsOn("copyLicenseToRaw")
}

dependencies {
    // Android Core & Utils
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.annotation)
    implementation(libs.guava)
    implementation(libs.okio)

    // Android Lifecycle
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.livedata.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // DataStore
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // Android UI / Compose Core
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.google.fonts)
    implementation(libs.androidx.recyclerview)
    implementation(libs.lazycolumnscrollbar)

    // Material
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.material)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)

    // JSON Serialization & Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    runtimeOnly(libs.kotlin.reflect)

    // Image Loading (Coil, Glide & Zoomable)
    implementation(libs.coil.compose)
    implementation(libs.coil.core)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil3.coil)
    runtimeOnly(libs.coil.gif)
    implementation(libs.glide)
    implementation(libs.zoomable)
    implementation(libs.zoomable.image)
    implementation(libs.zoomable.image.coil3)

    // Ktor Network
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.http)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    runtimeOnly(libs.ktor.server.core)

    // Hilt & Dependency Injection
    implementation(libs.dagger)
    implementation(libs.hilt.android)
    implementation(libs.hilt.core)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.javax.inject)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android.compiler)
    //todo: remover isso quando o dagger/hilt tiver suporte a jvm 2.4
    ksp("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")

    // Room & SQLite
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite)
    ksp(libs.androidx.room.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime)

    // Lottie Animations
    implementation(libs.lottie)
    implementation(libs.lottie.compose)

    // Glance Compose Widgets
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // About Libraries
    implementation(libs.aboutlibraries.compose.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.aboutlibraries.core)

    // Tests
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestRuntimeOnly(libs.androidx.test.core)
    androidTestRuntimeOnly(libs.androidx.test.runner)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugRuntimeOnly(libs.androidx.ui.test.manifest)

    // LeakCanary
    debugRuntimeOnly(libs.leakcanary.android)
}