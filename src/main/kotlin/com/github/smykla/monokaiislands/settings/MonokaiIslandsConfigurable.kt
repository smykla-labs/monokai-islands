package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class MonokaiIslandsConfigurable : Configurable {

    private var settingsComponent: MonokaiIslandsSettingsComponent? = null

    override fun getDisplayName(): String = "Monokai Islands"

    override fun createComponent(): JComponent {
        val component = MonokaiIslandsSettingsComponent()
        settingsComponent = component
        return component.panel
    }

    override fun isModified(): Boolean {
        val settings = MonokaiIslandsSettings.getInstance()
        val component = settingsComponent ?: return false
        return component.enableMarkdownCss != settings.enableMarkdownCss
    }

    override fun apply() {
        val settings = MonokaiIslandsSettings.getInstance()
        val component = settingsComponent ?: return

        settings.enableMarkdownCss = component.enableMarkdownCss

        // Immediately re-apply CSS based on new setting if Monokai theme is active
        val lafManager = LafManager.getInstance()
        val isMonokaiTheme = lafManager.currentUIThemeLookAndFeel?.id == ThemeChangeListener.THEME_ID
        if (isMonokaiTheme) {
            ThemeChangeListener.applyMarkdownCss(settings.enableMarkdownCss)
        }
    }

    override fun reset() {
        val settings = MonokaiIslandsSettings.getInstance()
        val component = settingsComponent ?: return
        component.enableMarkdownCss = settings.enableMarkdownCss
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
