package com.github.smykla.monokaiislands.listeners

import com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ThemeProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val currentThemeId = LafManager.getInstance().currentUIThemeLookAndFeel?.id
        val isMonokaiTheme = currentThemeId == ThemeChangeListener.THEME_ID
        val enableMarkdownCss = MonokaiIslandsSettings.getInstance().enableMarkdownCss

        if (isMonokaiTheme && enableMarkdownCss) {
            LOG.info("Monokai theme detected on project open: ${project.name}, applying Markdown CSS")
            ThemeChangeListener.applyMarkdownCss(true)
        } else if (isMonokaiTheme) {
            LOG.info("Monokai theme detected on project open: ${project.name}, but Markdown CSS disabled in settings")
        }
    }

    companion object {
        private val LOG = logger<ThemeProjectActivity>()
    }
}
