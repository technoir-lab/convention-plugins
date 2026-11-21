package io.technoirlab.conventions.common.fixtures

import io.technoirlab.gradle.test.kit.GradleProject
import io.technoirlab.gradle.test.kit.buildDir
import kotlin.io.path.div
import kotlin.io.path.readText

private val CLASS_NAME = Regex("""<class name="([^"]+)"""")

/**
 * Returns the names of the classes that the project's Kover XML report measures, such as `com/example/Example`.
 */
fun GradleProject.coverageReportClasses(): List<String> =
    CLASS_NAME.findAll((buildDir / "reports/kover/report.xml").readText()).map { it.groupValues[1] }.toList()
