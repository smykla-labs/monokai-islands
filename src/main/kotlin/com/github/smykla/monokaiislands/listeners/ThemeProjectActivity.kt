package com.github.smykla.monokaiislands.listeners

import com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ThemeProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        val isMonokaiTheme = ThemeChangeListener.isMonokaiThemeActive()
        val enableMarkdownCss = MonokaiIslandsSettings.getInstance().enableMarkdownCss

        if (isMonokaiTheme) {
            if (enableMarkdownCss) {
                LOG.info("Monokai theme detected on project open: ${project.name}, applying Markdown CSS")
                ThemeChangeListener.applyMarkdownCss(true)
            } else {
                LOG.info("Monokai theme detected on project open: ${project.name}, Markdown CSS disabled in settings")
            }
        }
    }

    companion object {
        private val LOG = logger<ThemeProjectActivity>()
    }
}
