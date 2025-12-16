package com.github.smykla.monokaiislands.toolwindow

import com.github.smykla.monokaiislands.settings.ThemeTestingComponent
import com.github.smykla.monokaiislands.utils.DevModeDetector
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the Theme Testing tool window.
 *
 * This tool window provides visual testing components for theme development.
 * It is only available when running in dev mode (idea.is.internal=true).
 */
class ThemeTestingToolWindowFactory : ToolWindowFactory, DumbAware {

    /**
     * Determines if the tool window should be available.
     * Only shows in dev mode to prevent cluttering production IDEs.
     */
    override fun shouldBeAvailable(project: Project): Boolean {
        return DevModeDetector.isDevMode()
    }

    /**
     * Creates the tool window content using ThemeTestingComponent.
     * Wraps the component in a scroll pane for better UX with large content.
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val component = ThemeTestingComponent()
        val scrollPane = JBScrollPane(component.panel)
        val content = ContentFactory.getInstance().createContent(scrollPane, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
