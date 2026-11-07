package io.technoirlab.gradle.test.kit.problems

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.catchThrowable
import org.gradle.api.problems.AdditionalData
import org.gradle.api.problems.Problem
import org.gradle.api.problems.ProblemGroup
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.Severity
import org.junit.jupiter.api.Test
import java.io.IOException

@Suppress("UnstableApiUsage")
class RecordingProblemsTest {
    private val problems = RecordingProblems()

    @Test
    fun `report records a warning`() {
        problems.reporter.report(PROBLEM_ID) { contextualLabel("Option 'mode' is deprecated") }

        assertThat(problems.reported).containsExactly(
            RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'mode' is deprecated"),
        )
        assertThat(problems.thrown).isEmpty()
    }

    @Test
    fun `throwing records an error and throws the exception`() {
        val failure = IllegalStateException("Invalid configuration")

        assertThatThrownBy { problems.reporter.throwing(failure, PROBLEM_ID) { contextualLabel("Option 'mode' is invalid") } }
            .isSameAs(failure)
        assertThat(problems.thrown).containsExactly(
            RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'mode' is invalid"),
        )
        assertThat(problems.reported).isEmpty()
    }

    @Test
    fun `throwing wraps a checked exception in a runtime exception`() {
        val failure = IOException("Unable to read the configuration")

        assertThatThrownBy { problems.reporter.throwing(failure, PROBLEM_ID) {} }
            .isExactlyInstanceOf(RuntimeException::class.java)
            .cause()
            .isSameAs(failure)
    }

    @Test
    fun `create returns a problem without recording it`() {
        val problem = problems.reporter.create(PROBLEM_ID) { contextualLabel("Option 'mode' is invalid") }

        assertThat(problem).isEqualTo(RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'mode' is invalid"))
        assertThat(problems.reported).isEmpty()
        assertThat(problems.thrown).isEmpty()
    }

    @Test
    fun `records created problems when they are reported`() {
        val warning = problems.reporter.create(PROBLEM_ID) { contextualLabel("Option 'mode' is deprecated") }
        val errors = listOf(
            problems.reporter.create(PROBLEM_ID) { contextualLabel("Option 'mode' is invalid") },
            problems.reporter.create(PROBLEM_ID) { contextualLabel("Option 'level' is invalid") },
        )
        val failure = IllegalStateException("Invalid configuration")

        problems.reporter.report(listOf(warning))
        val thrown = catchThrowable { problems.reporter.throwing(failure, errors) }

        assertThat(thrown).isSameAs(failure)
        assertThat(problems.reported).containsExactly(
            RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'mode' is deprecated"),
        )
        assertThat(problems.thrown).containsExactly(
            RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'mode' is invalid"),
            RecordedProblem(id = PROBLEM_ID, contextualLabel = "Option 'level' is invalid"),
        )
    }

    @Test
    fun `records every part of a problem spec`() {
        val cause = IllegalArgumentException("Unknown mode 'slow'")

        problems.reporter.report(PROBLEM_ID) {
            contextualLabel("Option 'mode' is invalid")
            details("The option accepts 'fast' or 'safe'.")
            solution("Set 'mode' to 'fast'.")
            solution("Remove 'mode'.")
            documentedAt("https://example.com/options#mode")
            stackLocation()
            fileLocation("settings.gradle.kts")
            lineInFileLocation("build.gradle.kts", 1)
            lineInFileLocation("build.gradle.kts", 2, 3)
            lineInFileLocation("build.gradle.kts", 4, 5, 6)
            offsetInFileLocation("gradle.properties", 7, 8)
            withException(cause)
        }

        assertThat(problems.reported).containsExactly(
            RecordedProblem(
                id = PROBLEM_ID,
                contextualLabel = "Option 'mode' is invalid",
                details = "The option accepts 'fast' or 'safe'.",
                solutions = listOf("Set 'mode' to 'fast'.", "Remove 'mode'."),
                documentationUrl = "https://example.com/options#mode",
                locations = listOf(
                    RecordedLocation.File("settings.gradle.kts"),
                    RecordedLocation.LineInFile("build.gradle.kts", line = 1),
                    RecordedLocation.LineInFile("build.gradle.kts", line = 2, column = 3),
                    RecordedLocation.LineInFile("build.gradle.kts", line = 4, column = 5, length = 6),
                    RecordedLocation.OffsetInFile("gradle.properties", offset = 7, length = 8),
                    RecordedLocation.Stack,
                ),
                exception = cause,
            ),
        )
    }

    @Test
    fun `records each location once`() {
        problems.reporter.report(PROBLEM_ID) {
            fileLocation("build.gradle.kts")
            lineInFileLocation("build.gradle.kts", 1)
            fileLocation("build.gradle.kts")
            stackLocation()
            stackLocation()
        }

        assertThat(problems.reported).containsExactly(
            RecordedProblem(
                id = PROBLEM_ID,
                locations = listOf(
                    RecordedLocation.File("build.gradle.kts"),
                    RecordedLocation.LineInFile("build.gradle.kts", line = 1),
                    RecordedLocation.Stack,
                ),
            ),
        )
    }

    @Test
    fun `ignores the deprecated severity`() {
        problems.reporter.report(PROBLEM_ID) {
            @Suppress("DEPRECATION")
            severity(Severity.ERROR)
        }

        assertThat(problems.reported).containsExactly(RecordedProblem(id = PROBLEM_ID))
        assertThat(problems.thrown).isEmpty()
    }

    @Test
    fun `rejects additional data`() {
        assertThatThrownBy { problems.reporter.report(PROBLEM_ID) { additionalData(ExampleData::class.java) {} } }
            .isInstanceOf(UnsupportedOperationException::class.java)
        assertThat(problems.reported).isEmpty()
    }

    @Test
    fun `rejects problems of other implementations`() {
        val problem = object : Problem {}

        assertThatThrownBy { problems.reporter.report(problem) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThat(problems.reported).isEmpty()
    }

    private interface ExampleData : AdditionalData

    private companion object {
        @Suppress("DEPRECATION")
        private val PROBLEM_ID = ProblemId.create("invalid-option", "Invalid option", ProblemGroup.create("example", "Example plugin"))
    }
}
