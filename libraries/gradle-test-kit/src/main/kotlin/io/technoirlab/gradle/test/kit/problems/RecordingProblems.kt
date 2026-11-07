package io.technoirlab.gradle.test.kit.problems

import org.gradle.api.Action
import org.gradle.api.problems.AdditionalData
import org.gradle.api.problems.Problem
import org.gradle.api.problems.ProblemGroups
import org.gradle.api.problems.ProblemId
import org.gradle.api.problems.ProblemReporter
import org.gradle.api.problems.ProblemSpec
import org.gradle.api.problems.Problems
import org.gradle.api.problems.Severity

/**
 * A [Problems] service that records the problems reported to it instead of passing them to Gradle.
 *
 * Its reporter records the problems that Gradle reports as warnings in [reported], and the problems that fail the build in
 * [thrown]. Like Gradle's reporter, `throwing` throws the given exception, wrapped in a [RuntimeException] when it is checked.
 *
 * A recorded problem keeps every part of its spec except additional data, which the spec rejects. The spec ignores the
 * deprecated severity because Gradle derives the severity from the reporting method. Gradle's predefined problem
 * [groups][getGroups] are not available.
 */
@Suppress("UnstableApiUsage")
class RecordingProblems : Problems {
    private val reporter = Reporter()

    /**
     * The problems reported with [ProblemReporter.report], in reporting order.
     */
    val reported: List<RecordedProblem>
        field = mutableListOf()

    /**
     * The problems reported with [ProblemReporter.throwing], in reporting order.
     */
    val thrown: List<RecordedProblem>
        field = mutableListOf()

    override fun getReporter(): ProblemReporter = reporter

    /**
     * Throws [UnsupportedOperationException]: Gradle's predefined problem groups are not available.
     */
    override fun getGroups(): ProblemGroups = throw UnsupportedOperationException("RecordingProblems provides no predefined problem groups")

    private inner class Reporter : ProblemReporter {
        override fun create(problemId: ProblemId, action: Action<in ProblemSpec>): Problem =
            Spec(problemId).also { action.execute(it) }.toProblem()

        override fun report(problemId: ProblemId, spec: Action<in ProblemSpec>) {
            report(create(problemId, spec))
        }

        override fun report(problem: Problem) {
            reported += problem.recorded()
        }

        override fun report(problems: Collection<Problem>) {
            problems.forEach(::report)
        }

        override fun throwing(exception: Throwable, problemId: ProblemId, spec: Action<in ProblemSpec>): RuntimeException =
            throwing(exception, create(problemId, spec))

        override fun throwing(exception: Throwable, problem: Problem): RuntimeException = throwing(exception, listOf(problem))

        override fun throwing(exception: Throwable, problems: Collection<Problem>): RuntimeException {
            thrown += problems.map { it.recorded() }
            throw exception as? RuntimeException ?: RuntimeException(exception)
        }

        private fun Problem.recorded(): RecordedProblem {
            require(this is RecordedProblem) { "RecordingProblems reports only a RecordedProblem, but got $this" }
            return this
        }
    }

    private class Spec(private val problemId: ProblemId) : ProblemSpec {
        private var contextualLabel: String? = null
        private var details: String? = null
        private val solutions = mutableListOf<String>()
        private var documentationUrl: String? = null
        private val locations = mutableListOf<RecordedLocation>()
        private var collectStackLocation = false
        private var exception: Throwable? = null

        override fun contextualLabel(contextualLabel: String): ProblemSpec = apply { this.contextualLabel = contextualLabel }

        override fun details(details: String): ProblemSpec = apply { this.details = details }

        override fun solution(solution: String): ProblemSpec = apply { solutions += solution }

        override fun documentedAt(url: String): ProblemSpec = apply { documentationUrl = url }

        override fun fileLocation(path: String): ProblemSpec = location(RecordedLocation.File(path))

        override fun lineInFileLocation(path: String, line: Int): ProblemSpec = location(RecordedLocation.LineInFile(path, line))

        override fun lineInFileLocation(path: String, line: Int, column: Int): ProblemSpec =
            location(RecordedLocation.LineInFile(path, line, column))

        override fun lineInFileLocation(path: String, line: Int, column: Int, length: Int): ProblemSpec =
            location(RecordedLocation.LineInFile(path, line, column, length))

        override fun offsetInFileLocation(path: String, offset: Int, length: Int): ProblemSpec =
            location(RecordedLocation.OffsetInFile(path, offset, length))

        override fun stackLocation(): ProblemSpec = apply { collectStackLocation = true }

        override fun <T : AdditionalData> additionalData(type: Class<T>, config: Action<in T>): ProblemSpec =
            throw UnsupportedOperationException("RecordingProblems does not record additional data")

        override fun withException(t: Throwable): ProblemSpec = apply { exception = t }

        @Deprecated("Gradle derives the severity from the reporting method.")
        override fun severity(severity: Severity): ProblemSpec = this

        fun toProblem(): RecordedProblem {
            val recordedLocations = if (collectStackLocation) locations + RecordedLocation.Stack else locations.toList()
            return RecordedProblem(problemId, contextualLabel, details, solutions.toList(), documentationUrl, recordedLocations, exception)
        }

        private fun location(location: RecordedLocation): ProblemSpec = apply {
            if (location !in locations) {
                locations += location
            }
        }
    }
}
