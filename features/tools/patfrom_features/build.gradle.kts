import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMPLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "th.skylabmek.kmp_frontend.features.tools.patfrom_features"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    val xcfName = "features:tools:patfrom_featuresKit"

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().forEach {
        it.binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.composeMaterial3)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
            }
        }

        // Use androidMain as the primary location for mpfilepicker implementation code
        // and share it with other compatible platforms.
        val sharedPickerDir = "src/androidMain/kotlin"

        androidMain.get().apply {
            dependencies {
                implementation(libs.mpfilepicker)
            }
        }

        jvmMain.get().apply {
            kotlin.srcDir(sharedPickerDir)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.mpfilepicker)
            }
        }

        jsMain.get().apply {
            kotlin.srcDir(sharedPickerDir)
            dependencies {
                implementation(libs.mpfilepicker)
            }
        }

        // Manually handle iosMain if not automatically provided
        val iosMain by creating {
            dependsOn(commonMain.get())
            kotlin.srcDir(sharedPickerDir)
            dependencies {
                implementation(libs.mpfilepicker)
            }
        }
        
        getByName("iosX64Main").dependsOn(iosMain)
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)

        wasmJsMain.get().apply {
            // Native browser implementation
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
