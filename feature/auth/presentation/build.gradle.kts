import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.convention.cmp.feature)
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    androidTarget {
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.datetime)

                implementation(projects.feature.auth.domain)
                implementation(projects.core.domain)
                implementation(projects.core.designsystem)
                implementation(projects.core.presentation)

                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.bundles.koin.common)
            }
        }

        commonTest {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
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

extensions.configure<LibraryExtension> {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    add("androidTestImplementation", platform(libs.androidx.compose.bom))
    add("androidTestImplementation", libs.androidx.compose.ui.test.junit4.android)
    add("debugImplementation", platform(libs.androidx.compose.bom))
    add("debugImplementation", libs.androidx.compose.ui.test.manifest)
}

compose.resources {
    packageOfResClass = "crew.feature.auth.presentation.generated.resources"
}
