import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm()

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.ktor.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            
            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)
            
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "ai.toolio.app.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}