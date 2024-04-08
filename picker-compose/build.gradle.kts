import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.mavenPublishVanniktech)
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    // Android
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        publishLibraryVariants("release")
    }

    // JVM / Desktop
    jvmToolchain(17)
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    // Wasm
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "PickerComposeKt"
        browser()
    }

    // iOS / macOS
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "PickerComposeKt"
            isStatic = true
        }
    }

    sourceSets {
        val wasmJsMain by getting

        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)

            // Picker Core
            api(projects.pickerCore)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain.get())
        }

        nativeMain.get().dependsOn(nonAndroidMain)
        jvmMain.get().dependsOn(nonAndroidMain)
        wasmJsMain.dependsOn(nonAndroidMain)
    }
}

android {
    namespace = "io.github.vinceglb.picker.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}