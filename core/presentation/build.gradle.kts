plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.datetime)

                implementation(projects.core.domain)
                implementation(libs.material3.adaptive)
                implementation(compose.components.resources)

                implementation(libs.moko.permissions)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.permissions.notifications)
                implementation(libs.moko.permissions.camera)
            }
        }

        androidMain {
            dependencies {
                // Media picker (gallery/camera) needs Activity Result APIs, FileProvider/ContextCompat
                // and EXIF orientation handling for the shared image-decode step.
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.exifinterface)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
}

compose.resources {
    packageOfResClass = "crew.core.presentation.generated.resources"
}
