package io.technoirlab.gradle.test.kit

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal

fun Project.evaluate() {
    (this as ProjectInternal).evaluate()
}
