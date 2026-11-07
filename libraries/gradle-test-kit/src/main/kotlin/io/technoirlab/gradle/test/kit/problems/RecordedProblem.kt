package io.technoirlab.gradle.test.kit.problems

import org.gradle.api.problems.Problem
import org.gradle.api.problems.ProblemId

/**
 * A problem recorded by [RecordingProblems].
 *
 * Each property holds the part of the problem that its spec specifies, or the default when the spec omits that part.
 *
 * @property id the ID that the problem is reported with.
 * @property contextualLabel the label that describes this occurrence of the problem.
 * @property details the details of the problem.
 * @property solutions the solutions to the problem, in the order the spec adds them.
 * @property documentationUrl the URL of the problem's documentation.
 * @property locations the locations of the problem, each once, in the order the spec adds them; a requested
 * [stack location][RecordedLocation.Stack] comes last.
 * @property exception the exception that the spec attaches with `withException`. The exception that `throwing` throws is not
 * recorded.
 */
@Suppress("UnstableApiUsage")
data class RecordedProblem(
    val id: ProblemId,
    val contextualLabel: String? = null,
    val details: String? = null,
    val solutions: List<String> = emptyList(),
    val documentationUrl: String? = null,
    val locations: List<RecordedLocation> = emptyList(),
    val exception: Throwable? = null,
) : Problem
