package com.github.smykla.monokaiislands.ui.debug

import com.github.smykla.monokaiislands.ui.components.ScrollableWrapPanel
import com.github.smykla.monokaiislands.ui.components.WrapLayout
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.BasicStroke
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
import java.awt.RenderingHints
import javax.swing.BoxLayout
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JRootPane
import javax.swing.ScrollPaneConstants
import javax.swing.Timer
import javax.swing.UIManager
import javax.swing.border.AbstractBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

private const val SELECTION_ARC = 10
private const val SELECTION_THICKNESS = 2
private const val BUTTON_SIZE = 40
private const val GRID_HGAP = 5
private const val GRID_VGAP = 5
private const val GRID_BORDER_TOP = 5
private const val GRID_BORDER_LEFT = 3
private const val GAP_SIZE = 4
private const val INPUT_WIDTH = 200
private const val TEXT_FIELD_COLUMNS = 20
private const val MAX_TEXT_PREVIEW_LENGTH = 8
private const val MIN_PANEL_SIZE = 10
private const val LAYOUT_GAP = 10
private const val LAYOUT_VGAP = 5
private const val GRID_COLUMNS = 1
private const val GRID_ROWS = 3
private const val WEIGHT_NONE = 0.0
private const val WEIGHT_FULL = 1.0
private const val GRID_X_0 = 0
private const val GRID_X_1 = 1
private const val GRID_Y_0 = 0
private val DEFAULT_BORDER_COLOR = JBColor(0x3592C4, 0x3592C4)

@Suppress("LongMethod")
class InteractiveIconPanel : JPanel() {

    private var selectedIcon: Icon? = null
    private var buttonText = ""
    private var previewAnimationTimer: Timer? = null

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = LEFT_ALIGNMENT

        val icons = ThemeIcons.getAllIcons()

        val previewButtons = createPreviewButtons(buttonText)
        val updatePreview = createUpdateFunction(previewButtons)
        // Initialize preview labels/icons when text starts empty
        updatePreview(null, buttonText)

        val borderColor = UIManager.getColor("Button.default.focusedBorderColor") ?: DEFAULT_BORDER_COLOR
        val selectionBorder = object : AbstractBorder() {
            override fun paintBorder(
                c: Component,
                g: Graphics,
                x: Int,
                y: Int,
                width: Int,
                height: Int
            ) {
                val g2 = g.create() as Graphics2D
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
                g2.color = borderColor
                g2.stroke = BasicStroke(SELECTION_THICKNESS.toFloat())
                g2.drawRoundRect(
                    x + SELECTION_THICKNESS / 2,
                    y + SELECTION_THICKNESS / 2,
                    width - SELECTION_THICKNESS,
                    height - SELECTION_THICKNESS,
                    SELECTION_ARC,
                    SELECTION_ARC
                )
                g2.dispose()
            }

            override fun getBorderInsets(c: Component): Insets {
                return JBUI.insets(SELECTION_THICKNESS)
            }
        }

        val allButtons = mutableListOf<JButton>()

        // Create a panel with WrapLayout for automatic wrapping.
        // ScrollableWrapPanel tracks viewport width and revalidates on window resize.
        val gridPanel = ScrollableWrapPanel().apply {
            layout = WrapLayout(FlowLayout.LEFT, GRID_HGAP, GRID_VGAP)
            border = JBUI.Borders.empty(GRID_BORDER_TOP, GRID_BORDER_LEFT) // minimal horizontal padding
            minimumSize = JBUI.size(MIN_PANEL_SIZE, MIN_PANEL_SIZE)

            val buttonSize = JBUI.size(BUTTON_SIZE, BUTTON_SIZE)

            // Add the "None" button
            val noneButton = JButton().apply {
                toolTipText = "None"
                preferredSize = buttonSize
                minimumSize = buttonSize
                maximumSize = buttonSize
                border = selectionBorder
                addActionListener {
                    previewAnimationTimer?.stop()
                    previewAnimationTimer = null
                    allButtons.forEach { it.border = UIManager.getBorder("Button.border") }
                    border = selectionBorder
                    selectedIcon = null
                    updatePreview(null, buttonText)
                }
            }
            add(noneButton)
            allButtons.add(noneButton)

            // Add icon buttons
            icons.forEach { (label, icon) ->
                add(JButton(icon).apply {
                    toolTipText = label
                    preferredSize = buttonSize
                    minimumSize = buttonSize
                    maximumSize = buttonSize
                    addActionListener {
                        previewAnimationTimer?.stop()
                        previewAnimationTimer = null
                        allButtons.forEach { it.border = UIManager.getBorder("Button.border") }
                        border = selectionBorder
                        selectedIcon = icon
                        updatePreview(icon, buttonText)
                    }
                    allButtons.add(this)
                })
            }
        }

        val textInput = createTextInputPanel(buttonText) { text ->
            buttonText = text
            updatePreview(selectedIcon, text)
        }

        val previewPanel = createButtonPreviewPanel(previewButtons)

        val gap = JBUI.scale(GAP_SIZE)
        val inputAndPreviewRow = JPanel(GridBagLayout()).apply {
            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                gridy = GRID_Y_0
            }

