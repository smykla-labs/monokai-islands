package com.github.smykla.monokaiislands.listeners

import com.intellij.ide.ui.LafManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ThemeProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val currentThemeId = LafManager.getInstance().currentUIThemeLookAndFeel?.id
        val isMonokaiTheme = currentThemeId == ThemeChangeListener.THEME_ID

        if (isMonokaiTheme) {
            LOG.info("Monokai theme detected on project open: ${project.name}")
            ThemeChangeListener.applyMarkdownCss(true)
        }
    }

    companion object {
        private val LOG = logger<ThemeProjectActivity>()
    }
}
