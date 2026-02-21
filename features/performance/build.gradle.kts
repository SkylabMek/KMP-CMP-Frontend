import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMPLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "th.skylabmek.kmp_frontend.features.performance"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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

    val xcfName = "features:performanceKit"

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
                implementation(project(":ui"))
                implementation(project(":core:common"))
                implementation(project(":domain"))
                implementation(project(":features:app"))
                implementation(project(":features:markdown"))
                implementation(project(":features:tools:patfrom_features"))
                implementation(project(":shared-resources"))

                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.composeMaterial3)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.materialIconsExtended)

                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                
                implementation(libs.bundles.coil)
            }
        }

        val androidMain by getting
        val jvmMain by getting
        val jsMain by getting
        val wasmJsMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
