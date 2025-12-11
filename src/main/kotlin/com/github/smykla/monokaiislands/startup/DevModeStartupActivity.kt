package com.github.smykla.monokaiislands.startup

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.ui.UIUtil
import java.awt.Frame

/**
 * Development-only startup activity that configures the IDE for theme development.
 * Only runs when idea.is.internal=true (development mode).
 */
@Suppress("UnstableApiUsage")
class DevModeStartupActivity : ProjectActivity {

    private val log = Logger.getInstance(DevModeStartupActivity::class.java)

    override suspend fun execute(project: Project) {
        // Only run in development mode (when idea.is.internal=true)
        if (!isInternalMode()) {
            log.info("Not in internal mode, skipping DevModeStartupActivity")
            return
        }

        log.info("Running DevModeStartupActivity")

        // Set theme to Monokai Islands Dark
        setTheme()

        // Set color scheme to Monokai Islands Dark
        setColorScheme()

        // Maximize window
        maximizeWindow(project)
    }

    private fun isInternalMode(): Boolean {
        val prop = System.getProperty("idea.is.internal")
        log.info("idea.is.internal property value: $prop")
        return prop?.toBoolean() == true
    }

    private fun setTheme() {
        val lafManager = LafManager.getInstance()
        val targetThemeId = "com.github.smykla-labs.monokai-islands-dark"

        // Find our theme using the non-deprecated API
        val theme = lafManager.installedThemes.firstOrNull { it.id == targetThemeId }

        // Set it as current if found and not already active
        val currentTheme = lafManager.currentUIThemeLookAndFeel
        if (theme != null && currentTheme?.id != targetThemeId) {
            UIUtil.invokeLaterIfNeeded {
                lafManager.currentUIThemeLookAndFeel = theme
                lafManager.updateUI()
            }
        }
    }

    private fun setColorScheme() {
        val colorsManager = EditorColorsManager.getInstance()
        val targetSchemeName = "Monokai Islands Dark"

        val scheme = colorsManager.allSchemes.firstOrNull { it.name == targetSchemeName }
        if (scheme != null && colorsManager.globalScheme.name != targetSchemeName) {
            UIUtil.invokeLaterIfNeeded {
                colorsManager.setGlobalScheme(scheme)
            }
        }
    }

    private fun maximizeWindow(project: Project) {
        val frame = WindowManager.getInstance().getFrame(project)
        frame?.extendedState = Frame.MAXIMIZED_BOTH
    }
}
