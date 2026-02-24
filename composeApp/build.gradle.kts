import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMPLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvmToolchain(21)
    
    androidLibrary {
        namespace = "th.skylabmek.kmp_frontend"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
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

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":navigation"))
                implementation(project(":ui"))
                implementation(project(":domain"))
                implementation(project(":shared-resources"))
                implementation(project(":core:network"))
                implementation(project(":core:common"))
                implementation(project(":core:data-local"))
                implementation(project(":features:feature:app"))
                implementation(project(":features:app_features:profile"))
                implementation(project(":features:app_features:home"))
                implementation(project(":features:feature:performance"))

                implementation(libs.kotlinx.datetime)

                // compose
                implementation(libs.compose.materialIconsExtended)
                implementation(libs.bundles.composeMaterial3)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // navigation
                implementation(project(":navigation"))
                implementation(libs.bundles.navigation3)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        // We use the default hierarchy template.
        // It automatically handles commonMain -> jsMain, commonMain -> wasmJsMain, etc.
        // To share code between js and wasmJs, we can use the 'webMain' source set
        // but we must let the template create the edges.

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.compose.ui.tooling)
                implementation(libs.androidx.activity.compose)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
                implementation(libs.compose.ui.tooling)
            }
        }

        // Let the default hierarchy template create jsMain and wasmJsMain.
        // If you need a shared webMain, the best practice in modern KMP is to
        // name it such that the template recognizes it or use the 'applyDefaultHierarchyTemplate'
        // to customize it, but manually adding dependsOn to getting/creating source sets
        // often conflicts with the template's automatic discovery.
    }
}

compose.desktop {
    application {
        mainClass = "th.skylabmek.kmp_frontend.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "th.skylabmek.kmp_frontend"
            packageVersion = "1.0.0"
        }
    }
}