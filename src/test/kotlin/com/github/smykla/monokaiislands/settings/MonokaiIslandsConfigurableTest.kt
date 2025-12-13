package com.github.smykla.monokaiislands.settings

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import org.junit.jupiter.api.Test
import javax.swing.JPanel

/**
 * Unit tests for MonokaiIslandsConfigurable.
 *
 * Note: Testing createComponent(), isModified(), apply(), and reset()
 * requires IDE fixtures (Application context and MonokaiIslandsSettings
 * service) which are not available in unit tests. Since createComponent()
 * calls reset() to initialize UI with current settings, it also requires
 * the Application context. These methods are verified through manual
 * testing in the sandbox IDE. Full integration testing would require
 * LightPlatformTestCase or similar IDE test harness.
 */
class MonokaiIslandsConfigurableTest {

    @Test
    fun `getDisplayName returns Monokai Islands`() {
        val configurable = MonokaiIslandsConfigurable()
        configurable.displayName shouldBe "Monokai Islands"
    }

    @Test
    fun `getId returns correct ID`() {
        val configurable = MonokaiIslandsConfigurable()
        configurable.id shouldBe "com.github.smykla.monokaiislands.settings.MonokaiIslandsConfigurable"
    }
}
