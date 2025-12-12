#!/usr/bin/env python3
"""Generate theme JSON files from palette definitions."""

import json
from pathlib import Path


def generate_theme_json(palette: dict, variant: str) -> dict:  # noqa: PLR0915
    """Generate theme JSON structure from palette.

    Args:
        palette: Color palette dictionary
        variant: Theme variant (e.g., "dark", "dark-light", "dark-darker")
    """
    is_dark = "dark" in variant
    parent = "Islands Dark" if is_dark else "Islands Light"
    # Each variant has its own editor scheme to sync background colors
    editor_scheme = f"/editor-schemes/monokai-islands-{variant}.xml"

    # Build a colors dictionary with base palette colors and derived colors
    colors = {}

    # Add base palette colors (skip _comment key)
    for key, value in palette.items():
        if not key.startswith("_"):
            colors[key] = value

    # === Utility colors ===
    colors["transparent"] = "#00000000"

    # === Alpha variants (for overlays and transparency) ===
    colors["dimmed5_80"] = palette["dimmed5"] + "80"  # 50% opacity
    colors["dimmed5_60"] = palette["dimmed5"] + "60"  # 38% opacity
    colors["dimmed5_40"] = palette["dimmed5"] + "40"  # 25% opacity
    colors["accent1_20"] = palette["accent1"] + "20"  # 13% opacity
    colors["accent3_20"] = palette["accent3"] + "20"  # 13% opacity
    colors["accent3_40"] = palette["accent3"] + "40"  # 25% opacity
    colors["accent6_25"] = palette["accent6"] + "40"  # 25% opacity (purple tint)
    colors["accent6_15"] = palette["accent6"] + "26"  # 15% opacity (subtle purple)

    # === Project window tab colors (transparency-based) ===
    colors["project_tab_active"] = "#ffffff08"     # White @ 3% - very subtle glow for active
    colors["project_tab_hover"] = "#ffffff0d"      # White @ 5% - subtle hover
    colors["project_tab_inactive_text"] = "#808590"  # Dimmed text for inactive

    # === Active tab/button colors ===
    colors["tab_active_bg"] = "#352a38"     # Warm purple background for active tabs
    colors["tab_active_border"] = calculate_lighter_color("#352a38", 0.12)

    # === Input/form colors (warm purple-tinted UI) ===
    colors["input_bg"] = "#2a252d"          # Input field background
    colors["input_border"] = "#332e38"      # Input border - darker, subtler
    colors["input_arrow"] = "#3a3340"       # Dropdown arrow button
    colors["input_hover"] = "#4a4255"       # Hover state
    colors["input_disabled"] = "#181618"    # Disabled state

    # === Focus colors ===
    colors["button_focus"] = "#ffffff"      # White border for focused regular buttons

    # === Tree selection with alpha transparency ===
    colors["tree_selection_fg"] = palette["text"] + "cc"  # 80% opacity white
    colors["button_default_focus"] = "#4a9ba8"  # Deep cyan for default button focus
    colors["input_focus"] = palette["accent5"]  # Cyan border for focused inputs (industry standard)

    # === Selection colors ===
    colors["selection_bg"] = "#3d3540"      # More visible selection
    colors["selection_inactive"] = "#4a3a4a"  # Inactive selection - more visible
    colors["list_selection"] = "#382d38"    # File list selection - balanced visibility
    colors["list_selection_alpha"] = "#6a4a7090"  # Vivid purple selection (blends with file colors)

    # === Tool window button colors (stripe icons) ===
    colors["toolwindow_button_selected"] = "#3d304060"  # Warm purple @ 38% opacity

    # === Status colors ===
    colors["error_bg"] = "#3d2830"          # Error backgrounds (reddish tint)
    colors["warning_bg"] = "#3d3525"        # Warning backgrounds (yellowish tint)

    # === Popup/notification backgrounds (lighter than editor for distinction) ===
    colors["popup_bg"] = "#2a262a"          # Slightly lighter, warm gray
    colors["popup_border"] = "#2d282d"      # Very subtle border, nearly the same as popup_bg
    colors["notification_bg"] = "#2d282d"   # Neutral, not too purple
    colors["notification_border"] = "#403840"  # Visible border for notifications
    # Transparent notification backgrounds (90% opacity, lighter for visibility)
    colors["notification_bg_90"] = "#3a363ae5"  # 90% opacity, lighter gray
    colors["notification_border_90"] = "#5a5460e5"  # 90% opacity border, more visible
    colors["error_bg_90"] = "#4d3540e5"  # Error with 90% opacity, more saturated
    colors["warning_bg_90"] = "#4d4530e5"  # Warning with 90% opacity, more saturated

    # === Diff colors (subtler for a darker background, good text contrast) ===
    colors["diff_inserted"] = "#263328"     # Added lines (green) - subtle, warm
    colors["diff_deleted"] = "#2d2330"      # Deleted lines (pink-red) - Monokai warm
    colors["diff_modified"] = "#252a30"     # Modified lines (cyan) - subtle
    colors["diff_conflict"] = "#302820"     # Conflict lines (orange) - subtle

    # === File colors (warm tints for a file tree) ===
    colors["file_yellow"] = "#3d3528"       # Yellow-brown tint
    colors["file_green"] = "#2a3230"        # Teal-green tint
    colors["file_gray"] = "#2d2a2e"         # Warm gray
    colors["file_blue"] = "#282d38"         # Blue tint
    colors["file_orange"] = "#3d3028"       # Orange tint
    colors["file_rose"] = "#382830"         # Rose/pink tint
    colors["file_violet"] = "#302838"       # Violet tint

    # Base theme structure
    theme = {
        "name": f"Monokai Islands {variant.capitalize()}",
        "dark": is_dark,
        "author": "Bart Smykla",
        "editorScheme": editor_scheme,
        "parentTheme": parent,
        "colors": colors,
        "ui": {},
    }

    # UI color mappings (reference color names from colors block)
    ui_colors = {
        # Main window backgrounds - use background as base, dark1 for chrome
        "*.background": "background",
        "MainWindow.background": "dark1",
        # Title pane (window header with project tabs on macOS)
        "TitlePane.background": "dark1",
        "TitlePane.inactiveBackground": "dark2",
        "TitlePane.Button.hoverBackground": "input_hover",
        "TitlePane.infoForeground": "text",
        "TitlePane.inactiveInfoForeground": "dimmed2",
        # Project window tabs (macOS merged windows)
        "MainWindow.Tab.background": "dark1",
        "MainWindow.Tab.selectedBackground": "project_tab_active",
        "MainWindow.Tab.selectedInactiveBackground": "project_tab_hover",
        "MainWindow.Tab.hoverBackground": "project_tab_hover",
        "MainWindow.Tab.foreground": "project_tab_inactive_text",
        "MainWindow.Tab.selectedForeground": "text",
        "MainWindow.Tab.hoverForeground": "dimmed1",
        "MainWindow.Tab.borderColor": "transparent",
        "MainWindow.Tab.separatorColor": "dimmed5",
        # Main toolbar dropdowns (project tabs, run configs, branches)
        "MainToolbar.Dropdown.background": "dark1",
        "MainToolbar.Dropdown.hoverBackground": "input_hover",
        "MainToolbar.Dropdown.pressedBackground": "selection_bg",
        "MainToolbar.Dropdown.transparentHoverBackground": "dimmed5_80",
        # Main toolbar icons
        "MainToolbar.Icon.background": "dark1",
        "MainToolbar.Icon.hoverBackground": "input_hover",
        "MainToolbar.Icon.pressedBackground": "selection_bg",
        # Default tabs (fallback for all tabs)
        "DefaultTabs.background": "background",
        "DefaultTabs.hoverBackground": "dimmed5_80",
        "DefaultTabs.underlineColor": "tab_active_border",
        "DefaultTabs.inactiveUnderlineColor": "dimmed4",
        "DefaultTabs.underlineHeight": 2,
        "DefaultTabs.underlinedTabBackground": "tab_active_bg",
        "DefaultTabs.underlinedTabForeground": "text",
        "Panel.background": "background",
        "SidePanel.background": "background",
        # Tool windows - editor background (lighter layer)
        "ToolWindow.background": "background",
        "ToolWindow.Header.background": "background",
        "ToolWindow.Header.inactiveBackground": "background",
        # Tool window stripe buttons (sidebar icons like Project, Commit)
        "ToolWindow.Button.selectedBackground": "toolwindow_button_selected",
        "ToolWindow.Button.selectedForeground": "text",
        "ToolWindow.Button.hoverBackground": "input_hover",
        # Tool window header tabs
        "ToolWindow.HeaderTab.selectedBackground": "tab_active_bg",
        "ToolWindow.HeaderTab.selectedInactiveBackground": "selection_inactive",
        "ToolWindow.HeaderTab.hoverBackground": "dimmed5_80",
        "ToolWindow.HeaderTab.hoverInactiveBackground": "dimmed5_80",
        "ToolWindow.HeaderTab.underlineColor": "tab_active_border",
        "ToolWindow.HeaderTab.inactiveUnderlineColor": "dimmed4",
        "ToolWindow.HeaderTab.underlineHeight": 2,
        # Editor tabs - Islands theme properties
        "EditorTabs.background": "background",
        "EditorTabs.underlineColor": "tab_active_border",
        "EditorTabs.underlinedBorderColor": "tab_active_border",
        "EditorTabs.inactiveUnderlinedTabBorderColor": "dimmed4",
        "EditorTabs.underlinedTabBackground": "tab_active_bg",
        "EditorTabs.inactiveUnderlinedTabBackground": "background",
        "EditorTabs.selectedBackground": "tab_active_bg",
        "EditorTabs.selectedForeground": "text",
        "EditorTabs.borderColor": "tab_active_border",
        "EditorTabs.hoverBackground": "dimmed5",
        "EditorTabs.inactiveBackground": "background",
        "EditorTabs.inactiveForeground": "dimmed2",
        # Islands styling
        "Island.borderColor": "background",
        "Island.arc": 20,
        "Island.borderWidth": 5,
        # Transparent borders for clean Islands look
        "StatusBar.borderColor": "transparent",
        "ToolWindow.Stripe.borderColor": "transparent",
        "MainToolbar.borderColor": "transparent",
        # Selection and focus - warm purple tints (for file trees, settings)
        "Tree.foreground": "text",  # Normal files use default text color (white)
        "Tree.selectionBackground": "list_selection_alpha",
        "Tree.selectionForeground": "tree_selection_fg",  # 80% opacity white
        "Tree.selectionInactiveBackground": "selection_inactive",
        "List.foreground": "text",
        "List.selectionBackground": "list_selection_alpha",
        "List.selectionForeground": "tree_selection_fg",
        "List.selectionInactiveBackground": "selection_inactive",
        # Project tree and file lists (with subtle transparency)
        "Tree.background": "background",
        "ProjectViewTree.selectionBackground": "list_selection",
        "ProjectViewTree.selectionInactiveBackground": "selection_inactive",
        "FileChooser.selectionInactiveBackground": "input_bg",
        # File colors - warm tinted versions
        "FileColor.Yellow": "file_yellow",
        "FileColor.Green": "file_green",
        "FileColor.Gray": "file_gray",
        "FileColor.Blue": "file_blue",
        "FileColor.Orange": "file_orange",
        "FileColor.Rose": "file_rose",
        "FileColor.Violet": "file_violet",
        # Default button (primary action) - teal/cyan with dark text
        "Button.default.startBackground": "accent5",
        "Button.default.endBackground": "accent5",
        "Button.default.foreground": "dark1",
        "Button.default.startBorderColor": "accent5",
        "Button.default.endBorderColor": "accent5",
        "Button.default.focusedBorderColor": "button_default_focus",
        "Button.default.focusColor": "button_default_focus",
        "Component.focusedBorderColor": "input_focus",
        "Component.focusColor": "input_focus",
        "Component.focusWidth": 2,
        # Code completion
        "CompletionPopup.selectionBackground": "dimmed5",
        "CompletionPopup.selectionInactiveBackground": "dimmed5_60",
        # Search
        "SearchEverywhere.Tab.selectedBackground": "dimmed5",
        "SearchMatch.startBackground": "accent3_40",
        "SearchMatch.endBackground": "accent3_40",
        # Version control
        "VersionControl.GitLog.localBranchIconColor": "accent4",
        "VersionControl.GitLog.remoteBranchIconColor": "accent5",
        "VersionControl.GitLog.tagIconColor": "accent2",
        # Notifications and balloons (with transparency)
        "Notification.background": "notification_bg_90",
        "Notification.borderColor": "notification_border_90",
        "Notification.borderInsets": "3,3,1,3",  # top,left,bottom,right
        "Notification.arc": 12,  # Rounded corners
        "Notification.errorBackground": "error_bg_90",
        "Notification.errorBorderColor": "accent1",
        "Notification.warningBackground": "warning_bg_90",
        "Notification.warningBorderColor": "accent3",
        "Notification.ToolWindow.background": "notification_bg_90",
        "Notification.ToolWindow.borderColor": "notification_border_90",
        "Notification.ToolWindow.errorBackground": "error_bg_90",
        "Notification.ToolWindow.errorBorderColor": "accent1",
        "Notification.ToolWindow.warningBackground": "warning_bg_90",
        "Notification.ToolWindow.warningBorderColor": "accent3",
        "Notification.linkForeground": "accent5",
        "Notification.ToolWindow.linkForeground": "accent5",
        # Balloon popups (with transparency)
        "Balloon.background": "notification_bg_90",
        "Balloon.borderColor": "notification_border_90",
        "Balloon.error.background": "error_bg_90",
        "Balloon.error.borderColor": "accent1",
        "Balloon.warning.background": "warning_bg_90",
        "Balloon.warning.borderColor": "accent3",
        # Editor notifications (banners)
        "EditorNotification.background": "input_bg",
        "EditorNotification.borderColor": "input_border",
        "Banner.background": "input_bg",
        "Banner.borderColor": "input_border",
        # Error/validation tooltips and hints
        "ValidationTooltip.errorBackground": "error_bg",
        "ValidationTooltip.errorBorderColor": "accent1",
        "ValidationTooltip.warningBackground": "warning_bg",
        "ValidationTooltip.warningBorderColor": "accent3",
        "ToolTip.background": "input_bg",
        "ToolTip.borderColor": "input_border",
        "ToolTip.foreground": "text",
        "HintPane.background": "input_bg",
        "Hint.background": "input_bg",
        "Hint.borderColor": "input_border",
        "Hint.foreground": "text",
        "Editor.ToolTip.background": "input_bg",
        "Editor.ToolTip.borderColor": "input_border",
        "EditorPane.background": "input_bg",
        "EditorPane.selectionBackground": "selection_bg",
        "EditorPane.selectionForeground": "text",
        "InformationHint.background": "input_bg",
        "InformationHint.borderColor": "input_border",
        "ErrorHint.background": "error_bg",
        "ErrorHint.borderColor": "accent1",
        "QuestionHint.background": "input_bg",
        "SpeedSearchPopup.background": "input_bg",
        "SpeedSearchPopup.borderColor": "input_border",
        "SpeedSearchPopup.foreground": "text",
        "GotItTooltip.background": "input_bg",
        "GotItTooltip.borderColor": "input_border",
        # Popups and dropdown menus
        "Popup.borderColor": "popup_border",
        "Popup.innerBorderColor": "popup_border",
        "Popup.paintBorder": True,
        "Popup.background": "popup_bg",
        "Popup.Header.activeBackground": "selection_bg",
        "Popup.Header.inactiveBackground": "background",
        "PopupMenu.background": "popup_bg",
        "PopupMenu.selectionBackground": "input_hover",
        "PopupMenu.selectionForeground": "text",
        "Menu.background": "popup_bg",
        "Menu.borderColor": "popup_border",
        "Menu.selectionBackground": "selection_bg",
        "Menu.selectionForeground": "text",
        "MenuItem.background": "popup_bg",
        "MenuItem.selectionBackground": "selection_bg",
        "MenuItem.selectionForeground": "text",
        # List hover for dropdowns
        "List.background": "background",
        "List.hoverBackground": "input_hover",
        "List.hoverForeground": "text",
        # Progress bar
        "ProgressBar.progressColor": "accent5",
        "ProgressBar.indeterminateStartColor": "accent5",
        "ProgressBar.indeterminateEndColor": "accent6",
        # Links
        "Link.foreground": "accent5",
        "Link.activeForeground": "accent5",
        "Link.hoverForeground": "accent5",
        "Link.pressedForeground": "accent5",
        "Link.visitedForeground": "accent6",
        # Dialogs and Settings - use regular background
        "Dialog.background": "background",
        "DialogWrapper.southPanelBackground": "background",
        "Table.background": "background",
        "Table.gridColor": "dimmed5",
        "Table.selectionBackground": "dimmed5_80",
        "Table.selectionForeground": "text",
        # Form controls and inputs - warm purple tints
        "CheckBox.background": "background",
        "CheckBox.borderColor1": "input_border",
        "CheckBox.borderColor2": "input_border",
        "CheckBox.focusedBorderColor": "input_focus",
        "CheckBox.disabledBackground": "background",
        "CheckBox.disabledBorderColor1": "input_bg",
        "CheckBox.disabledBorderColor2": "input_bg",
        "ComboBox.background": "input_bg",
        "ComboBox.selectionBackground": "input_hover",
        "ComboBox.ArrowButton.background": "input_arrow",
        # ComboBox popup list
        "ComboBoxPopup.background": "popup_bg",
        "ComboBoxPopup.foreground": "text",
        "ComboBox.ArrowButton.nonEditableBackground": "input_arrow",
        "ComboBox.ArrowButton.iconColor": "dimmed1",
        "ComboBox.nonEditableBackground": "input_bg",
        "ComboBox.borderColor": "input_border",
        "ComboBox.ArrowButton.disabledBackground": "input_disabled",
        "ComboBox.ArrowButton.disabledIconColor": "dimmed4",
        "ComboBox.disabledForeground": "dimmed4",
        "TextField.background": "background",
        "TextField.borderColor": "input_border",
        "TextField.selectionBackground": "selection_bg",
        "TextField.selectionForeground": "text",
        "TextField.disabledForeground": "dimmed4",
        "TextArea.background": "input_bg",
        "TextArea.borderColor": "input_border",
        "TextArea.selectionBackground": "selection_bg",
        "TextArea.selectionForeground": "text",
        "TextArea.disabledBackground": "input_disabled",
        "SearchField.background": "input_bg",
        "SearchField.borderColor": "input_border",
        "SearchEverywhere.SearchField.background": "input_bg",
        "SearchEverywhere.SearchField.borderColor": "input_border",
        "Spinner.background": "input_bg",
        "Spinner.borderColor": "input_border",
        "Spinner.disabledBackground": "input_disabled",
        "FormattedTextField.background": "input_bg",
        "FormattedTextField.borderColor": "input_border",
        "FormattedTextField.disabledBackground": "input_disabled",
        "PasswordField.background": "input_bg",
        "PasswordField.borderColor": "input_border",
        # Component-level styling
        "Component.disabledBorderColor": "input_bg",
        "Component.borderColor": "input_border",
        # Text field with a browse button
        "TextFieldWithBrowseButton.borderColor": "input_border",
        "TextFieldWithBrowseButton.background": "input_bg",
        # Buttons - match settings background, warm purple border
        "Button.startBackground": "background",
        "Button.endBackground": "background",
        "Button.background": "background",
        "Button.foreground": "text",
        "Button.startBorderColor": "input_border",
        "Button.endBorderColor": "input_border",
        "Button.borderColor": "input_border",
        "Button.shadowColor": "transparent",
        "Button.shadowWidth": 0,
        "Button.arc": 8,
        "Button.margin": "4,14,4,14",
        # Action buttons (icon buttons like help ?)
        "ActionButton.background": "background",
        "ActionButton.hoverBackground": "input_hover",
        "ActionButton.hoverBorderColor": "input_border",
        "ActionButton.pressedBackground": "selection_bg",
        "ActionButton.pressedBorderColor": "input_border",
        # Help button specifically
        "HelpButton.background": "background",
        "HelpButton.borderColor": "input_border",
        # Counter badges (notification counts on tabs, trees, etc.)
        # Note: Counter shape is hardcoded as oval - only colors can be customized
        "Counter.background": "accent1",
        "Counter.foreground": "text",
        # Toggle buttons and segmented buttons (for plugin manager, tabs, etc.)
        "ToggleButton.on.background": "tab_active_bg",
        "ToggleButton.on.foreground": "text",
        "ToggleButton.on.borderColor": "tab_active_border",
        "ToggleButton.off.background": "background",
        "ToggleButton.off.foreground": "dimmed1",
        "ToggleButton.off.borderColor": "input_border",
        # Segmented buttons (Plugin tabs, etc.) - ensure consistent padding
        "SegmentedButton.selected.startBackground": "tab_active_bg",
        "SegmentedButton.selected.endBackground": "tab_active_bg",
        "SegmentedButton.selected.foreground": "text",
        "SegmentedButton.selected.startBorderColor": "tab_active_border",
        "SegmentedButton.selected.endBorderColor": "tab_active_border",
        "SegmentedButton.unselected.startBackground": "background",
        "SegmentedButton.unselected.endBackground": "background",
        "SegmentedButton.unselected.foreground": "dimmed1",
        "SegmentedButton.unselected.startBorderColor": "input_border",
        "SegmentedButton.unselected.endBorderColor": "input_border",
        # New-style SegmentedButton properties for consistency
        "SegmentedButton.selectedButtonColor": "tab_active_bg",
        "SegmentedButton.focusedSelectedButtonColor": "tab_active_bg",
        "SegmentedButton.selectedStartBorderColor": "tab_active_border",
        "SegmentedButton.selectedEndBorderColor": "tab_active_border",
        # Settings/Preferences panel
        "Settings.background": "background",
        "OptionPane.background": "background",
        "TabbedPane.background": "background",
        "TabbedPane.contentAreaColor": "background",
        "ScrollPane.background": "background",
        "Viewport.background": "background",
    }

    theme["ui"] = flatten_to_nested(ui_colors)
    return theme


