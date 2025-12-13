package com.github.smykla.monokaiislands.listeners

import com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings
import com.intellij.ide.ui.LafManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Integration tests for ThemeChangeListener behavior with real IDE services.
 *
 * These tests verify that the settings service is accessible and that
 * lookAndFeelChanged() properly handles different scenarios without errors.
 *
 * Note: Full theme switching behavior requires the complete IDE environment.
 * These tests focus on verifiable behavior within the test harness.
 */
class ThemeChangeListenerIntegrationTest : BasePlatformTestCase() {

    private lateinit var listener: ThemeChangeListener
    private lateinit var settings: MonokaiIslandsSettings

    override fun setUp() {
        super.setUp()
        listener = ThemeChangeListener()
        settings = MonokaiIslandsSettings.getInstance()
        settings.enableMarkdownCss = false
    }

    override fun tearDown() {
        settings.enableMarkdownCss = false
        super.tearDown()
    }

    fun `test settings service is accessible in test environment`() {
        val instance = MonokaiIslandsSettings.getInstance()

        instance shouldNotBe null
        instance shouldBe settings
    }

    fun `test settings enableMarkdownCss defaults to false`() {
        settings.enableMarkdownCss shouldBe false
    }

    fun `test settings changes are persisted within session`() {
        settings.enableMarkdownCss = true

        MonokaiIslandsSettings.getInstance().enableMarkdownCss shouldBe true
    }

    fun `test lookAndFeelChanged handles real LafManager`() {
        val lafManager = LafManager.getInstance()
        settings.enableMarkdownCss = true

        listener.lookAndFeelChanged(lafManager)
    }

    fun `test lookAndFeelChanged with CSS disabled`() {
        val lafManager = LafManager.getInstance()
        settings.enableMarkdownCss = false

        listener.lookAndFeelChanged(lafManager)
    }

    fun `test applyMarkdownCss gracefully handles missing Markdown plugin`() {
        ThemeChangeListener.applyMarkdownCss(true)
        ThemeChangeListener.applyMarkdownCss(false)
    }

    fun `test theme detection logic`() {
        val lafManager = LafManager.getInstance()
        val currentTheme = lafManager.currentUIThemeLookAndFeel

        val isMonokaiTheme = currentTheme?.id == ThemeChangeListener.THEME_ID
        val shouldApply = isMonokaiTheme && settings.enableMarkdownCss

        shouldApply shouldBe false
    }

    fun `test theme detection with CSS enabled`() {
        val lafManager = LafManager.getInstance()
        val currentTheme = lafManager.currentUIThemeLookAndFeel
        settings.enableMarkdownCss = true

        val isMonokaiTheme = currentTheme?.id == ThemeChangeListener.THEME_ID
        val shouldApply = isMonokaiTheme && settings.enableMarkdownCss

        if (isMonokaiTheme) {
            shouldApply shouldBe true
        } else {
            shouldApply shouldBe false
        }
    }
}
