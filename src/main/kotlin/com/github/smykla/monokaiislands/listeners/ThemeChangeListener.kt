package com.github.smykla.monokaiislands.listeners

import com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

class ThemeChangeListener : LafManagerListener {

    override fun lookAndFeelChanged(source: LafManager) {
        val isMonokaiTheme = source.currentUIThemeLookAndFeel?.id == THEME_ID
        val settings = MonokaiIslandsSettings.getInstance()
        val shouldApply = isMonokaiTheme && settings.enableMarkdownCss
        LOG.info("Theme changed. Is Monokai: $isMonokaiTheme, CSS enabled: ${settings.enableMarkdownCss}")
        applyMarkdownCss(shouldApply)
    }

    companion object {

        private val LOG = logger<ThemeChangeListener>()

        const val THEME_ID = "com.github.smykla-labs.monokai-islands-dark"

        fun isMonokaiThemeActive(): Boolean =
            LafManager.getInstance().currentUIThemeLookAndFeel?.id == THEME_ID

        private const val CSS_RESOURCE_PATH = "/styles/markdown-preview.css"
        private const val MARKDOWN_SETTINGS_CLASS = "org.intellij.plugins.markdown.settings.MarkdownSettings"
        private const val MARKDOWN_COMPANION_CLASS = $$"$$MARKDOWN_SETTINGS_CLASS$Companion"

        val monoKaiCss: String by lazy {
            ThemeChangeListener::class.java.getResourceAsStream(CSS_RESOURCE_PATH)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: run {
                    LOG.error("Failed to load CSS resource: $CSS_RESOURCE_PATH")
                    ""
                }
        }

        fun applyMarkdownCss(enable: Boolean) {
            try {
                val settingsClass = Class.forName(MARKDOWN_SETTINGS_CLASS)
                val companionClass = Class.forName(MARKDOWN_COMPANION_CLASS)
                val companionInstance = settingsClass.getField("Companion").get(null)
                val getInstanceMethod = companionClass.getMethod("getInstance", Project::class.java)

                ProjectManager.getInstance().openProjects
                    .filterNot { it.isDisposed }
                    .forEach { project ->
                        applyToProject(project, getInstanceMethod, companionInstance, enable)
                    }
            } catch (e: ClassNotFoundException) {
                LOG.info("Markdown plugin isn't installed: ${e.message}")
            } catch (e: ReflectiveOperationException) {
                LOG.warn("Markdown plugin API changed or inaccessible: ${e.message}")
            }
        }

        private fun applyToProject(
            project: Project,
            getInstanceMethod: java.lang.reflect.Method,
            companionInstance: Any,
            enable: Boolean
        ) {
            try {
                val settings = getInstanceMethod.invoke(companionInstance, project)
                val settingsClass = settings.javaClass

                val setUseCustomStylesheetText = settingsClass.getMethod(
                    "setUseCustomStylesheetText",
                    Boolean::class.java
                )
                val setCustomStylesheetText = settingsClass.getMethod(
                    "setCustomStylesheetText",
                    String::class.java
                )

                setUseCustomStylesheetText.invoke(settings, enable)
                setCustomStylesheetText.invoke(settings, if (enable) monoKaiCss else "")

                LOG.info("Applied Markdown CSS to project: ${project.name}, enabled: $enable")
            } catch (e: ReflectiveOperationException) {
                LOG.warn("Failed to apply CSS to project ${project.name}: ${e.message}")
            }
        }
    }
}
