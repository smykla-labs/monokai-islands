package com.github.smykla.monokaiislands.startup

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ThemeStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val currentThemeId = LafManager.getInstance().currentUIThemeLookAndFeel?.id
        val isMonokaiTheme = currentThemeId == ThemeChangeListener.THEME_ID

        if (isMonokaiTheme) {
            ThemeChangeListener.applyMarkdownCss(true)
        }
    }
}