            gbc.gridx = GRID_X_0
            gbc.weightx = WEIGHT_NONE
            gbc.insets = JBUI.insetsRight(gap)
            add(textInput, gbc)

            gbc.gridx = GRID_X_1
            gbc.weightx = WEIGHT_FULL
            gbc.insets = JBUI.emptyInsets()
            add(previewPanel, gbc)

            alignmentX = LEFT_ALIGNMENT
        }

        // Wrap grid in its own scroll pane (no horizontal scrollbar) so width always equals the viewport,
        // preventing initial overflow while still allowing vertical scroll when content is tall.
        val gridScroll = object : JBScrollPane(gridPanel) {
            override fun getMinimumSize(): Dimension = Dimension(MIN_PANEL_SIZE, MIN_PANEL_SIZE)
        }.apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            border = null
        }

        add(gridScroll.apply { alignmentX = LEFT_ALIGNMENT })
        add(inputAndPreviewRow)
        minimumSize = JBUI.size(MIN_PANEL_SIZE, MIN_PANEL_SIZE)
    }

    private fun createPreviewButtons(text: String): Triple<JButton, JButton, JPanel> {
        val normal = JButton(text)
        val disabled = JButton(text).apply { isEnabled = false }
        val defaultBtn = JButton(text).apply { isDefaultCapable = true }
        val defaultContainer = JRootPane().apply {
            defaultButton = defaultBtn
            contentPane.add(defaultBtn)
        }.let { rootPane ->
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(rootPane)
                isOpaque = false
            }
        }
        return Triple(normal, disabled, defaultContainer)
    }

    private fun createUpdateFunction(
        buttons: Triple<JButton, JButton, JPanel>
    ): (Icon?, String) -> Unit = { icon, text ->
        val resolvedNormal = text.ifBlank { "Normal" }
        val resolvedDisabled = text.ifBlank { "Disabled" }
        val resolvedDefault = text.ifBlank { "Default" }
        val clippedNormal = StringUtil.shortenTextWithEllipsis(resolvedNormal, MAX_TEXT_PREVIEW_LENGTH, 0, true)
        val clippedDisabled = StringUtil.shortenTextWithEllipsis(resolvedDisabled, MAX_TEXT_PREVIEW_LENGTH, 0, true)
        val clippedDefault = StringUtil.shortenTextWithEllipsis(resolvedDefault, MAX_TEXT_PREVIEW_LENGTH, 0, true)

        buttons.first.icon = icon
        buttons.first.text = clippedNormal
        buttons.second.icon = icon
        buttons.second.text = clippedDisabled
        val defaultBtn = (buttons.third.components[0] as JRootPane).defaultButton
        defaultBtn.icon = icon
        defaultBtn.text = clippedDefault
    }

    private fun createTextInputPanel(
        initialText: String,
        onTextChanged: (String) -> Unit
    ): JPanel {
        val buttonHeight = JButton().preferredSize.height
        val preferredWidth = JBUI.scale(INPUT_WIDTH)
        val textField = ExtendableTextField(initialText, TEXT_FIELD_COLUMNS).apply {
            preferredSize = Dimension(preferredWidth, buttonHeight)
            minimumSize = Dimension(preferredWidth, buttonHeight)
            emptyText.text = "Button text..."
        }

        lateinit var clearExtension: ExtendableTextComponent.Extension

        fun updateExtensions(field: ExtendableTextField, ext: ExtendableTextComponent.Extension) {
            if (field.text.isNullOrBlank()) {
                field.setExtensions(emptyList())
            } else {
                field.setExtensions(listOf(ext))
            }
        }

        clearExtension = ExtendableTextComponent.Extension.create(
            AllIcons.Actions.Close,
            "Clear"
        ) {
            textField.text = ""
            onTextChanged("")
            updateExtensions(textField, clearExtension)
        }

        // Update preview on text changes
        textField.addActionListener {
            onTextChanged(textField.text)
            updateExtensions(textField, clearExtension)
        }
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                onTextChanged(textField.text)
                updateExtensions(textField, clearExtension)
            }
            override fun removeUpdate(e: DocumentEvent?) {
                onTextChanged(textField.text)
                updateExtensions(textField, clearExtension)
            }
            override fun changedUpdate(e: DocumentEvent?) {
                onTextChanged(textField.text)
                updateExtensions(textField, clearExtension)
            }
        })

        // Initialize extensions for initial state
        updateExtensions(textField, clearExtension)

        return JPanel(FlowLayout(FlowLayout.LEFT, LAYOUT_GAP, LAYOUT_VGAP)).apply { add(textField) }
    }

    private fun createButtonPreviewPanel(
        buttons: Triple<JButton, JButton, JPanel>
    ): JPanel {
        val gap = JBUI.scale(GAP_SIZE)
        return JPanel(GridLayout(GRID_COLUMNS, GRID_ROWS, gap, 0)).apply {
            border = JBUI.Borders.empty()
            add(buttons.first)
            add(buttons.second)
            add(buttons.third)
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
    }
}
