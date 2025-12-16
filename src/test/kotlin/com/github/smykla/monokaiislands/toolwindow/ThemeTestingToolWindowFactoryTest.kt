package com.github.smykla.monokaiislands.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindowFactory
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

/**
 * Unit tests for ThemeTestingToolWindowFactory.
 *
 * Note: Testing shouldBeAvailable() and createToolWindowContent() requires
 * IDE fixtures (Project, ToolWindow, ContentFactory) which are not available
 * in unit tests. These methods are verified through manual testing in the
 * sandbox IDE. Full integration testing would require LightPlatformTestCase
 * or similar IDE test harness.
 *
 * Tool window behavior: The tool window only appears when idea.is.internal=true
 * (dev mode). This is controlled by shouldBeAvailable() which delegates to
 * DevModeDetector.isDevMode().
 */
class ThemeTestingToolWindowFactoryTest {

    @Test
    fun `ThemeTestingToolWindowFactory implements ToolWindowFactory`() {
        val factory = ThemeTestingToolWindowFactory()
        factory.shouldBeInstanceOf<ToolWindowFactory>()
    }

    @Test
    fun `ThemeTestingToolWindowFactory implements DumbAware`() {
        val factory = ThemeTestingToolWindowFactory()
        factory.shouldBeInstanceOf<DumbAware>()
    }
}
