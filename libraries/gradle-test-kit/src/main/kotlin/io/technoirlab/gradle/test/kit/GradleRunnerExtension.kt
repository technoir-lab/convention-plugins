package io.technoirlab.gradle.test.kit

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteRecursively
import kotlin.io.path.div

class GradleRunnerExtension(
    private val resourceDir: String,
    configuration: GradleConfig.() -> Unit = {}
) : BeforeEachCallback, AfterEachCallback {

    private val config = GradleConfig()
    private var internalRoot: GradleProject? = null
    private lateinit var initScript: Path

    val root: GradleProject
        get() = checkNotNull(internalRoot) { "Root project isn't initialized. Perhaps test execution hasn't started yet" }

    init {
        config.configuration()
    }

    override fun beforeEach(context: ExtensionContext) {
        val projectDir = Files.createTempDirectory("project-")
        copyResources(resourceDir, projectDir)
        internalRoot = GradleProject(projectDir, rootDir = projectDir)
        initScript = projectDir / "gradle-test-kit.init.gradle.kts"
        InitScriptGenerator().generate(initScript)
    }

    override fun afterEach(context: ExtensionContext) {
        @OptIn(ExperimentalPathApi::class)
        internalRoot?.dir?.deleteRecursively()
    }

    fun build(vararg tasks: String, expectFailure: Boolean = false, configuration: GradleConfig.() -> Unit = {}): BuildResult {
        val config = GradleConfig(config)
        config.initScripts.add(0, initScript)
        config.configuration()
        val gradleRunner = createRunner(tasks, config)
        return if (expectFailure) gradleRunner.buildAndFail() else gradleRunner.build()
    }

    private fun createRunner(tasks: Array<out String>, config: GradleConfig): GradleRunner {
        val arguments = mutableListOf<String>()
        arguments += if (config.buildCache) "--build-cache" else "--no-build-cache"
        arguments += if (config.configurationCache) "--configuration-cache" else "--no-configuration-cache"
        arguments += if (config.configurationOnDemand) "--configure-on-demand" else "--no-configure-on-demand"
        arguments += if (config.dryRun) listOf("--dry-run") else emptyList()
        arguments += if (config.warningsAsErrors) "--warning-mode=fail" else "--warning-mode=all"
        arguments += config.arguments
        if (config.configurationCache) {
            arguments += "-Dorg.gradle.configuration-cache.parallel=true"
        }
        if (config.isolatedProjects) {
            arguments += "-Dorg.gradle.unsafe.isolated-projects=true"
        }
        arguments += config.systemProperties.map { "-D${it.key}=${it.value}" }
        arguments += config.gradleProperties.map { "-P${it.key}=${it.value}" }
        arguments += config.initScripts.flatMap { listOf("--init-script", it.absolutePathString()) }
        arguments += tasks

        @Suppress("SpreadOperator")
        return GradleRunner.create()
            .withArguments(*arguments.toTypedArray())
            .withProjectDir(root.dir.toFile())
            .apply {
                config.gradleVersion?.let { withGradleVersion(it) }
                if (config.environmentVariables.isNotEmpty()) {
                    withEnvironment(config.environmentVariables.mapValues { "${it.value}" })
                }
            }
            .forwardOutput()
    }
}
