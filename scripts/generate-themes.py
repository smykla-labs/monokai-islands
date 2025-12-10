#!/usr/bin/env python3
"""Generate theme JSON files from palette definitions."""

import json
from pathlib import Path


def generate_theme_json(palette: dict, variant: str) -> dict:
    """Generate theme JSON structure from palette.

    Args:
        palette: Color palette dictionary
        variant: Theme variant ("dark" or "light")
    """
    is_dark = variant == "dark"
    parent = "Islands Dark" if is_dark else "Islands Light"
    editor_scheme = f"/editor-schemes/monokai-islands-{variant}.xml"

    # Base theme structure
    theme = {
        "name": f"Monokai Islands {variant.capitalize()}",
        "dark": is_dark,
        "author": "Bart Smykla",
        "editorScheme": editor_scheme,
        "parent": parent,
        "islands": 1,
        "ui": {},
    }

    # UI color mappings
    ui_colors = {
        # Main window backgrounds
        "MainWindow.background": palette["dark1"],
        "ToolWindow.background": palette["background"],
        "ToolWindow.Header.background": palette["background"],
        # Editor tabs
        "EditorTabs.background": palette["background"],
        "EditorTabs.underlineColor": palette["accent1"],
        "EditorTabs.underlinedBorderColor": palette["accent1"],
        # Islands styling
        "Island.borderColor": palette["background"],
        "Island.arc": 20,
        "Island.borderWidth": 5,
        # Transparent borders for clean Islands look
        "StatusBar.borderColor": "#00000000",
        "ToolWindow.Stripe.borderColor": "#00000000",
        "MainToolbar.borderColor": "#00000000",
        # Selection and focus
        "Tree.selectionBackground": palette["dimmed5"] + "80",
        "Tree.selectionForeground": palette["text"],
        "List.selectionBackground": palette["dimmed5"] + "80",
        "List.selectionForeground": palette["text"],
        # Buttons and inputs
        "Button.default.startBackground": palette["accent5"],
        "Button.default.endBackground": palette["accent5"],
        "Button.default.foreground": palette["dark1"],
        "Button.default.focusedBorderColor": palette["accent5"],
        "Component.focusedBorderColor": palette["accent5"],
        # Code completion
        "CompletionPopup.selectionBackground": palette["dimmed5"],
        "CompletionPopup.selectionInactiveBackground": palette["dimmed5"] + "60",
        # Search
        "SearchEverywhere.Tab.selectedBackground": palette["dimmed5"],
        "SearchMatch.startBackground": palette["accent3"] + "40",
        "SearchMatch.endBackground": palette["accent3"] + "40",
        # Version control
        "VersionControl.GitLog.localBranchIconColor": palette["accent4"],
        "VersionControl.GitLog.remoteBranchIconColor": palette["accent5"],
        "VersionControl.GitLog.tagIconColor": palette["accent2"],
        # Notifications
        "Notification.errorBackground": palette["accent1"] + "20",
        "Notification.errorBorderColor": palette["accent1"],
        "Notification.warningBackground": palette["accent3"] + "20",
        "Notification.warningBorderColor": palette["accent3"],
        # Popups
        "Popup.borderColor": palette["dimmed5"],
        "Popup.Header.activeBackground": palette["dimmed5"],
        "Popup.Header.inactiveBackground": palette["background"],
        # Progress bar
        "ProgressBar.progressColor": palette["accent5"],
        "ProgressBar.indeterminateStartColor": palette["accent5"],
        "ProgressBar.indeterminateEndColor": palette["accent6"],
        # Links
        "Link.activeForeground": palette["accent5"],
        "Link.hoverForeground": palette["accent5"],
        "Link.visitedForeground": palette["accent6"],
    }

    theme["ui"] = ui_colors
    return theme


def main() -> None:
    """Generate theme JSON files from palettes."""
    project_root = Path(__file__).parent.parent
    palettes_dir = project_root / "palettes"
    themes_dir = project_root / "resources" / "themes"

    # Ensure themes directory exists
    themes_dir.mkdir(parents=True, exist_ok=True)

    variants = ["dark", "light"]

    for variant in variants:
        palette_path = palettes_dir / f"monokai-{variant}.json"

        if not palette_path.exists():
            print(f"⚠️  Skipping {variant}: palette not found at {palette_path}")
            continue

        # Load palette
        with palette_path.open() as f:
            palette = json.load(f)

        # Generate theme
        theme = generate_theme_json(palette, variant)

        # Write theme JSON
        theme_path = themes_dir / f"monokai-islands-{variant}.theme.json"
        with theme_path.open("w") as f:
            json.dump(theme, f, indent=2)
            f.write("\n")

        print(f"✓ Generated {variant} theme: {theme_path}")


if __name__ == "__main__":
    main()
