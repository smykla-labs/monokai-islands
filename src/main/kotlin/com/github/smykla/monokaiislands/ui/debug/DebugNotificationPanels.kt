package com.github.smykla.monokaiislands.ui.debug

import com.github.smykla.monokaiislands.ui.debug.PROGRESS_MAX_VALUE
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.Panel
import com.intellij.util.ui.JBUI
import java.awt.FlowLayout
import java.awt.Point
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JProgressBar

private const val PROGRESS_WIDTH = 200
private const val PROGRESS_HEIGHT = 20
private const val PROGRESS_DEFAULT_VALUE = 65
private const val NOTIFICATION_BTN_GAP = 10
private const val NOTIFICATION_BTN_VGAP = 5
private const val BALLOON_FADEOUT_TIME = 5000L
private const val HALF_ANCHOR_OFFSET = 2

fun Panel.notificationDemoGroup() {
    group("Notifications & Popups") {
        row {
            cell(createNotificationButtons())
        }
        row {
            button("Show Popup Menu") {
                showListPopup()
            }
            button("Show Dialog") {
                val project = ProjectManager.getInstance().openProjects.firstOrNull()
                Messages.showInfoMessage(project, "Sample dialog body", "Dialog Title")
            }
        }.bottomGap(BottomGap.MEDIUM)
    }
}

fun Panel.statusIndicatorsGroup() {
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
            cell(JProgressBar(0, PROGRESS_MAX_VALUE).apply {
                value = PROGRESS_DEFAULT_VALUE
                preferredSize = JBUI.size(PROGRESS_WIDTH, PROGRESS_HEIGHT)
            })
        }.bottomGap(BottomGap.MEDIUM)
    }
}

private fun createNotificationButtons(): JComponent =
    JPanel(FlowLayout(FlowLayout.LEFT, NOTIFICATION_BTN_GAP, NOTIFICATION_BTN_VGAP)).apply {
        fun addBtn(label: String, type: NotificationType) {
            val btn = JButton(label)
            btn.addActionListener { showNotification(btn, "Theme Test", "$label sample", type) }
            add(btn)
        }
        addBtn("Info Notification", NotificationType.INFORMATION)
        addBtn("Warning Notification", NotificationType.WARNING)
        addBtn("Error Notification", NotificationType.ERROR)
    }

private fun showNotification(
    anchor: JComponent,
    title: String,
    content: String,
    type: NotificationType
) {
    val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
        .getNotificationGroup("Monokai Islands Theme Test")

    val project = ProjectManager.getInstance().openProjects.firstOrNull()
    ApplicationManager.getApplication().invokeLater {
        val messageType = when (type) {
            NotificationType.INFORMATION -> MessageType.INFO
            NotificationType.WARNING -> MessageType.WARNING
            NotificationType.ERROR -> MessageType.ERROR
            NotificationType.IDE_UPDATE -> MessageType.INFO
        }
        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(content, messageType, null)
            .setFadeoutTime(BALLOON_FADEOUT_TIME)
            .createBalloon()
            .show(
                com.intellij.ui.awt.RelativePoint(anchor, Point(anchor.width / HALF_ANCHOR_OFFSET, anchor.height)),
                com.intellij.openapi.ui.popup.Balloon.Position.below
            )

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
            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
        })
        addSeparator()
        add(object : AnAction("Third item") {
            override fun actionPerformed(e: AnActionEvent) = Unit
            override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
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
