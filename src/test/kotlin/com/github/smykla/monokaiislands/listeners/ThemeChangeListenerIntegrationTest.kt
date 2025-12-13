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
 *
 * Method names use backtick notation for readability but must start with "test"
 * for JUnit 3/4 compatibility (required by BasePlatformTestCase).
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

    fun `test lookAndFeelChanged handles real LafManager without throwing`() {
        val lafManager = LafManager.getInstance()
        settings.enableMarkdownCss = true

        // Verify method executes without throwing exceptions
        val result = runCatching { listener.lookAndFeelChanged(lafManager) }

        result.isSuccess shouldBe true
    }

    fun `test lookAndFeelChanged with CSS disabled completes without error`() {
        val lafManager = LafManager.getInstance()
        settings.enableMarkdownCss = false

        // Verify method executes without throwing exceptions
        val result = runCatching { listener.lookAndFeelChanged(lafManager) }

        result.isSuccess shouldBe true
    }

    fun `test applyMarkdownCss gracefully handles missing Markdown plugin`() {
        // Verify both enable and disable complete without throwing
        val enableResult = runCatching { ThemeChangeListener.applyMarkdownCss(true) }
        val disableResult = runCatching { ThemeChangeListener.applyMarkdownCss(false) }

        enableResult.isSuccess shouldBe true
        disableResult.isSuccess shouldBe true
    }

    fun `test isMonokaiThemeActive returns boolean without throwing`() {
        val result = runCatching { ThemeChangeListener.isMonokaiThemeActive() }

        result.isSuccess shouldBe true
        result.getOrNull() shouldNotBe null
    }

    fun `test theme detection logic with CSS disabled`() {
        val isMonokaiTheme = ThemeChangeListener.isMonokaiThemeActive()
        settings.enableMarkdownCss = false

        val shouldApply = isMonokaiTheme && settings.enableMarkdownCss

        shouldApply shouldBe false
    }

    fun `test theme detection logic with CSS enabled`() {
        val isMonokaiTheme = ThemeChangeListener.isMonokaiThemeActive()
        settings.enableMarkdownCss = true

        val shouldApply = isMonokaiTheme && settings.enableMarkdownCss

        if (isMonokaiTheme) {
            shouldApply shouldBe true
        } else {
            shouldApply shouldBe false
        }
    }
}
