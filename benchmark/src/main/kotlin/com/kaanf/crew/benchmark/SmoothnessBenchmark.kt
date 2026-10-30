package com.kaanf.crew.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

/**
 * Oturumsuz ölçülebilen iki journey: cold start ve NavigationRoot'taki ortak slide geçişi
 * (Welcome → Register push + geri pop). Tüm ekranlar aynı enter/exit spec'ini kullandığı için
 * bu geçiş uygulama genelindeki navigasyon maliyetini temsil eder.
 *
 * Fiziksel cihaz bağlıyken: ./gradlew :benchmark:connectedBenchmarkReleaseAndroidTest
 *
 * ponytail: Dashboard/EventDetail scroll'u kapsanmıyor — test hesabı + canlı backend ister.
 * Oturum açık cihazda elle Perfetto ile ölçülür (performance-reports/SMOOTHNESS_AUDIT.md).
 */
class SmoothnessBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun coldStart() = rule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.COLD,
        iterations = ITERATIONS,
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun welcomeToRegisterPushPop() = rule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Partial(),
        startupMode = StartupMode.WARM,
        iterations = ITERATIONS,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            // İlk kurulumda onboarding pager'ı Welcome'ın önünde durur.
            device.wait(Until.findObject(By.text(SKIP)), UI_TIMEOUT_MS)?.click()
            device.wait(Until.hasObject(By.text(CONTINUE_WITH_EMAIL)), UI_TIMEOUT_MS)
        },
    ) {
        device.findObject(By.text(CONTINUE_WITH_EMAIL)).click()
        device.waitForIdle()
        device.pressBack()
        device.wait(Until.hasObject(By.text(CONTINUE_WITH_EMAIL)), UI_TIMEOUT_MS)
        device.waitForIdle()
    }

    private companion object {
        const val PACKAGE_NAME = "com.kaanf.crew"
        const val ITERATIONS = 10
        const val UI_TIMEOUT_MS = 10_000L
        const val SKIP = "Skip"
        const val CONTINUE_WITH_EMAIL = "Continue with email"
    }
}
