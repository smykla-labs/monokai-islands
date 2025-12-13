package com.github.smykla.monokaiislands.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ToggleMarkdownCssActionTest {

    @Test
    fun `action has correct update thread`() {
        val action = ToggleMarkdownCssAction()

        // Verify action uses background thread for updates
        action.actionUpdateThread shouldBe ActionUpdateThread.BGT
    }
}
