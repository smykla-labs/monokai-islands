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


def main() -> None:
    """Validate contrast ratios for all palettes."""
    if len(sys.argv) > 1:
        # Validate specific palette
        palette_path = Path(sys.argv[1])
        palettes_to_check = [(palette_path, palette_path.stem)]
    else:
        # Validate all palettes
        project_root = Path(__file__).parent.parent
        palettes_dir = project_root / "palettes"
        palettes_to_check = [
            (palettes_dir / "monokai-dark.json", "monokai-dark"),
        ]

    all_issues = []

    for palette_path, palette_name in palettes_to_check:
        if not palette_path.exists():
            print(f"⚠️  Skipping {palette_name}: file not found")
            continue

        with palette_path.open() as f:
            palette = json.load(f)

        print(f"\n{palette_name}:")
        issues = validate_palette(palette)

        if issues:
            all_issues.extend(issues)
            for issue in issues:
                print(issue)
        else:
            print("  ✅ All contrast ratios meet WCAG AA requirements")

    # Exit with error if any issues found
    if all_issues:
        print(f"\n❌ Found {len(all_issues)} contrast issues")
        sys.exit(1)
    else:
        print("\n✅ All palettes pass WCAG AA validation")


if __name__ == "__main__":
    main()
