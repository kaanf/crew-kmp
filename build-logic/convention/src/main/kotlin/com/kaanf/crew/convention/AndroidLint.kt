package com.kaanf.crew.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureAndroidLint(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    with(commonExtension) {
        lint {
            lintConfig = rootProject.file("config/lint/lint.xml")

            checkDependencies = true
            abortOnError = true
            warningsAsErrors = false

            htmlReport = true
            xmlReport = true
            sarifReport = true

            absolutePaths = false
        }
    }
}
