package com.github.smykla.monokaiislands.settings

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import javax.swing.JPanel

/**
 * Unit tests for ThemeTestingComponent.
 *
 * These tests verify basic panel creation without IDE context.
 * Visual verification of UI components requires manual testing
 * in the sandbox IDE (see .claude/skills/jetbrains-theme-investigation/theme-testing-panel.md).
 */
class ThemeTestingComponentTest {

    @Test
    fun `panel is created and not null`() {
        val component = ThemeTestingComponent()

        component.panel shouldNotBe null
        component.panel.shouldBeInstanceOf<JPanel>()
    }

    @Test
    fun `panel has components`() {
        val component = ThemeTestingComponent()

        component.panel.componentCount shouldNotBe 0
    }
}
