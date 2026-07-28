plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.google.services)
}

// App Distribution appId ile çalışır; google-services eklentisi (FCM) ise androidApp/google-services.json ister.
// Kimlik: GOOGLE_APPLICATION_CREDENTIALS env'i service account json'ına baksın.
android.buildTypes.getByName("release") {
    firebaseAppDistribution {
        appId = "1:508051238075:android:9993908c62d66337498d9e"
        groups = "testers"
        releaseNotes = providers.environmentVariable("RELEASE_NOTES").getOrElse("")
    }
}

dependencies {
    implementation(projects.composeApp)
    // PushTokenSync, UserRepository/SessionStorage arayüzlerini kullanır.
    implementation(projects.core.domain)
    // MainActivity, Apple deep link dönüşünü AppleSignInBridge'e iletir.
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
