@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMPLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "th.skylabmek.kmp_frontend.core.network"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "networkKit"

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = xcfName
        }
    }

    jvm()
    js {
        browser()
    }
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":shared-resources"))

                //Data Local
                implementation(project(":core:data-local"))

                //Koin
                implementation(libs.koin.core)

                //Network - ktor
                api(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlin.stdlib)
                
                // compose
                implementation(libs.bundles.composeMaterial3)
                api(libs.compose.components.resources)

                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.koin.test)
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)
            }
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
