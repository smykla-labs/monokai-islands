package com.github.smykla.monokaiislands.listeners

import com.intellij.ide.ui.LafManagerListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.types.instanceOf
import org.junit.jupiter.api.Test

/**
 * Unit tests for ThemeChangeListener.
 *
 * These tests cover basic properties and structure without IDE context.
 * For integration tests with real IDE services (settings, LafManager),
 * see ThemeChangeListenerIntegrationTest which uses BasePlatformTestCase.
 */
class ThemeChangeListenerTest {

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
    fun `listener implements LafManagerListener`() {
        val listener = ThemeChangeListener()

        listener shouldBe instanceOf<LafManagerListener>()
    }
}
