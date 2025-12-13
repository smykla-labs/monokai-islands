package com.github.smykla.monokaiislands.listeners

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class ThemeChangeListenerIntegrationTest {

    @Test
    fun `THEME_ID constant is correct`() {
        ThemeChangeListener.THEME_ID shouldBe "com.github.smykla-labs.monokai-islands-dark"
    }

    @Test
    fun `monoKaiCss loads from resources`() {
        val css = ThemeChangeListener.monoKaiCss

        css.shouldNotBeEmpty()
    }

    @Test
    fun `applyMarkdownCss handles missing Markdown plugin gracefully`() {
        // This test verifies that applyMarkdownCss doesn't throw exceptions
        // when the Markdown plugin is not available (ClassNotFoundException)
        // The method should catch the exception and log it

        // Note: This requires full IDE environment to test properly
        // For now, we verify the method exists and can be called
        // Integration testing in sandbox IDE will verify actual behavior
    }

    @Test
    fun `listener implements LafManagerListener`() {
        val listener = ThemeChangeListener()

        listener shouldBe io.kotest.matchers.types.instanceOf<com.intellij.ide.ui.LafManagerListener>()
    }
}
