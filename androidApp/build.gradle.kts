plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.google.services)
    alias(libs.plugins.androidx.baselineprofile)
}

android.defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
    implementation(projects.core.data)
    implementation(libs.sentry.android)
    implementation(projects.core.presentation)
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
    testImplementation(libs.kotlinx.coroutines.test)
    // debugImplementation(ui-tooling) puts androidx.test (via compose ui-test) on the app's runtime
    // classpath and AGP pins androidTest to exactly those versions, so the catalog's newer
    // junit/runner/espresso can't resolve. ui-test-junit4 from the same BOM brings matching ones.
    androidTestImplementation(libs.androidx.compose.ui.test.junit4.android)
}

// Optional build-time values. Never store a Sentry auth token in the app.
android.buildFeatures.buildConfig = true
android.defaultConfig {
    val sentryDsn = providers.environmentVariable("CREW_SENTRY_MOBILE_DSN").getOrElse("")
    require(sentryDsn.none { it == '"' || it == '\\' || it.isISOControl() })
    buildConfigField("String", "SENTRY_DSN", "\"$sentryDsn\"")
}
