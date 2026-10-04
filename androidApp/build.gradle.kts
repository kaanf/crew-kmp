plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.google.services)
    alias(libs.plugins.androidx.baselineprofile)
}

android.buildTypes.getByName("release") {
    firebaseAppDistribution {
        appId = "1:508051238075:android:9993908c62d66337498d9e"
        groups = "testers"
        releaseNotes = providers.environmentVariable("RELEASE_NOTES").getOrElse("")
    }
}

dependencies {
    baselineProfile(projects.benchmark)

    implementation(projects.composeApp)
    implementation(projects.core.domain)
    implementation(projects.feature.auth.presentation)

    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)

    implementation(libs.koin.android)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

//    debugImplementation(libs.wiretap.launcher)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.core.splashscreen)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
