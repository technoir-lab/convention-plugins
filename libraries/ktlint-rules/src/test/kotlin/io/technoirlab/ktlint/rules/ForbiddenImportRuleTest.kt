package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.test.KtLintAssertThat
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import io.technoirlab.ktlint.rules.ForbiddenImportRule.Companion.toWholeNameGlobRegex
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ForbiddenImportRuleTest {
    @Test
    fun `accepts imports when configuration is omitted`() {
        val code =
            """
            import legacy.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `accepts imports for empty, blank and nonmatching patterns`() {
        val code =
            """
            import legacy.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("", " ", "vendor.*")
            .hasNoLintViolations()
    }

    @Test
    fun `matches exact and wildcard patterns against the entire import path`() {
        val code =
            """
            import exact.Type
            import exact.TypeExtra
            import legacy.Direct
            import legacy.deep.Type
            import other.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("exact.Type", "legacy.*")
            .hasLintViolationsWithoutAutoCorrect(
                LintViolation(
                    line = 1,
                    col = 1,
                    detail = message("exact.Type"),
                ),
                LintViolation(
                    line = 3,
                    col = 1,
                    detail = message("legacy.Direct"),
                ),
                LintViolation(
                    line = 4,
                    col = 1,
                    detail = message("legacy.deep.Type"),
                ),
            )
    }

    @Test
    fun `supports trailing two-sided and consecutive wildcards`() {
        val code =
            """
            import prefix.Type
            import sample.infix.Type
            import vendor.internal.deep.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("prefix*", "*infix*", "vendor.**.Type")
            .hasLintViolationsWithoutAutoCorrect(
                LintViolation(
                    line = 1,
                    col = 1,
                    detail = message("prefix.Type"),
                ),
                LintViolation(
                    line = 2,
                    col = 1,
                    detail = message("sample.infix.Type"),
                ),
                LintViolation(
                    line = 3,
                    col = 1,
                    detail = message("vendor.internal.deep.Type"),
                ),
            )
    }

    @Test
    fun `emits one finding per directive when multiple patterns match`() {
        val code =
            """
            import vendor.internal.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.*", "*internal.Type", "vendor.internal.Type")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("vendor.internal.Type"),
            )
    }

    @Test
    fun `reports unresolved imports`() {
        val code =
            """
            import unresolved.symbol.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("unresolved.*")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("unresolved.symbol.Type"),
            )
    }

    @Test
    fun `matches the underlying path of aliased imports`() {
        val code =
            """
            import vendor.Type as Alias
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.Type")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("vendor.Type"),
            )
    }

    @Test
    fun `uses the parser path for star imports`() {
        val code =
            """
            import vendor.*
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.*")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("vendor.*"),
            )
    }

    @Test
    fun `uses the parser path for backticked identifiers`() {
        val code =
            """
            import vendor.`when`
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.`when`")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("vendor.`when`"),
            )
    }

    @Test
    fun `checks imports in Kotlin scripts`() {
        val code =
            """
            import vendor.script.Type
            
            println("example")
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .asKotlinScript()
            .withPatterns("vendor.*")
            .hasLintViolationWithoutAutoCorrect(
                line = 1,
                col = 1,
                detail = message("vendor.script.Type"),
            )
    }

    @Test
    fun `ignores packages and call-site qualified names`() {
        val code =
            """
            package vendor.example
            
            fun example() = vendor.internal.call()
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.*")
            .hasNoLintViolations()
    }

    @Test
    fun `reports at the import keyword without autocorrection`() {
        val code =
            """
            package example
            
            import vendor.Type
            
            class Example
            """.trimIndent()

        forbiddenImportAssertThat(code)
            .withPatterns("vendor.Type")
            .hasLintViolationWithoutAutoCorrect(
                line = 3,
                col = 1,
                detail = message("vendor.Type"),
            )
    }

    private fun forbiddenImportAssertThat(@Language("kotlin") code: String) = assertThatRule { ForbiddenImportRule() }(code)

    private fun KtLintAssertThat.withPatterns(vararg patterns: String) = withEditorConfigOverride(
        ForbiddenImportRule.IMPORTS_PROPERTY to ForbiddenImportRule.IMPORTS_PROPERTY.type.parse(
            patterns.joinToString(","),
        ),
    )

    private fun message(importName: String) = "The import `$importName` is forbidden."

    @Nested
    inner class WholeNameGlobMatching {
        @Test
        fun `matches the entire candidate case-sensitively`() {
            val matcher = "vendor.Type".toWholeNameGlobRegex()

            assertThat(matcher.matches("vendor.Type")).isTrue()
            assertThat(matcher.matches("vendor.TypeExtra")).isFalse()
            assertThat(matcher.matches("prefix.vendor.Type")).isFalse()
            assertThat(matcher.matches("vendor.type")).isFalse()
        }

        @Test
        fun `wildcard matches zero or more characters including periods`() {
            val matcher = "vendor*Type".toWholeNameGlobRegex()

            assertThat(matcher.matches("vendorType")).isTrue()
            assertThat(matcher.matches("vendor.internal.deep.Type")).isTrue()
        }

        @Test
        fun `consecutive wildcards are equivalent to one wildcard`() {
            val single = "vendor.*.Type".toWholeNameGlobRegex()
            val consecutive = "vendor.***.Type".toWholeNameGlobRegex()

            listOf("vendor..Type", "vendor.internal.Type", "vendor.internal.deep.Type").forEach {
                assertThat(consecutive.matches(it)).isEqualTo(single.matches(it))
            }
        }

        @Test
        fun `treats all non-wildcard regex and glob syntax literally`() {
            val matcher = "vendor.(name)?[one]+\\value".toWholeNameGlobRegex()

            assertThat(matcher.matches("vendor.(name)?[one]+\\value")).isTrue()
            assertThat(matcher.matches("vendor.Xname-one+value")).isFalse()
        }
    }
}
