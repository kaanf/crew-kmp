plugins {
    // AGP build-logic üzerinden zaten classpath'te; versiyon belirtmek çakışıyor.
    // Kotlin desteği AGP 9'da built-in, ayrı plugin gerekmiyor.
    id("com.android.test")
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.kaanf.crew.benchmark"
    compileSdk = libs.versions.projectCompileSdkVersion.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        // Baseline profile üretimi API 28+ cihaz ister; uygulamanın minSdk'i (26) burada geçerli değil.
        minSdk = 28
        targetSdk = libs.versions.projectTargetSdkVersion.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":androidApp"

    testOptions.managedDevices.allDevices {
        create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api34") {
            device = "Pixel 6"
            apiLevel = 34
            // ATD imajı otomatik test için küçültülmüş; sistemde zaten kurulu.
            systemImageSource = "aosp-atd"
        }
    }
}

// Studio (2025.3) sync'te her modülün "debug" varyantı için AGP 9'un ProjectGraph modelini ister.
// baselineprofile eklentisi bu modülde debug/release varyantlarını kapattığı için AGP null döner ve
// sync NPE ile patlar. Varyantları açık bırakmak bedava: baseline profile task'ları yalnız
// nonMinifiedRelease/benchmarkRelease icin uretiliyor.
androidComponents {
    beforeVariants { it.enable = true }
}

baselineProfile {
    managedDevices += "pixel6Api34"
    // CI'da fiziksel cihaz yok; emülatör üzerinden üret.
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.junit)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
