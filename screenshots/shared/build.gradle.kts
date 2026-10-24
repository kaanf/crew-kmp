plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.kaanf.crew.screenshots.shared"
        compileSdk = 36
        minSdk = 26
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }
    // Deliberately no physical-device target or production framework.
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "CrewScreenshots"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.presentation)
            implementation(projects.core.designsystem)
            implementation(projects.core.palette)
            implementation(projects.feature.home.domain)
            implementation(projects.feature.home.presentation)
            implementation(projects.feature.game.domain)
            implementation(projects.feature.game.presentation)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.bundles.koin.common)
            implementation(libs.coil.compose)
        }
    }
}
compose.resources { packageOfResClass = "crew.screenshots.generated.resources" }
