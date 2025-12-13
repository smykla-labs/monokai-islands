package com.github.smykla.monokaiislands.settings

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class MonokaiIslandsSettingsTest {

    @Test
    fun `default value is false`() {
        val settings = MonokaiIslandsSettings()
        settings.enableMarkdownCss shouldBe false
    }

    @Test
    fun `getState returns settings instance`() {
        val settings = MonokaiIslandsSettings()
        settings.enableMarkdownCss = false

        val state = settings.getState()

        state shouldNotBe null
        state shouldBe settings
        state?.enableMarkdownCss shouldBe false
    }

    @Test
    fun `loadState copies values from source state`() {
        val settings = MonokaiIslandsSettings()
        settings.enableMarkdownCss = true

        val sourceState = MonokaiIslandsSettings()
        sourceState.enableMarkdownCss = false

        settings.loadState(sourceState)

        // Target should have the value from source
        settings.enableMarkdownCss shouldBe false
        // Source should remain unchanged
        sourceState.enableMarkdownCss shouldBe false
    }
}
