#!/usr/bin/env python3
"""Validate WCAG contrast ratios for theme colors."""

import json
from pathlib import Path
import sys


def hex_to_rgb(hex_color: str) -> tuple[int, int, int]:
    """Convert hex color to RGB values (0-255 range)."""
    hex_color = hex_color.lstrip("#")
    return tuple(int(hex_color[i : i + 2], 16) for i in (0, 2, 4))


def calculate_relative_luminance(rgb: tuple[int, int, int]) -> float:
    """Calculate relative luminance for WCAG contrast ratio.

    Formula from WCAG 2.1:
    https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html
    """

    def adjust_channel(c: int) -> float:
        c_norm = c / 255.0
        if c_norm <= 0.03928:
            return c_norm / 12.92
        return ((c_norm + 0.055) / 1.055) ** 2.4

    r, g, b = rgb
    return 0.2126 * adjust_channel(r) + 0.7152 * adjust_channel(g) + 0.0722 * adjust_channel(b)


def calculate_contrast_ratio(color1: str, color2: str) -> float:
    """Calculate WCAG contrast ratio between two colors."""
    l1 = calculate_relative_luminance(hex_to_rgb(color1))
    l2 = calculate_relative_luminance(hex_to_rgb(color2))

    lighter = max(l1, l2)
    darker = min(l1, l2)

    return (lighter + 0.05) / (darker + 0.05)


def validate_palette(palette: dict) -> list[str]:
    """Validate WCAG contrast requirements for a palette."""
    issues = []

    # Text contrast requirements (WCAG AA: 4.5:1)
    text_pairs = [
        ("text", "background", 4.5),
        ("text", "dark1", 4.5),
        ("accent1", "background", 3.0),
        ("accent2", "background", 3.0),
        ("accent3", "background", 3.0),
        ("accent4", "background", 3.0),
        ("accent5", "background", 3.0),
        ("accent6", "background", 3.0),
        ("dimmed3", "background", 4.5),  # Comments
    ]

    for fg_key, bg_key, min_ratio in text_pairs:
        if fg_key not in palette or bg_key not in palette:
            continue

        ratio = calculate_contrast_ratio(palette[fg_key], palette[bg_key])
        if ratio < min_ratio:
            issues.append(
                f"  ❌ {fg_key} on {bg_key}: {ratio:.2f}:1 "
                f"(required: {min_ratio}:1)"
            )

    return issues


def validate_diff_colors(diff_colors: dict, line_numbers: str, text: str) -> list[str]:
    """Validate contrast for diff backgrounds.

    Args:
        diff_colors: Dict of diff color names to hex values
        line_numbers: Hex color for line numbers
        text: Hex color for main text
    """
    issues = []

    # Diff backgrounds should have good text contrast (8:1+ recommended, 4.5:1 minimum)
    for name, bg_color in diff_colors.items():
        text_ratio = calculate_contrast_ratio(text, bg_color)
        if text_ratio < 4.5:
            issues.append(
                f"  ❌ text on {name}: {text_ratio:.2f}:1 "
                f"(required: 4.5:1)"
            )
        elif text_ratio < 7.0:
            issues.append(
                f"  ⚠️  text on {name}: {text_ratio:.2f}:1 "
                f"(recommended: 7.0:1)"
            )

        # Line numbers need 2.0:1 minimum on diff backgrounds
        line_ratio = calculate_contrast_ratio(line_numbers, bg_color)
        if line_ratio < 2.0:
            issues.append(
                f"  ❌ line_numbers on {name}: {line_ratio:.2f}:1 "
                f"(required: 2.0:1)"
            )

    return issues


def validate_selection_colors(selection_bg: str, syntax_colors: dict) -> list[str]:
    """Validate contrast for selection background against syntax colors.

    Args:
        selection_bg: Hex color for selection background
        syntax_colors: Dict of syntax color names to hex values
    """
    issues = []

    for name, color in syntax_colors.items():
        ratio = calculate_contrast_ratio(color, selection_bg)
        # Comments are intentionally faded, so lower threshold
        min_ratio = 2.0 if "comment" in name.lower() else 3.0

        if ratio < min_ratio:
            issues.append(
                f"  ❌ {name} on selection: {ratio:.2f}:1 "
                f"(required: {min_ratio}:1)"
            )

    return issues


def print_validation_result(section: str, issues: list[str]) -> None:
    """Print validation results for a section."""
    print(f"\n{section}:")
    if issues:
        for issue in issues:
            print(issue)
    else:
        print(f"  ✅ {section} meet contrast requirements")


def get_palettes_to_check() -> list[tuple[Path, str]]:
    """Get list of palettes to validate."""
    if len(sys.argv) > 1:
        palette_path = Path(sys.argv[1])
        return [(palette_path, palette_path.stem)]

    project_root = Path(__file__).parent.parent
    palettes_dir = project_root / "palettes"
    return [(palettes_dir / "monokai-dark.json", "monokai-dark")]


def main() -> None:
    """Validate contrast ratios for all palettes."""
    all_issues: list[str] = []

    # Validate palettes
    for palette_path, palette_name in get_palettes_to_check():
        if not palette_path.exists():
            print(f"⚠️  Skipping {palette_name}: file not found")
            continue

        with palette_path.open() as f:
            palette = json.load(f)

        issues = validate_palette(palette)
        print_validation_result(palette_name, issues)
        all_issues.extend(issues)

    # Validate diff colors
    diff_colors = {
        "diff_inserted": "#2d5038",
        "diff_deleted": "#582838",
        "diff_modified": "#2d4858",
        "diff_conflict": "#583825",
    }
    diff_issues = validate_diff_colors(diff_colors, "#7B8590", "#fcfcfa")
    print_validation_result("Diff backgrounds", diff_issues)
    all_issues.extend(diff_issues)

    # Validate selection background
    syntax_colors = {
        "white_text": "#fcfcfa",
        "normal_text": "#bcbec4",
        "keywords_pink": "#ff6188",
        "strings_yellow": "#ffd866",
        "functions_green": "#a9dc76",
        "types_cyan": "#78dce8",
        "constants_purple": "#ab9df2",
        "numbers_orange": "#fc9867",
        "comments_gray": "#727072",
    }
    selection_issues = validate_selection_colors("#454045", syntax_colors)
    print_validation_result("Selection background", selection_issues)
    all_issues.extend(selection_issues)

    # Exit with error if any issues found
    if all_issues:
        print(f"\n❌ Found {len(all_issues)} contrast issues")
        sys.exit(1)

    print("\n✅ All validations pass")


if __name__ == "__main__":
    main()
