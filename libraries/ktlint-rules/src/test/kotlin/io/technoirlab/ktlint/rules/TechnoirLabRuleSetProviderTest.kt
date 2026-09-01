package io.technoirlab.ktlint.rules

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.ServiceLoader

class TechnoirLabRuleSetProviderTest {
    private val provider = TechnoirLabRuleSetProvider()

    @Test
    fun `discoverable via ServiceLoader`() {
        val ruleSetProviders = ServiceLoader.load(RuleSetProviderV3::class.java)

        assertThat(ruleSetProviders)
            .flatExtracting({ it.id.value })
            .contains(TechnoirLabRuleSetProvider.ID)
    }

    @Test
    fun `has the contracted provider and rule IDs`() {
        assertThat(provider.id.value).isEqualTo("technoirlab")
        assertThat(provider.getRuleProviders())
            .flatExtracting({ it.ruleId.value })
            .containsExactly("technoirlab:forbidden-import")
    }

    @Test
    fun `loaded provider and rule IDs are unique`() {
        val providers = ServiceLoader.load(RuleSetProviderV3::class.java).toList()
        val providerIds = providers.map { it.id.value }
        val ruleIds = providers.flatMap { provider -> provider.getRuleProviders().map { it.ruleId.value } }

        assertThat(providerIds).doesNotHaveDuplicates()
        assertThat(ruleIds).doesNotHaveDuplicates()
    }

    @Test
    fun `rule provider creates fresh instances`() {
        val ruleProvider = provider.getRuleProviders().single()

        assertThat(ruleProvider.createNewRuleInstance())
            .isNotSameAs(ruleProvider.createNewRuleInstance())
    }
}
