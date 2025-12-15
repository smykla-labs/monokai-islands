package com.github.smykla.monokaiislands.settings

import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.TopGap
import com.github.smykla.monokaiislands.utils.ScrollableWrapPanel
import com.github.smykla.monokaiislands.utils.WrapLayout
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.awt.RelativePoint
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.project.Project
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.ColorPanel
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.SearchTextField
import com.intellij.ui.table.JBTable
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Point
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFormattedTextField
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JProgressBar
import javax.swing.JRadioButton
import javax.swing.JSeparator
import javax.swing.JSlider
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.border.TitledBorder
import javax.swing.table.DefaultTableModel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

@Suppress("MagicNumber", "TooManyFunctions", "LongMethod")
class ThemeTestingComponent {

    private val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
        .getNotificationGroup("Monokai Islands Theme Test")

    val panel = panel {
        group("Theme Testing Panel") {
            row {
                text("""
                    Visual testing for theme changes during development.
                    This panel is only visible in dev mode (idea.is.internal=true).
                """.trimIndent())
            }
        }

        group("Buttons") {
            row {
                cell(createIconsPanel())
                    .align(AlignX.FILL)
            }.resizableRow()
        }

        group("Toolbar & Splitter") {
            row {
                cell(createToolbarPanel())
                    .align(AlignX.FILL)
            }
            row {
                cell(createSplitterSample())
                    .align(AlignX.FILL)
            }.bottomGap(BottomGap.SMALL)
        }

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
                cell(JPasswordField("secret", 10))
                label("Search:").gap(RightGap.SMALL)
                cell(createSearchField())
            }
            row("Formatted:") {
                cell(JFormattedTextField(java.text.DecimalFormat("#,###")).apply {
                    value = 12345
                    columns = 8
                })
                label("Color:").gap(RightGap.SMALL)
                cell(ColorPanel().apply { selectedColor = Color(0x66CCFF) })
            }.bottomGap(BottomGap.NONE)