def flatten_to_nested(flat_dict: dict) -> dict:
    """Convert flat dot-notation keys to nested dictionary structure.

    Args:
        flat_dict: Dictionary with dot-notation keys
            (e.g., "ToolWindow.Header.background")

    Returns:
        Nested dictionary structure matching JetBrains theme format
    """
    nested = {}

    for key, value in flat_dict.items():
        parts = key.split(".")
        current = nested

        # Navigate/create nested structure for all but the last part
        for part in parts[:-1]:
            if part not in current:
                current[part] = {}
            elif not isinstance(current[part], dict):
                # Handle case where a key is both a value and a parent
                # e.g., "Button.background" and "Button.default.background"
                current[part] = {"": current[part]}
            current = current[part]

        # Set the final value
        final_key = parts[-1]
        if final_key in current and isinstance(current[final_key], dict):
            # Value already exists as a dict, store value at empty key
            current[final_key][""] = value
        else:
            current[final_key] = value

    return nested


def calculate_lighter_color(hex_color: str, factor: float) -> str:
    """Calculate a lighter version of a hex color.

    Args:
        hex_color: Hex color string (e.g., "#3d3548")
        factor: Lightness factor (0.0 to 1.0, where 0.15 = 15% lighter)

    Returns:
        Hex color string of the lighter color
    """
    # Remove # if present
    hex_color = hex_color.lstrip("#")
    # Convert to RGB
    r = int(hex_color[0:2], 16)
    g = int(hex_color[2:4], 16)
    b = int(hex_color[4:6], 16)
    # Calculate lighter color
    r_new = int(r + (255 - r) * factor)
    g_new = int(g + (255 - g) * factor)
    b_new = int(b + (255 - b) * factor)
    # Convert back to hex
    return f"#{r_new:02x}{g_new:02x}{b_new:02x}"


def main() -> None:
    """Generate theme JSON files from palettes."""
    project_root = Path(__file__).parent.parent
    palettes_dir = project_root / "palettes"
    themes_dir = project_root / "src" / "main" / "resources" / "themes"

    # Ensure themes directory exists
    themes_dir.mkdir(parents=True, exist_ok=True)

    # Load dark palette
    palette_path = palettes_dir / "monokai-dark.json"
    with palette_path.open() as f:
        palette = json.load(f)

    # Generate theme
    theme = generate_theme_json(palette, "dark")
    theme["name"] = "Monokai Islands Dark"

    # Write theme JSON
    theme_path = themes_dir / "monokai-islands-dark.theme.json"
    with theme_path.open("w") as f:
        json.dump(theme, f, indent=2)
        f.write("\n")

    print(f"✓ Generated Dark theme: {theme_path}")


if __name__ == "__main__":
    main()
