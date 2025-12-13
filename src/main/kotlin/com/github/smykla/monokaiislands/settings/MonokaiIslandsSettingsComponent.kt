package com.github.smykla.monokaiislands.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class MonokaiIslandsSettingsComponent {

    private val enableMarkdownCssCheckBox = JBCheckBox(
        "Enable custom Markdown preview styling (when Monokai Islands theme is active)"
    )

    val panel: JPanel = FormBuilder.createFormBuilder()
        .addComponent(enableMarkdownCssCheckBox)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    var enableMarkdownCss: Boolean
        get() = enableMarkdownCssCheckBox.isSelected
        set(value) {
            enableMarkdownCssCheckBox.isSelected = value
        }
}