            // Selection controls (kept within Input Elements for consistency)
            buttonsGroup {
                row("Radio Buttons:") {
                    radioButton("Option 1")
                    radioButton("Option 2")
                }
            }
            row("Slider:") {
                cell(JSlider(0, 100, 50).apply {
                    preferredSize = JBUI.size(200, 30)
                })
            }.bottomGap(BottomGap.SMALL)
        }

        group("Lists & Trees") {
            row {
                // Small top spacer to separate headers from bordered boxes
                cell(Box.createVerticalStrut(JBUI.scale(4)) as JComponent)
            }.topGap(TopGap.NONE).bottomGap(BottomGap.NONE)

            row {
                val listComponent = JBList(listOf("Item 1", "Item 2", "Item 3 (Selected)")).apply {
                    selectedIndex = 2
                    border = JBUI.Borders.emptyLeft(5)
                }
                val listPanel = createTitledPanel("List", listComponent, 120)

                val root = DefaultMutableTreeNode("Root")
                val expanded = DefaultMutableTreeNode("Expanded")
                expanded.add(DefaultMutableTreeNode("Child 1"))
                expanded.add(DefaultMutableTreeNode("Child 2"))
                root.add(expanded)
                root.add(DefaultMutableTreeNode("Collapsed"))

                val treeComponent = Tree(DefaultTreeModel(root)).apply {
                    expandRow(1)
                }
                val treePanel = createTitledPanel("Tree", treeComponent, 120)

                val container = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.X_AXIS)
                    isOpaque = false
                    add(JPanel(BorderLayout()).apply {
                        isOpaque = false
                        border = JBUI.Borders.empty() // no extra left inset
                        add(listPanel, BorderLayout.CENTER)
                    })
                    add(Box.createHorizontalStrut(JBUI.scale(6))) // smaller gap between boxes
                    add(JPanel(BorderLayout()).apply {
                        isOpaque = false
                        border = JBUI.Borders.emptyRight(JBUI.scale(1)) // minimal right inset
                        add(treePanel, BorderLayout.CENTER)
                    })
                }

                cell(container)
                    .align(AlignX.FILL)
            }.topGap(TopGap.NONE)
        }.bottomGap(BottomGap.SMALL)

        group("Tables & Data") {
            row {
                cell(createTableSample())
                    .align(AlignX.FILL)
            }.bottomGap(BottomGap.SMALL)
        }

        group("Progress & Status") {
            row("Progress Bars:") {
                cell(JProgressBar(0, 100).apply { value = 45; preferredSize = JBUI.size(200, 20) })
                cell(JProgressBar().apply { isIndeterminate = true; preferredSize = JBUI.size(150, 20) })
            }
            row {
                label("Async Icon:")
                cell(AsyncProcessIcon("theme-test").apply { preferredSize = JBUI.size(32, 16) })
            }.bottomGap(BottomGap.SMALL)
        }

        group("Validation & Feedback") {
            row {
                cell(createOutlinedField("OK", null))
                cell(createOutlinedField("Warning", "warning"))
                cell(createOutlinedField("Error", "error"))
            }.bottomGap(BottomGap.SMALL)
        }

        group("Notifications & Popups") {
            row {
                cell(createNotificationButtons())
            }
            row {
                button("Show Popup Menu") {
                    showListPopup()
                }
                button("Show Dialog") {
                    Messages.showInfoMessage(null as Project?, "Sample dialog body", "Dialog Title")
                }
            }.bottomGap(BottomGap.MEDIUM)
        }

        group("Borders & Separators") {
            row {
                comment("Example of titled border with content")
            }
            row {
                cell(JPanel().apply {
                    layout = FlowLayout(FlowLayout.LEFT, 10, 5)
                    border = TitledBorder("Titled Border Panel")
                    add(JBLabel("Content inside titled border"))
                    add(JBCheckBox("Checkbox example"))
                    preferredSize = JBUI.size(400, 60)
                })
            }.topGap(TopGap.MEDIUM)

            row {
                label("Horizontal Separator:")
            }.topGap(TopGap.MEDIUM)
            row {
                cell(JSeparator(SwingConstants.HORIZONTAL).apply {
                    preferredSize = JBUI.size(300, 2)
                })
            }
            row {
                label("Text after separator")
            }.bottomGap(BottomGap.MEDIUM)
        }

        group("Status Indicators") {
            row {
                label("ℹ️ Info Message")
            }
            row {
                label("⚠️ Warning Message")
            }
            row {
                label("❌ Error Message")
            }
            row("Progress Bar:") {
                cell(JProgressBar(0, 100).apply {
                    value = 65
                    preferredSize = JBUI.size(200, 20)
                })
            }.bottomGap(BottomGap.MEDIUM)
        }
    }

    private fun createIconsPanel(): JPanel {
        val allIcons = getActionsIcons() + getGeneralIcons() + getFileTypesIcons() +
            getNodesIcons() + getDebuggerIcons() + getCodeIcons()
        return createInteractiveIconPanel(allIcons)
    }

    private fun createInteractiveIconPanel(icons: List<Pair<String, Icon>>): JPanel {
        var selectedIcon: Icon? = null
        var buttonText = ""

        val previewButtons = createPreviewButtons(buttonText)
        val updatePreview = createUpdateFunction(previewButtons)
        // Initialize preview labels/icons when text starts empty
        updatePreview(null, buttonText)

        // Timer for animating preview buttons when spinner is selected
        var previewAnimationTimer: javax.swing.Timer? = null

        val borderColor = UIManager.getColor("Button.default.focusedBorderColor") ?: Color(0x3592C4)
        val selectionBorder = object : javax.swing.border.AbstractBorder() {
            private val arc = 10
            private val thickness = 2

            override fun paintBorder(
                c: java.awt.Component,
                g: java.awt.Graphics,
                x: Int,
                y: Int,
                width: Int,
                height: Int
            ) {
                val g2 = g.create() as java.awt.Graphics2D
                g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                )
                g2.color = borderColor
                g2.stroke = java.awt.BasicStroke(thickness.toFloat())
                g2.drawRoundRect(
                    x + thickness / 2,
                    y + thickness / 2,
                    width - thickness,
                    height - thickness,
                    arc,
                    arc
                )
                g2.dispose()
            }

            override fun getBorderInsets(c: java.awt.Component): java.awt.Insets {
                return java.awt.Insets(thickness, thickness, thickness, thickness)
            }
        }

        val allButtons = mutableListOf<JButton>()

        // Create panel with WrapLayout for automatic wrapping.
        // ScrollableWrapPanel tracks viewport width and revalidates on window resize.
        val gridPanel = ScrollableWrapPanel().apply {
            layout = WrapLayout(FlowLayout.LEFT, 5, 5)
            border = JBUI.Borders.empty(5, 3) // minimal horizontal padding
            minimumSize = JBUI.size(10, 10)

            val buttonSize = JBUI.size(40, 40)

            // Add "None" button
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

        val gap = JBUI.scale(4)
        val inputAndPreviewRow = JPanel(GridBagLayout()).apply {
            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                gridy = 0
            }

            gbc.gridx = 0
            gbc.weightx = 0.0
            gbc.insets = JBUI.insetsRight(gap)
            add(textInput, gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            gbc.insets = JBUI.emptyInsets()
            add(previewPanel, gbc)

            alignmentX = java.awt.Component.LEFT_ALIGNMENT
        }

        // Wrap grid in its own scroll pane (no horizontal scrollbar) so width always equals the viewport,
        // preventing initial overflow while still allowing vertical scroll when content is tall.
        val gridScroll = object : com.intellij.ui.components.JBScrollPane(gridPanel) {
            override fun getMinimumSize(): java.awt.Dimension = java.awt.Dimension(10, 10)
        }.apply {
            horizontalScrollBarPolicy = javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBarPolicy = javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            border = null
        }

        // Stack scrollable grid + controls vertically; outer settings scrollpane remains intact.
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = java.awt.Component.LEFT_ALIGNMENT
            add(gridScroll.apply { alignmentX = java.awt.Component.LEFT_ALIGNMENT })
            add(inputAndPreviewRow)
            minimumSize = JBUI.size(10, 10)
        }
    }

    private fun createToolbarPanel(): JComponent {
        val group = DefaultActionGroup().apply {
            add(object : AnAction("Run", "Run action", AllIcons.Actions.Execute) {
                override fun actionPerformed(e: AnActionEvent) = Unit
            })
            add(object : AnAction("Build", "Build action", AllIcons.Actions.Compile) {
                override fun actionPerformed(e: AnActionEvent) = Unit
            })
            addSeparator()
            add(object : AnAction("Stop", "Stop action", AllIcons.Actions.Suspend) {
                override fun actionPerformed(e: AnActionEvent) = Unit
            })
        }
        val toolbar = ActionManager.getInstance()
            .createActionToolbar("MonokaiIslandsThemeToolbar", group, true)
        val wrapper = JPanel(BorderLayout())
        toolbar.targetComponent = wrapper
        wrapper.add(toolbar.component, BorderLayout.CENTER)
        return wrapper
    }

    private fun createSplitterSample(): JComponent {
        val splitter = OnePixelSplitter(false, 0.35f).apply {
            firstComponent = JPanel(BorderLayout()).apply {
                border = JBUI.Borders.empty(8)
                add(JBLabel("Left panel"), BorderLayout.CENTER)
            }
            secondComponent = JPanel(BorderLayout()).apply {
                border = JBUI.Borders.empty(8)
                add(JBLabel("Right panel"), BorderLayout.CENTER)
            }
            setHonorComponentsMinimumSize(true)
        }
        return splitter
    }

    private fun createSearchField(): JComponent =
        SearchTextField().apply {
            text = "Search..."
            preferredSize = JBUI.size(160, 28)
        }

    private fun createTableSample(): JComponent {
        val model = DefaultTableModel(
            arrayOf(
                arrayOf("Alice", "Online", "✔"),
                arrayOf("Bob", "Idle", "…"),
                arrayOf("Carol", "Offline", "×")
            ),
            arrayOf("Name", "Status", "Indicator")
        )
        return JBTable(model).apply {
            preferredScrollableViewportSize = Dimension(420, 90)
            showHorizontalLines = false
            showVerticalLines = false
            border = JBUI.Borders.empty(4)
        }
    }

    private fun createNotificationButtons(): JComponent =
        JPanel(FlowLayout(FlowLayout.LEFT, 10, 5)).apply {
            fun addBtn(label: String, type: NotificationType) {
                val btn = JButton(label)
                btn.addActionListener { showNotification(btn, "Theme Test", "$label sample", type) }
                add(btn)
            }
            addBtn("Info Notification", NotificationType.INFORMATION)
            addBtn("Warning Notification", NotificationType.WARNING)
            addBtn("Error Notification", NotificationType.ERROR)
        }

    private fun createOutlinedField(label: String, outline: String?): JComponent =
        JPanel(FlowLayout(FlowLayout.LEFT, 5, 5)).apply {
            add(JBLabel("$label:"))
            add(JBTextField(label.lowercase(), 10).apply {
                if (outline != null) {
                    putClientProperty("JComponent.outline", outline)
                }
            })
        }

    private fun showNotification(anchor: JComponent, title: String, content: String, type: NotificationType) {
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        ApplicationManager.getApplication().invokeLater {
            // Always show a local balloon so it works inside Settings even without a project
            val messageType = when (type) {
                NotificationType.INFORMATION -> MessageType.INFO
                NotificationType.WARNING -> MessageType.WARNING
                NotificationType.ERROR -> MessageType.ERROR
                NotificationType.IDE_UPDATE -> MessageType.INFO
            }
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(content, messageType, null)
                .setFadeoutTime(5000)
                .createBalloon()
                .show(
                    RelativePoint(anchor, Point(anchor.width / 2, anchor.height)),
                    com.intellij.openapi.ui.popup.Balloon.Position.below
                )

            // Also notify project (if any) so the standard notification area shows it
            val notification = notificationGroup.createNotification(title, content, type)
            notification.notify(project)
        }
    }

    private fun showListPopup() {
        val group = DefaultActionGroup().apply {
            add(object : AnAction("First item") {
                override fun actionPerformed(e: AnActionEvent) = Unit
            })
            add(object : AnAction("Second item (disabled)") {
                override fun actionPerformed(e: AnActionEvent) = Unit
                override fun update(e: AnActionEvent) { e.presentation.isEnabled = false }
            })
            addSeparator()
            add(object : AnAction("Third item") {
                override fun actionPerformed(e: AnActionEvent) = Unit
            })
        }
        JBPopupFactory.getInstance()
            .createActionGroupPopup(
                "Popup Menu",
                group,
                DataContext.EMPTY_CONTEXT,
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                true
            )
            .showInFocusCenter()
    }

    private fun createPreviewButtons(text: String): Triple<JButton, JButton, JPanel> {
        val normal = JButton(text)
        val disabled = JButton(text).apply { isEnabled = false }
        val defaultBtn = JButton(text).apply { isDefaultCapable = true }
        val defaultContainer = javax.swing.JRootPane().apply {
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
        val resolvedNormal = if (text.isBlank()) "Normal" else text
        val resolvedDisabled = if (text.isBlank()) "Disabled" else text
        val resolvedDefault = if (text.isBlank()) "Default" else text
        val maxLen = 8
        val clippedNormal = StringUtil.shortenTextWithEllipsis(resolvedNormal, maxLen, 0, true)
        val clippedDisabled = StringUtil.shortenTextWithEllipsis(resolvedDisabled, maxLen, 0, true)
        val clippedDefault = StringUtil.shortenTextWithEllipsis(resolvedDefault, maxLen, 0, true)

        buttons.first.icon = icon
        buttons.first.text = clippedNormal
        buttons.second.icon = icon
        buttons.second.text = clippedDisabled
        val defaultBtn = (buttons.third.components[0] as javax.swing.JRootPane).defaultButton
        defaultBtn.icon = icon
        defaultBtn.text = clippedDefault
    }

    private fun createTextInputPanel(
        initialText: String,
        onTextChanged: (String) -> Unit
    ): JPanel {
        val buttonHeight = JButton().preferredSize.height
        val preferredWidth = JBUI.scale(200)
        val textField = ExtendableTextField(initialText, 20).apply {
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
        textField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                onTextChanged(textField.text); updateExtensions(textField, clearExtension)
            }
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                onTextChanged(textField.text); updateExtensions(textField, clearExtension)
            }
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                onTextChanged(textField.text); updateExtensions(textField, clearExtension)
            }
        })

        // Initialize extensions for initial state
        updateExtensions(textField, clearExtension)

        return JPanel(FlowLayout(FlowLayout.LEFT, 10, 5)).apply { add(textField) }
    }

    private fun createButtonPreviewPanel(
        buttons: Triple<JButton, JButton, JPanel>
    ): JPanel {
        val gap = JBUI.scale(4)
        return JPanel(GridLayout(1, 3, gap, 0)).apply {
            border = JBUI.Borders.empty()
            add(buttons.first)
            add(buttons.second)
            add(buttons.third)
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
    }

    private fun getActionsIcons() = listOf(
        "Execute" to AllIcons.Actions.Execute,
        "Compile" to AllIcons.Actions.Compile,
        "Suspend" to AllIcons.Actions.Suspend,
        "Cancel" to AllIcons.Actions.Cancel,
        "Refresh" to AllIcons.Actions.Refresh,
        "Search" to AllIcons.Actions.Search,
        "Find" to AllIcons.Actions.Find,
        "Edit" to AllIcons.Actions.Edit,
        "Close" to AllIcons.Actions.Close,
        "StartDebugger" to AllIcons.Actions.StartDebugger,
        "Resume" to AllIcons.Actions.Resume,
        "Pause" to AllIcons.Actions.Pause,
        "Restart" to AllIcons.Actions.Restart
    )

    private fun getGeneralIcons() = listOf(
        "Add" to AllIcons.General.Add,
        "Remove" to AllIcons.General.Remove,
        "Settings" to AllIcons.General.Settings,
        "User" to AllIcons.General.User,
        "Information" to AllIcons.General.Information,
        "Warning" to AllIcons.General.Warning,
        "Error" to AllIcons.General.Error,
        "BalloonInformation" to AllIcons.General.BalloonInformation,
        "BalloonWarning" to AllIcons.General.BalloonWarning,
        "InspectionsOK" to AllIcons.General.InspectionsOK
    )

    private fun getFileTypesIcons() = listOf(
        "Text" to AllIcons.FileTypes.Text,
        "Json" to AllIcons.FileTypes.Json,
        "Xml" to AllIcons.FileTypes.Xml,
        "Html" to AllIcons.FileTypes.Html,
        "Yaml" to AllIcons.FileTypes.Yaml,
        "Archive" to AllIcons.FileTypes.Archive,
        "Any_type" to AllIcons.FileTypes.Any_type,
        "Unknown" to AllIcons.FileTypes.Unknown
    )

    private fun getNodesIcons() = listOf(
        "Folder" to AllIcons.Nodes.Folder,
        "Module" to AllIcons.Nodes.Module,
        "Package" to AllIcons.Nodes.Package,
        "Class" to AllIcons.Nodes.Class,
        "Method" to AllIcons.Nodes.Method,
        "Field" to AllIcons.Nodes.Field,
        "Property" to AllIcons.Nodes.Property,
        "Interface" to AllIcons.Nodes.Interface
    )

    private fun getDebuggerIcons() = listOf(
        "StartDebugger" to AllIcons.Actions.StartDebugger,
        "Resume" to AllIcons.Actions.Resume,
        "Pause" to AllIcons.Actions.Pause,
        "Restart" to AllIcons.Actions.Restart,
        "TraceOver" to AllIcons.Actions.TraceOver,
        "TraceInto" to AllIcons.Actions.TraceInto,
        "StepOut" to AllIcons.Actions.StepOut
    )

    private fun getCodeIcons() = listOf(
        "IntentionBulb" to AllIcons.Actions.IntentionBulb,
        "Lightning" to AllIcons.Actions.Lightning,
        "QuickfixBulb" to AllIcons.Actions.QuickfixBulb,
        "QuickfixOffBulb" to AllIcons.Actions.QuickfixOffBulb,
        "RealIntentionBulb" to AllIcons.Actions.RealIntentionBulb,
        "ShowCode" to AllIcons.Actions.ShowCode
    )

    private fun createTitledPanel(title: String, content: JComponent, preferredHeight: Int? = null): JPanel {
        return JPanel(BorderLayout()).apply {
            border = TitledBorder(title)
            add(content, BorderLayout.CENTER)
            // Ensure content doesn't get clipped if it's smaller than the preferredSize
            minimumSize = JBUI.size(10, 10)
            if (preferredHeight != null) {
                preferredSize = JBUI.size(0, preferredHeight)
            }
        }
    }
}
