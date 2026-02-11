plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMPLibrary)
    alias(libs.plugins.androidLint)
}

kotlin {
    androidLibrary {
        namespace = "th.skylabmek.kmp_frontend.core.data_local"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    jvm()

    js {
        browser()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "core-data-local"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.coroutines)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}