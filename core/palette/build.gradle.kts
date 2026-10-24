plugins {
    alias(libs.plugins.convention.cmp.library)
}

kotlin {
    // Geometry/contrast tests also run on the JVM (commonTest otherwise runs only on iOS simulators).
    androidLibrary {
        withHostTest {}
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
            }
        }
    }
}
