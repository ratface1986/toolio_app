import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

kotlin {
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = "ToolioShared"
            freeCompilerArgs += listOf(
                "-Xexpect-actual-classes",
                "-Xbinary=bundleId=ai.toolio.app.shared"
            )
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = "ToolioShared"
            freeCompilerArgs += listOf(
                "-Xexpect-actual-classes",
                "-Xbinary=bundleId=ai.toolio.app.shared"
            )
        }
    }

    iosX64 {
        binaries.framework {
            baseName = "ToolioShared"
            freeCompilerArgs += listOf(
                "-Xexpect-actual-classes",
                "-Xbinary=bundleId=ai.toolio.app.shared"
            )
        }
    }

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
            implementation(libs.uuid)
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.skiko)
            implementation(libs.ui)
            implementation(libs.ui.graphics)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.skiko)
            implementation(libs.ui)
            implementation(libs.ui.graphics)
            implementation("androidx.core:core-ktx:1.16.0")
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.skiko)
            implementation(libs.ui)
            implementation(libs.ui.graphics)
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
    packaging {
        "**/libandroidx.graphics.path.so"
    }
}