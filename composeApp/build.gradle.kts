import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods {
        version = "2.2.0"
        summary = "Common UI for Toolio"
        homepage = "https://github.com/ratface1986/toolio_app"
        ios.deploymentTarget = "17.6.1"

        pod("FirebaseAuth") {
            version = "11.15.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GoogleSignIn") {
            version = "8.0.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("RevenueCat") {
            version = "5.31"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.billing)
            implementation(libs.firebase.auth.ktx)
            implementation(libs.play.services.auth)
            implementation(libs.purchases)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.ui)
            implementation(libs.uuid)
            implementation(libs.ui.graphics)
            implementation(libs.kotlinx.serialization.json.v163)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "ai.toolio.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    lint {
        // Отключаем проблемный детектор
        disable.add("NullSafeMutableLiveData")
        // Можешь также отключить все фатальные ошибки Lint для релиза,
        // но это менее безопасно и может скрыть другие проблемы.
        // Это можно использовать как временное решение:
        // abortOnError = false
    }

    defaultConfig {
        applicationId = "ai.toolio.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 3
        versionName = "1.0.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}