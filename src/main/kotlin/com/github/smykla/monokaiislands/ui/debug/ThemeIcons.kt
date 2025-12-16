package com.github.smykla.monokaiislands.ui.debug

import com.intellij.icons.AllIcons
import javax.swing.Icon

object ThemeIcons {
    fun getAllIcons(): List<Pair<String, Icon>> {
        return getActionsIcons() + getGeneralIcons() + getFileTypesIcons() +
            getNodesIcons() + getDebuggerIcons() + getCodeIcons()
    }

    private fun getActionsIcons() = listOf(
        "Execute" to AllIcons.Actions.Execute,
        "Compile" to AllIcons.Actions.Compile,
        "Suspend" to AllIcons.Actions.Suspend,
        "Cancel" to AllIcons.Actions.Cancel,
        "Refresh" to AllIcons.Actions.Refresh,
        "Search" to AllIcons.Actions.Search,
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
}
