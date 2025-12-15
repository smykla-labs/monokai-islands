package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.github.smykla.monokaiislands.utils.DevModeDetector
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MonokaiIslandsConfigurable : SearchableConfigurable {

    private var settingsComponent: MonokaiIslandsSettingsComponent? = null
    private var themeTestingComponent: ThemeTestingComponent? = null
    private val devSectionGap = JBUI.scale(16)

    override fun getId(): String =
        "com.github.smykla.monokaiislands.settings.MonokaiIslandsConfigurable"

    override fun getDisplayName(): String = "Monokai Islands"

    override fun createComponent(): JComponent {
        if (settingsComponent == null) {
            settingsComponent = MonokaiIslandsSettingsComponent()
        }

        if (!DevModeDetector.isDevMode()) {
            return settingsComponent?.panel
                ?: error("settingsComponent should not be null after initialization")
        }

        if (themeTestingComponent == null) {
            themeTestingComponent = ThemeTestingComponent()
        }

        val stacked = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(settingsComponent?.panel)
            add(Box.createVerticalStrut(devSectionGap))
            add(themeTestingComponent?.panel)
        }

        return JBScrollPane(stacked).apply { border = null }
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
        themeTestingComponent = null
    }
}
