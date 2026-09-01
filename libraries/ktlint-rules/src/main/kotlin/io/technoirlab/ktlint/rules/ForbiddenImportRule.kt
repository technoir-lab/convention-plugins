package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType.IMPORT_DIRECTIVE
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.CommaSeparatedListValueParser
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import org.ec4j.core.model.PropertyType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtImportDirective

internal class ForbiddenImportRule :
    Rule(
        ruleId = RuleId(RULE_ID),
        about = TechnoirLabRuleSetProvider.ABOUT,
        usesEditorConfigProperties = setOf(IMPORTS_PROPERTY),
    ),
    RuleAutocorrectApproveHandler {
    private var forbiddenImportMatchers = emptyList<Regex>()

    override fun beforeFirstNode(editorConfig: EditorConfig) {
        forbiddenImportMatchers = editorConfig[IMPORTS_PROPERTY]
            .asSequence()
            .filter { it.isNotBlank() }
            .map { it.toWholeNameGlobRegex() }
            .toList()
    }

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != IMPORT_DIRECTIVE) {
            return
        }

        val importedName = (node.psi as? KtImportDirective)
            ?.importPath
            ?.pathStr
            ?: return
        if (forbiddenImportMatchers.any { it.matches(importedName) }) {
            emit(
                node.startOffset,
                "The import `$importedName` is forbidden.",
                false,
            )
        }
    }

    internal companion object {
        internal const val RULE_ID = "${TechnoirLabRuleSetProvider.ID}:forbidden-import"

        internal val IMPORTS_PROPERTY = EditorConfigProperty(
            type = PropertyType(
                "ktlint_technoirlab_forbidden-import_imports",
                "Comma-separated whole-name glob patterns of forbidden imports.",
                CommaSeparatedListValueParser(),
                emptySet(),
            ),
            defaultValue = emptySet(),
            propertyWriter = { it.joinToString(",") },
        )

        internal fun String.toWholeNameGlobRegex(): Regex = split('*')
            .joinToString(separator = ".*") { Regex.escape(it) }
            .toRegex()
    }
}
