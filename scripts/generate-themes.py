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
    editor_scheme = f"/editor-schemes/monokai-islands-{variant}.xml"

    # Build colors dictionary with base palette colors and derived colors
    colors = {}

    # Add base palette colors
    for key, value in palette.items():
        colors[key] = value

    # Add derived colors with alpha values
    colors["transparent"] = "#00000000"
    colors["dimmed5_80"] = palette["dimmed5"] + "80"
    colors["dimmed5_60"] = palette["dimmed5"] + "60"
    colors["accent1_20"] = palette["accent1"] + "20"
    colors["accent3_20"] = palette["accent3"] + "20"
    colors["accent3_40"] = palette["accent3"] + "40"
    # File colors - warm tinted versions that work with Monokai
    colors["file_yellow"] = "#3d3528"     # Warm yellow-brown tint
    colors["file_green"] = "#2a3230"      # Muted teal-green (harmonizes with purple selection)
    colors["file_gray"] = "#2d2a2e"       # Subtle warm gray (close to bg)
    colors["file_blue"] = "#282d38"       # Warm blue tint
    colors["file_orange"] = "#3d3028"     # Warm orange tint
    colors["file_rose"] = "#382830"       # Warm rose/pink tint
    colors["file_violet"] = "#302838"     # Warm violet tint
    # Warm purple-tinted UI colors (Monokai spirit, not pure grays)
    colors["input_bg"] = "#352f38"        # Input fields - warm purple
    colors["input_border"] = "#564b5e"    # Input borders - higher contrast purple
    colors["input_arrow"] = "#4a4252"     # Arrow buttons - distinct purple
    colors["input_hover"] = "#756a80"     # Hover state for dropdowns - balanced
    colors["input_disabled"] = "#1e1b1e"  # Disabled - darker muted
    colors["selection_bg"] = "#3d3545"    # Selection - warm purple
    colors["error_bg"] = "#3d2830"        # Error tooltip - reddish tint
    colors["warning_bg"] = "#3d3525"      # Warning tooltip - yellowish tint

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
        # Editor tabs
        "EditorTabs.background": "background",
        "EditorTabs.underlineColor": "accent1",
        "EditorTabs.underlinedBorderColor": "accent1",
        # Islands styling
        "Island.borderColor": "background",
        "Island.arc": 20,
        "Island.borderWidth": 5,
        # Transparent borders for clean Islands look
        "StatusBar.borderColor": "transparent",
        "ToolWindow.Stripe.borderColor": "transparent",
        "MainToolbar.borderColor": "transparent",
        # Selection and focus - warm purple tints (for file trees, settings)
        "Tree.selectionBackground": "selection_bg",
        "Tree.selectionForeground": "text",
        "Tree.selectionInactiveBackground": "input_bg",
        "List.selectionBackground": "selection_bg",
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
        # Default button (primary action)
        "Button.default.startBackground": "accent5",
        "Button.default.endBackground": "accent5",
        "Button.default.foreground": "dark1",
        "Button.default.startBorderColor": "accent5",
        "Button.default.endBorderColor": "accent5",
        "Button.default.focusedBorderColor": "text",
        "Button.default.focusColor": "text",
        "Component.focusedBorderColor": "accent5",
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
        "CheckBox.focusedBorderColor": "accent5",
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
        "TextField.disabledForeground": "dimmed4",
        "TextArea.background": "input_bg",
        "TextArea.borderColor": "input_border",
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


def main() -> None:
    """Generate theme JSON files from palettes."""
    project_root = Path(__file__).parent.parent
    palettes_dir = project_root / "palettes"
    themes_dir = project_root / "src" / "main" / "resources" / "themes"

    # Ensure themes directory exists
    themes_dir.mkdir(parents=True, exist_ok=True)

    # Define theme variants with their palette files and theme names
    variants = [
        ("dark", "monokai-dark.json", "Dark"),
    ]

    for variant_id, palette_file, variant_name in variants:
        palette_path = palettes_dir / palette_file

        if not palette_path.exists():
            print(f"⚠️  Skipping {variant_name}: palette not found at {palette_path}")
            continue

        # Load palette
        with palette_path.open() as f:
            palette = json.load(f)

        # Generate theme with custom name
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
