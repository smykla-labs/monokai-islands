package com.github.smykla.monokaiislands.ui.debug

import com.intellij.ui.ColorPanel
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.util.ui.JBUI
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JFormattedTextField
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JSlider

private const val SEARCH_FIELD_WIDTH = 160
private const val SEARCH_FIELD_HEIGHT = 28
private const val PASSWORD_FIELD_COLUMNS = 10
private const val FORMATTED_FIELD_VALUE = 12345L
private const val FORMATTED_FIELD_COLUMNS = 8
private const val SLIDER_MIN = 0
private const val SLIDER_MAX = 100
private const val SLIDER_INIT = 50
private const val SLIDER_WIDTH = 200
private const val SLIDER_HEIGHT = 30
private const val OUTLINED_FIELD_GAP = 5
private const val OUTLINED_FIELD_COLUMNS = 10
private val COLOR_CYAN = JBColor(0x66CCFF, 0x66CCFF)

fun Panel.buttonsGroup() {
    group("Buttons") {
        row {
            cell(InteractiveIconPanel())
                .align(AlignX.FILL)
        }.resizableRow()
    }
}

fun Panel.inputGalleryGroup() {
    group("Input Elements") {
        row("Text Field:") {
            textField()
                .apply { component.text = "Sample text" }
        }
        row("Combo Box:") {
            comboBox(listOf("Option 1", "Option 2", "Option 3"))
        }
        row {
            checkBox("Enabled Checkbox")
                .apply { component.isSelected = true }
            checkBox("Disabled Checkbox")
                .apply {
                    component.isSelected = false
                    component.isEnabled = false
                }
        }
        row {
            label("Password:")
            cell(JPasswordField("secret", PASSWORD_FIELD_COLUMNS))
            label("Search:").gap(RightGap.SMALL)
            cell(createSearchField())
        }
        row("Formatted:") {
            cell(JFormattedTextField(java.text.DecimalFormat("#,###")).apply {
                value = FORMATTED_FIELD_VALUE
                columns = FORMATTED_FIELD_COLUMNS
            })
            label("Color:").gap(RightGap.SMALL)
            cell(ColorPanel().apply { selectedColor = COLOR_CYAN })
        }.bottomGap(BottomGap.NONE)

        // Selection controls (kept within Input Elements for consistency)
        buttonsGroup {
            row("Radio Buttons:") {
                radioButton("Option 1")
                radioButton("Option 2")
            }
        }
        row("Slider:") {
            cell(JSlider(SLIDER_MIN, SLIDER_MAX, SLIDER_INIT).apply {
                preferredSize = JBUI.size(SLIDER_WIDTH, SLIDER_HEIGHT)
            })
        }.bottomGap(BottomGap.SMALL)
    }
}

fun Panel.validationFeedbackGroup() {
    group("Validation & Feedback") {
        row {
            cell(createOutlinedField("OK", null))
            cell(createOutlinedField("Warning", "warning"))
            cell(createOutlinedField("Error", "error"))
        }.bottomGap(BottomGap.SMALL)
    }
}

private fun createSearchField(): JComponent =
    SearchTextField().apply {
        text = "Search..."
        preferredSize = JBUI.size(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HEIGHT)
    }

private fun createOutlinedField(label: String, outline: String?): JComponent =
    JPanel(FlowLayout(FlowLayout.LEFT, OUTLINED_FIELD_GAP, OUTLINED_FIELD_GAP)).apply {
        add(JBLabel("$label:"))
        add(JBTextField(label.lowercase(), OUTLINED_FIELD_COLUMNS).apply {
            if (outline != null) {
                putClientProperty("JComponent.outline", outline)
            }
        })
    }
