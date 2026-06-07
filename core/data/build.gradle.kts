plugins {
    alias(libs.plugins.convention.kmp.library)
    alias(libs.plugins.convention.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.ktor.common)
                implementation(libs.touchlab.kermit)
                implementation(libs.koin.core)

                implementation(projects.core.domain)

                implementation(libs.datastore)
                implementation(libs.datastore.preferences)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.ktor.client.okhttp)
                // Wiretap iOS klib'leri Kotlin 2.3 ABI'si ister; proje 2.2'de olduğu için
                // wiretap yalnızca Android'de kullanılır (iosMain'de no-op).
                implementation(libs.wiretap.ktor)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
