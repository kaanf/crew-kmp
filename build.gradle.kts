import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask

fun BaseKtLintCheckTask.excludeGeneratedSources() {
    exclude("**/build/**")
    exclude("**/generated/**")
    exclude { fileTreeElement ->
        val normalizedPath = fileTreeElement.file.invariantSeparatorsPath
        normalizedPath.contains("/build/") || normalizedPath.contains("/generated/")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.android) apply false
}

configure<KtlintExtension> {
    outputToConsole.set(true)
    ignoreFailures.set(true)
}

tasks.withType<BaseKtLintCheckTask>().configureEach {
    excludeGeneratedSources()
}

tasks.withType<KtLintFormatTask>().configureEach {
    excludeGeneratedSources()
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<KtlintExtension> {
        outputToConsole.set(true)
        ignoreFailures.set(true)
    }

    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        parallel = true
        ignoreFailures = true
        basePath = rootDir.absolutePath
    }

    tasks.withType<BaseKtLintCheckTask>().configureEach {
        excludeGeneratedSources()
    }

    tasks.withType<KtLintFormatTask>().configureEach {
        excludeGeneratedSources()
    }

    tasks.withType<Detekt>().configureEach {
        exclude("**/build/**")
        exclude("**/generated/**")
        jvmTarget = "17"

        reports.html.required.set(true)
        reports.xml.required.set(true)
        reports.sarif.required.set(true)
        reports.md.required.set(false)
    }
}

tasks.named("ktlintCheck") {
    dependsOn(subprojects.map { "${it.path}:ktlintCheck" })
}

tasks.named("ktlintFormat") {
    dependsOn(subprojects.map { "${it.path}:ktlintFormat" })
}

val aggregatedDetektTaskNames =
    setOf(
        "detektMetadataCommonMain",
        "detektMetadataCommonTest",
        "detektMetadataIosMain",
        "detektMetadataIosTest",
        "detektAndroidDebug",
        "detektAndroidDebugUnitTest",
        "detektAndroidDebugAndroidTest",
    )

val detektTask: TaskProvider<Task> =
    tasks.register("detekt") {
        group = "verification"
        description = "Runs detekt for the source sets that contain actual KMP code."
    }

val detektBaselineTask: TaskProvider<Task> =
    tasks.register("detektBaseline") {
        group = "verification"
        description = "Creates detekt baselines for the source sets that contain actual KMP code."
    }

val androidLintTask: TaskProvider<Task> =
    tasks.register("androidLint") {
        group = "verification"
        description = "Runs Android Lint for every Android target in the KMP project."
    }

gradle.projectsEvaluated {
    androidLintTask.configure {
        dependsOn(
            subprojects.mapNotNull { project ->
                project.tasks.findByName("lintDebug")?.path
            },
        )
    }

    detektTask.configure {
        dependsOn(
            subprojects.flatMap { project ->
                aggregatedDetektTaskNames.mapNotNull { taskName ->
                    project.tasks.findByName(taskName)?.path
                }
            },
        )
    }

    detektBaselineTask.configure {
        dependsOn(
            subprojects.flatMap { project ->
                aggregatedDetektTaskNames.mapNotNull { taskName ->
                    project.tasks.findByName(
                        taskName.replace("detekt", "detektBaseline"),
                    )?.path
                }
            },
        )
    }
}
