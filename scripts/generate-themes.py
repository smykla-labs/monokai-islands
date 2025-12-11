#!/usr/bin/env python3
"""Generate theme JSON files from palette definitions."""

import json
from pathlib import Path


def generate_theme_json(palette: dict, variant: str) -> dict:
    """Generate theme JSON structure from palette.

    Args:
        palette: Color palette dictionary
        variant: Theme variant (e.g., "dark", "dark-option1", "dark-option2")
    """
    is_dark = "dark" in variant
    parent = "Islands Dark" if is_dark else "Islands Light"
    # All dark variants share the same editor scheme (only UI colors differ)
    base_variant = "dark" if is_dark else "light"
    editor_scheme = f"/editor-schemes/monokai-islands-{base_variant}.xml"

    # Build colors dictionary with base palette colors and derived colors
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
    colors["accent1_20"] = palette["accent1"] + "20"  # 13% opacity
    colors["accent3_20"] = palette["accent3"] + "20"  # 13% opacity
    colors["accent3_40"] = palette["accent3"] + "40"  # 25% opacity

    # === Active tab/button colors ===
    colors["tab_active_bg"] = "#403044"     # Warm purple background for active tabs
    colors["tab_active_border"] = calculate_lighter_color("#403044", 0.15)  # 15% lighter

    # === Input/form colors (warm purple-tinted UI) ===
    colors["input_bg"] = "#352f38"          # Input field background
    colors["input_border"] = "#564b5e"      # Input border (unfocused)
    colors["input_arrow"] = "#4a4252"       # Dropdown arrow button
    colors["input_hover"] = "#756a80"       # Hover state
    colors["input_disabled"] = "#1e1b1e"    # Disabled state

    # === Focus colors ===
    colors["button_focus"] = "#ffffff"      # White border for focused buttons (contrast with cyan)
    colors["input_focus"] = palette["accent5"]  # Cyan border for focused inputs

    # === Selection colors ===
    colors["selection_bg"] = "#454045"      # Active selection (neutral gray for diffs)
    colors["selection_inactive"] = "#2d2830"  # Inactive selection

    # === Status colors ===
    colors["error_bg"] = "#3d2830"          # Error backgrounds (reddish tint)
    colors["warning_bg"] = "#3d3525"        # Warning backgrounds (yellowish tint)

    # === Diff colors (saturated for visibility) ===
    colors["diff_inserted"] = "#2d5038"     # Added lines (green)
    colors["diff_deleted"] = "#582838"      # Deleted lines (red)
    colors["diff_modified"] = "#2d4858"     # Modified lines (cyan)
    colors["diff_conflict"] = "#583825"     # Conflict lines (orange)

    # === File colors (warm tints for file tree) ===
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
        "Panel.background": "background",
        "SidePanel.background": "background",
        # Tool windows - editor background (lighter layer)
        "ToolWindow.background": "background",
        "ToolWindow.Header.background": "dark1",
        "ToolWindow.Header.inactiveBackground": "dark1",
        # Editor tabs - Islands theme properties
        "EditorTabs.background": "background",
        "EditorTabs.underlineColor": "tab_active_border",
        "EditorTabs.underlinedBorderColor": "tab_active_border",
        "EditorTabs.inactiveUnderlinedTabBorderColor": "dimmed4",
        "EditorTabs.underlineHeight": 2,
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
        "Tree.selectionBackground": "selection_inactive",
        "Tree.selectionForeground": "text",
        "Tree.selectionInactiveBackground": "input_bg",
        "List.selectionBackground": "selection_inactive",
        "List.selectionForeground": "text",
        "List.selectionInactiveBackground": "input_bg",
        # Project tree and file lists
        "Tree.background": "background",
        "ProjectViewTree.selectionBackground": "selection_bg",
        "ProjectViewTree.selectionInactiveBackground": "input_bg",
        "FileChooser.selectionInactiveBackground": "input_bg",
        # File colors - warm tinted versions
        "FileColor.Yellow": "file_yellow",
        "FileColor.Green": "file_green",
        "FileColor.Gray": "file_gray",
        "FileColor.Blue": "file_blue",
        "FileColor.Orange": "file_orange",
        "FileColor.Rose": "file_rose",
        "FileColor.Violet": "file_violet",
        # Default button (primary action) - cyan for best contrast
        "Button.default.startBackground": "accent5",
        "Button.default.endBackground": "accent5",
        "Button.default.foreground": "dark1",
        "Button.default.startBorderColor": "accent5",
        "Button.default.endBorderColor": "accent5",
        "Button.default.focusedBorderColor": "button_focus",
        "Button.default.focusColor": "button_focus",
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
        # Notifications and balloons
        "Notification.background": "input_bg",
        "Notification.borderColor": "input_border",
        "Notification.errorBackground": "error_bg",
        "Notification.errorBorderColor": "accent1",
        "Notification.warningBackground": "warning_bg",
        "Notification.warningBorderColor": "accent3",
        "Notification.ToolWindow.background": "input_bg",
        "Notification.ToolWindow.borderColor": "input_border",
        "Notification.ToolWindow.errorBackground": "error_bg",
        "Notification.ToolWindow.errorBorderColor": "accent1",
        "Notification.ToolWindow.warningBackground": "warning_bg",
        "Notification.ToolWindow.warningBorderColor": "accent3",
        # Balloon popups
        "Balloon.background": "input_bg",
        "Balloon.borderColor": "input_border",
        "Balloon.error.background": "error_bg",
        "Balloon.error.borderColor": "accent1",
        "Balloon.warning.background": "warning_bg",
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
        "Popup.borderColor": "input_border",
        "Popup.background": "background",
        "Popup.Header.activeBackground": "selection_bg",
        "Popup.Header.inactiveBackground": "background",
        "PopupMenu.background": "background",
        "PopupMenu.selectionBackground": "input_hover",
        "PopupMenu.selectionForeground": "text",
        "PopupMenu.borderColor": "input_border",
        "Menu.background": "background",
        "Menu.selectionBackground": "selection_bg",
        "Menu.selectionForeground": "text",
        "MenuItem.background": "background",
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
        "Link.activeForeground": "accent5",
        "Link.hoverForeground": "accent5",
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
        "ComboBoxPopup.background": "background",
        "ComboBoxPopup.foreground": "text",
        "ComboBox.ArrowButton.nonEditableBackground": "input_arrow",
        "ComboBox.ArrowButton.iconColor": "dimmed1",
        "ComboBox.nonEditableBackground": "input_bg",
        "ComboBox.borderColor": "input_border",
        "ComboBox.ArrowButton.disabledBackground": "input_disabled",
        "ComboBox.ArrowButton.disabledIconColor": "dimmed4",
        "ComboBox.disabledForeground": "dimmed4",
        "TextField.background": "input_bg",
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
        # Action buttons (icon buttons like help ?)
        "ActionButton.background": "background",
        "ActionButton.hoverBackground": "input_hover",
        "ActionButton.hoverBorderColor": "input_border",
        "ActionButton.pressedBackground": "selection_bg",
        "ActionButton.pressedBorderColor": "input_border",
        # Help button specifically
        "HelpButton.background": "background",
        "HelpButton.borderColor": "input_border",
        # Toggle buttons and segmented buttons (for plugin manager, tabs, etc.)
        "ToggleButton.on.background": "tab_active_bg",
        "ToggleButton.on.foreground": "text",
        "ToggleButton.on.borderColor": "tab_active_border",
        "ToggleButton.off.background": "background",
        "ToggleButton.off.foreground": "dimmed1",
        "ToggleButton.off.borderColor": "input_border",
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
        # Settings/Preferences panel
        "Settings.background": "background",
        "OptionPane.background": "background",
        "OptionPane.messageAreaBorder": "background",
        "TabbedPane.background": "background",
        "TabbedPane.contentAreaColor": "background",
        "ScrollPane.background": "background",
        "Viewport.background": "background",
    }

    theme["ui"] = ui_colors
    return theme


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

    # Load base palette
    base_palette_path = palettes_dir / "monokai-dark.json"
    with base_palette_path.open() as f:
        base_palette = json.load(f)

    # Define theme variants
    variants = [
        ("dark", "Dark"),
    ]

    for variant_id, variant_name in variants:
        palette = base_palette.copy()

        # Generate theme
        theme = generate_theme_json(palette, variant_id)
        theme["name"] = f"Monokai Islands {variant_name}"

        # Write theme JSON
        theme_path = themes_dir / f"monokai-islands-{variant_id}.theme.json"
        with theme_path.open("w") as f:
            json.dump(theme, f, indent=2)
            f.write("\n")

        print(f"✓ Generated {variant_name} theme: {theme_path}")


if __name__ == "__main__":
    main()
