package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import java.io.Serial

class TechnoirLabRuleSet : RuleSetProviderV3(RuleSetId(ID)) {
    override fun getRuleProviders(): Set<RuleProvider> = emptySet()

    internal companion object {
        internal const val ID = "technoirlab"

        @Serial
        private const val serialVersionUID: Long = -3030186594870071318L
    }
}
