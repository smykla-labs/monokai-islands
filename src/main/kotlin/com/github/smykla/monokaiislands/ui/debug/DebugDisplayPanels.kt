package com.github.smykla.monokaiislands.ui.debug

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.table.JBTable
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JSeparator
import javax.swing.SwingConstants
import javax.swing.border.TitledBorder
import javax.swing.table.DefaultTableModel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

private const val LIST_PANEL_HEIGHT = 120
private const val TABLE_WIDTH = 420
private const val TABLE_HEIGHT = 90
private const val PROGRESS_INDETERMINATE_WIDTH = 150
private const val ASYNC_ICON_WIDTH = 32
private const val ASYNC_ICON_HEIGHT = 16
private const val SPLITTER_PROPORTION = 0.35f
private const val BORDER_EMPTY = 8
private const val SEPARATOR_WIDTH = 300
private const val SEPARATOR_HEIGHT = 2
private const val TITLED_PANEL_WIDTH = 400
private const val TITLED_PANEL_HEIGHT = 60
private const val FLOW_LAYOUT_HGAP = 10
private const val FLOW_LAYOUT_VGAP = 5
private const val VERTICAL_STRUT_SMALL = 4
private const val HORIZONTAL_STRUT_SMALL = 6
private const val LIST_BORDER_LEFT = 5
private const val TREE_EXPAND_ROW = 1
private const val TABLE_BORDER = 4
private const val MIN_PANEL_SIZE = 10
private const val ZERO_SIZE = 0

fun Panel.toolbarAndSplitterGroup() {
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
}

fun Panel.treeListGalleryGroup() {
    group("Lists & Trees") {
        row {
            // Small top spacer to separate headers from bordered boxes
            cell(Box.createVerticalStrut(JBUI.scale(VERTICAL_STRUT_SMALL)) as JComponent)
        }.topGap(TopGap.NONE).bottomGap(BottomGap.NONE)

        row {
            val listComponent = JBList(listOf("Item 1", "Item 2", "Item 3 (Selected)")).apply {
                selectedIndex = 2
                border = JBUI.Borders.emptyLeft(LIST_BORDER_LEFT)
            }
            val listPanel = createTitledPanel("List", listComponent, LIST_PANEL_HEIGHT)

            val root = DefaultMutableTreeNode("Root")
            val expanded = DefaultMutableTreeNode("Expanded")
            expanded.add(DefaultMutableTreeNode("Child 1"))
            expanded.add(DefaultMutableTreeNode("Child 2"))
            root.add(expanded)
            root.add(DefaultMutableTreeNode("Collapsed"))

            val treeComponent = Tree(DefaultTreeModel(root)).apply {
                expandRow(TREE_EXPAND_ROW)
            }
            val treePanel = createTitledPanel("Tree", treeComponent, LIST_PANEL_HEIGHT)

            val container = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                isOpaque = false
                add(JPanel(BorderLayout()).apply {
                    isOpaque = false
                    border = JBUI.Borders.empty() // no extra left inset
                    add(listPanel, BorderLayout.CENTER)
                })
                add(Box.createHorizontalStrut(JBUI.scale(HORIZONTAL_STRUT_SMALL)))
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
}

fun Panel.tablesAndDataGroup() {
    group("Tables & Data") {
        row {
            cell(createTableSample())
                .align(AlignX.FILL)
        }.bottomGap(BottomGap.SMALL)
    }
}

fun Panel.progressAndStatusGroup() {
    group("Progress & Status") {
        row("Progress Bars:") {
            cell(JProgressBar(0, PROGRESS_MAX_VALUE).apply {
                value = PROGRESS_DEFAULT_VALUE
                preferredSize = JBUI.size(PROGRESS_WIDTH, PROGRESS_HEIGHT)
            })
            cell(JProgressBar().apply {
                isIndeterminate = true
                preferredSize = JBUI.size(PROGRESS_INDETERMINATE_WIDTH, PROGRESS_HEIGHT)
            })
        }
        row {
            label("Async Icon:")
            cell(AsyncProcessIcon("theme-test").apply {
                preferredSize = JBUI.size(ASYNC_ICON_WIDTH, ASYNC_ICON_HEIGHT)
            })
        }.bottomGap(BottomGap.SMALL)
    }
}

fun Panel.bordersAndSeparatorsGroup() {
    group("Borders & Separators") {
        row {
            comment("Example of a titled border with content")
        }
        row {
            cell(JPanel().apply {
                layout = FlowLayout(FlowLayout.LEFT, FLOW_LAYOUT_HGAP, FLOW_LAYOUT_VGAP)
                border = TitledBorder("Titled Border Panel")
                add(JBLabel("Content inside titled border"))
                add(JBCheckBox("Checkbox example"))
                preferredSize = JBUI.size(TITLED_PANEL_WIDTH, TITLED_PANEL_HEIGHT)
            })
        }.topGap(TopGap.MEDIUM)

        row {
            label("Horizontal Separator:")
        }.topGap(TopGap.MEDIUM)
        row {
            cell(JSeparator(SwingConstants.HORIZONTAL).apply {
                preferredSize = JBUI.size(SEPARATOR_WIDTH, SEPARATOR_HEIGHT)
            })
        }
        row {
            label("Text after separator")
        }.bottomGap(BottomGap.MEDIUM)
    }
}

// Helpers

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
    val splitter = OnePixelSplitter(false, SPLITTER_PROPORTION).apply {
        firstComponent = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(BORDER_EMPTY)
            add(JBLabel("Left panel"), BorderLayout.CENTER)
        }
        secondComponent = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(BORDER_EMPTY)
            add(JBLabel("Right panel"), BorderLayout.CENTER)
        }
        setHonorComponentsMinimumSize(true)
    }
    return splitter
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
        preferredScrollableViewportSize = Dimension(TABLE_WIDTH, TABLE_HEIGHT)
        showHorizontalLines = false
        showVerticalLines = false
        border = JBUI.Borders.empty(TABLE_BORDER)
    }
}

private fun createTitledPanel(
    title: String,
    content: JComponent,
    preferredHeight: Int? = null
): JPanel {
    return JPanel(BorderLayout()).apply {
        border = TitledBorder(title)
        add(content, BorderLayout.CENTER)
        // Ensure content doesn't get clipped if it's smaller than the preferredSize
        minimumSize = JBUI.size(MIN_PANEL_SIZE, MIN_PANEL_SIZE)
        if (preferredHeight != null) {
            preferredSize = JBUI.size(ZERO_SIZE, preferredHeight)
        }
    }
}
