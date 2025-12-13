package com.github.smykla.monokaiislands.settings

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class MonokaiIslandsConfigurable : SearchableConfigurable {

    private var enableMarkdownCssCheckBox: JBCheckBox? = null

    override fun getId(): String =
        "com.github.smykla.monokaiislands.settings.MonokaiIslandsConfigurable"

    override fun getDisplayName(): String = "Monokai Islands"

    override fun createComponent(): JComponent {
        enableMarkdownCssCheckBox = JBCheckBox("Enable custom Markdown preview styling")
        return FormBuilder.createFormBuilder()
            .addComponent(enableMarkdownCssCheckBox!!)
            .addComponentFillVertically(javax.swing.JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val settings = MonokaiIslandsSettings.getInstance()
        return enableMarkdownCssCheckBox?.isSelected != settings.enableMarkdownCss
    }

    override fun apply() {
        val settings = MonokaiIslandsSettings.getInstance()
        settings.enableMarkdownCss = enableMarkdownCssCheckBox?.isSelected ?: true

        // Immediately re-apply CSS based on new setting if Monokai theme is active
        val lafManager = LafManager.getInstance()
        val isMonokaiTheme = lafManager.currentUIThemeLookAndFeel?.id == ThemeChangeListener.THEME_ID
        if (isMonokaiTheme) {
            ThemeChangeListener.applyMarkdownCss(settings.enableMarkdownCss)
        }
    }

    override fun reset() {
        val settings = MonokaiIslandsSettings.getInstance()
        enableMarkdownCssCheckBox?.isSelected = settings.enableMarkdownCss
    }

    override fun disposeUIResources() {
        enableMarkdownCssCheckBox = null
    }
}
