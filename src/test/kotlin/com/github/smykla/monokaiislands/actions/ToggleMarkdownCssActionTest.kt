package com.github.smykla.monokaiislands.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.ToggleAction
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
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
    fun `action can be instantiated without errors`() {
        // Verify basic instantiation works without throwing exceptions
        val action = ToggleMarkdownCssAction()

        // Action should be a ToggleAction
        action shouldBe instanceOf<ToggleAction>()
    }

    @Test
    fun `action has correct update thread`() {
        val action = ToggleMarkdownCssAction()

        // Verify action uses background thread for updates
        action.actionUpdateThread shouldBe ActionUpdateThread.BGT
    }
}
