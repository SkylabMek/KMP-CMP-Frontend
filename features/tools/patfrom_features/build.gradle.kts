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

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
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

        // The default hierarchy template automatically creates iosMain 
        // and connects it to the specific iOS targets.
        iosMain.get().apply {
            kotlin.srcDir(sharedPickerDir)
            dependencies {
                implementation(libs.mpfilepicker)
            }
        }

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
