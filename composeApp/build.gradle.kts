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
    // 1. Declare targets first
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

    // 2. Configure source sets after targets are declared
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":navigation"))
                implementation(project(":ui"))
                implementation(project(":domain"))
                implementation(project(":shared-resources"))
                implementation(project(":core:network"))
                implementation(project(":core:common"))
                implementation(project(":core:data-local"))
                implementation(project(":features:app"))
                implementation(project(":features:profile"))
                implementation(project(":features:home"))

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

        val webMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }

        val jsMain by getting {
            dependsOn(webMain)
        }

        val wasmJsMain by getting {
            dependsOn(webMain)
        }

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
    }
}

compose.resources {
    generateResClass = always
    publicResClass = true
    packageOfResClass = "th.skylabmek.kmp_frontend"
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
