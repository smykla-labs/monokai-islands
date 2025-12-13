package com.github.smykla.monokaiislands.actions

import com.github.smykla.monokaiislands.listeners.ThemeChangeListener
import com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class ToggleMarkdownCssAction : ToggleAction() {

    override fun isSelected(e: AnActionEvent): Boolean =
        MonokaiIslandsSettings.getInstance().enableMarkdownCss

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        MonokaiIslandsSettings.getInstance().enableMarkdownCss = state
        ThemeChangeListener.applyMarkdownCss(state && ThemeChangeListener.isMonokaiThemeActive())
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
