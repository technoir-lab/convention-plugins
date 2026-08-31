package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.ServiceLoader

class TechnoirLabRuleSetTest {
    @Test
    fun `discoverable via ServiceLoader`() {
        val ruleSetProviders = ServiceLoader.load(RuleSetProviderV3::class.java)

        assertThat(ruleSetProviders)
            .flatExtracting({ it.id.value })
            .contains(TechnoirLabRuleSet.ID)
    }
}
