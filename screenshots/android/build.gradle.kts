plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}
android {
    namespace = "com.kaanf.crew.screenshots"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.kaanf.crew.screenshots"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "screenshots"
    }
    buildFeatures { compose = true }
}
// There is intentionally no distributable release variant.
androidComponents { beforeVariants(selector().withBuildType("release")) { it.enable = false } }
dependencies {
    implementation(projects.screenshots.shared)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.jetbrains.compose.foundation)
}
