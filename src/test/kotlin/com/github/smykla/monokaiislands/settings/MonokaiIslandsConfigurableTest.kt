package com.github.smykla.monokaiislands.settings

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import org.junit.jupiter.api.Test
import javax.swing.JPanel

class MonokaiIslandsConfigurableTest {

    @Test
    fun `getDisplayName returns Monokai Islands`() {
        val configurable = MonokaiIslandsConfigurable()
        configurable.displayName shouldBe "Monokai Islands"
    }

    @Test
    fun `createComponent returns settings panel`() {
        val configurable = MonokaiIslandsConfigurable()
        val component = configurable.createComponent()

        component shouldBe instanceOf<JPanel>()
    }

    @Test
    fun `disposeUIResources does not throw exception`() {
        val configurable = MonokaiIslandsConfigurable()
        configurable.createComponent()

        // Should not throw exception
        configurable.disposeUIResources()
    }
}
