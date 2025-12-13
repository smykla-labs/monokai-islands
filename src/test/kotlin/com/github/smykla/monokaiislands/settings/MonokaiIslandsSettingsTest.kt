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

        val newState = MonokaiIslandsSettings()
        newState.enableMarkdownCss = false

        settings.loadState(newState)

        settings.enableMarkdownCss shouldBe false
    }

    @Test
    fun `loadState preserves source state values`() {
        val settings = MonokaiIslandsSettings()
        settings.enableMarkdownCss = true

        val sourceState = MonokaiIslandsSettings()
        sourceState.enableMarkdownCss = false

        settings.loadState(sourceState)

        sourceState.enableMarkdownCss shouldBe false
        settings.enableMarkdownCss shouldBe false
    }
}
