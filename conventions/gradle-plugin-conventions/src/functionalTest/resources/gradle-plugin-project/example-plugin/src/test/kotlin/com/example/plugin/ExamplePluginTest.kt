package com.example.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExamplePluginTest {
    private lateinit var project: Project

    @BeforeEach
    fun setUp() {
        project = ProjectBuilder.builder().build()
    }

    @Test
    fun `apply test`() {
        ExamplePlugin().apply(project)
    }
}
