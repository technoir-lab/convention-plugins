package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import java.io.Serial

class TechnoirLabRuleSetProvider : RuleSetProviderV3(RuleSetId(ID)) {
    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { ForbiddenImportRule() },
    )

    internal companion object {
        internal const val ID = "technoirlab"
        internal val ABOUT = Rule.About(
            maintainer = "Technoir Lab",
            repositoryUrl = "https://github.com/technoir-lab/convention-plugins",
            issueTrackerUrl = "https://github.com/technoir-lab/convention-plugins/issues",
        )

        @Serial
        private const val serialVersionUID: Long = -3030186594870071318L
    }
}
