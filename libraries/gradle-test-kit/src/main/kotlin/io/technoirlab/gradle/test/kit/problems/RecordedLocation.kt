package io.technoirlab.gradle.test.kit.problems

/**
 * A location of a [RecordedProblem], as its problem spec specifies it.
 */
sealed interface RecordedLocation {
    /**
     * A file, specified with `fileLocation`.
     */
    data class File(val path: String) : RecordedLocation

    /**
     * A line in a file, specified with `lineInFileLocation`.
     *
     * @property column the column on the line, or `null` when the spec omits it.
     * @property length the length of the location, or `null` when the spec omits it.
     */
    data class LineInFile(
        val path: String,
        val line: Int,
        val column: Int? = null,
        val length: Int? = null,
    ) : RecordedLocation

    /**
     * A range of characters in a file, specified with `offsetInFileLocation`.
     */
    data class OffsetInFile(
        val path: String,
        val offset: Int,
        val length: Int,
    ) : RecordedLocation

    /**
     * The location of the code that reports the problem, requested with `stackLocation`.
     */
    data object Stack : RecordedLocation
}
