package com.github.smykla.monokaiislands.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.panel
import javax.swing.JPanel

class MonokaiIslandsSettingsComponent {

    private val enableMarkdownCssCheckBox = JBCheckBox(
        "Enable custom Markdown preview styling (when Monokai Islands theme is active)"
    )

    val panel: JPanel = panel {
        group("General") {
            row {
                cell(enableMarkdownCssCheckBox)
            }
        }.bottomGap(BottomGap.NONE)
    }

    var enableMarkdownCss: Boolean
        get() = enableMarkdownCssCheckBox.isSelected
        set(value) {
            enableMarkdownCssCheckBox.isSelected = value
        }
}
