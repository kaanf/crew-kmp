rootProject.name = "crew"
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
include(":core:presentation")
include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":feature:auth:presentation")
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:home:presentation")
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:game:domain")
include(":feature:game:data")
include(":feature:game:presentation")
include(":androidApp")
include(":benchmark")
