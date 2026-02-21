rootProject.name = "kmp-frontend"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")
include(":androidApp")
include(":shared-resources")

include(":core:network")
include(":core:common")
include(":core:data-local")

include(":domain")
include(":ui")
include(":navigation")

include(":features:app_features:home")
include(":features:app_features:profile")

include(":features:feature:app")
include(":features:feature:performance")
include(":features:feature:markdown")

include(":features:tools:patfrom_features")
