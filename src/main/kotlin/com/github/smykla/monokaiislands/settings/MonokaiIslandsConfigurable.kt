package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent

class MonokaiIslandsConfigurable : SearchableConfigurable {

    private var settingsComponent: MonokaiIslandsSettingsComponent? = null

    override fun getId(): String =
        "com.github.smykla.monokaiislands.settings.MonokaiIslandsConfigurable"

    override fun getDisplayName(): String = "Monokai Islands"

    override fun createComponent(): JComponent {
        val component = MonokaiIslandsSettingsComponent()
        settingsComponent = component
        return component.panel
    }

    override fun isModified(): Boolean {
        val settings = MonokaiIslandsSettings.getInstance()
        return settingsComponent?.enableMarkdownCss != settings.enableMarkdownCss
    }

    override fun apply() {
        val settings = MonokaiIslandsSettings.getInstance()
        settings.enableMarkdownCss = settingsComponent?.enableMarkdownCss ?: false

        // Immediately re-apply CSS based on new setting if Monokai theme is active
        val lafManager = LafManager.getInstance()
        val isMonokaiTheme = lafManager.currentUIThemeLookAndFeel?.id == ThemeChangeListener.THEME_ID
        if (isMonokaiTheme) {
            ThemeChangeListener.applyMarkdownCss(settings.enableMarkdownCss)
        }
    }

    override fun reset() {
        val settings = MonokaiIslandsSettings.getInstance()
        settingsComponent?.enableMarkdownCss = settings.enableMarkdownCss
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
