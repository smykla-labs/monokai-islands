package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent

class MonokaiIslandsConfigurable : SearchableConfigurable {

    private var settingsComponent: MonokaiIslandsSettingsComponent? = null

    override fun getId(): String = ID

    override fun getDisplayName(): String = DISPLAY_NAME

    override fun createComponent(): JComponent {
        val component = settingsComponent ?: MonokaiIslandsSettingsComponent().also { settingsComponent = it }
        return component.panel
    }

    override fun isModified(): Boolean {
        val settings = MonokaiIslandsSettings.getInstance()
        return settingsComponent?.let { it.enableMarkdownCss != settings.enableMarkdownCss } ?: false
    }

    override fun apply() {
        val settings = MonokaiIslandsSettings.getInstance()
        settings.enableMarkdownCss = settingsComponent?.enableMarkdownCss ?: false

        // Apply or remove CSS based on setting and current theme
        ThemeChangeListener.applyMarkdownCss(
            ThemeChangeListener.isMonokaiThemeActive() && settings.enableMarkdownCss
        )
    }

    override fun reset() {
        val settings = MonokaiIslandsSettings.getInstance()
        settingsComponent?.enableMarkdownCss = settings.enableMarkdownCss
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    companion object {
        const val ID = "com.github.smykla.monokaiislands.settings.MonokaiIslandsConfigurable"
        const val DISPLAY_NAME = "Monokai Islands"
    }
}
