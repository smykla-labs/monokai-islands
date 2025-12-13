package com.github.smykla.monokaiislands.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * Unit tests for ToggleMarkdownCssAction.
 *
 * Note: Testing isSelected() and setSelected() requires IDE fixtures (Application context,
 * LafManager, and MonokaiIslandsSettings service) which are not available in unit tests.
 * These methods are verified through manual testing in the sandbox IDE. Full integration
 * testing would require LightPlatformTestCase or similar IDE test harness.
 */
class ToggleMarkdownCssActionTest {

    @Test
    fun `action has correct update thread`() {
        val action = ToggleMarkdownCssAction()

        // Verify action uses background thread for updates
        action.actionUpdateThread shouldBe ActionUpdateThread.BGT
    }
}
