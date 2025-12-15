package com.github.smykla.monokaiislands.settings

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * Unit tests for MonokaiIslandsConfigurable.
 *
 * Note: Testing createComponent(), isModified(), apply(), and reset()
 * requires IDE fixtures (Application context and MonokaiIslandsSettings
 * service) which are not available in unit tests. These methods are
 * verified through manual testing in the sandbox IDE. Full integration
 * testing would require LightPlatformTestCase or similar IDE test harness.
 *
 * Dev mode functionality: In dev mode (idea.is.internal=true), createComponent()
 * returns a JBTabbedPane with "Settings" and "Theme Testing" tabs. In production,
 * it returns the regular settings panel. This behavior is verified through manual
 * testing in the sandbox IDE.
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
