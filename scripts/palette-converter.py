#!/usr/bin/env python3
"""Convert dark Monokai palette to light variant using HSL color space."""

import colorsys
import json
from pathlib import Path


def hex_to_rgb(hex_color: str) -> tuple[float, float, float]:
    """Convert hex color to RGB values (0-1 range)."""
    hex_color = hex_color.lstrip("#")
    return tuple(int(hex_color[i : i + 2], 16) / 255.0 for i in (0, 2, 4))


def rgb_to_hex(rgb: tuple[float, float, float]) -> str:
    """Convert RGB values (0-1 range) to hex color."""
    return f"#{int(rgb[0] * 255):02x}{int(rgb[1] * 255):02x}{int(rgb[2] * 255):02x}"


def convert_accent_to_light(hex_color: str) -> str:
    """Convert accent color for light theme: desaturate 12%, darken 18%."""
    r, g, b = hex_to_rgb(hex_color)
    h, lightness, s = colorsys.rgb_to_hls(r, g, b)

    # Desaturate and darken for better contrast on light background
    s = max(0.0, s - 0.12)
    lightness = max(0.0, lightness - 0.18)

    r, g, b = colorsys.hls_to_rgb(h, lightness, s)
    return rgb_to_hex((r, g, b))


def invert_background(hex_color: str, target_lightness: float = 0.98) -> str:
    """Invert background color to near-white."""
    r, g, b = hex_to_rgb(hex_color)
    h, _lightness, s = colorsys.rgb_to_hls(r, g, b)

    # Set to near-white with minimal saturation
    lightness = target_lightness
    s = 0.02

    r, g, b = colorsys.hls_to_rgb(h, lightness, s)
    return rgb_to_hex((r, g, b))


def convert_dimmed_to_light(hex_color: str, position: int) -> str:
    """Convert dimmed colors for light theme (reverse progression)."""
    r, g, b = hex_to_rgb(hex_color)
    h, _lightness, s = colorsys.rgb_to_hls(r, g, b)

    # Reverse dimmed colors: dimmed1 (lightest) becomes darkest, etc.
    # Position 1-5, where 1 is lightest and 5 is darkest
    target_lightness = 0.20 + (position - 1) * 0.12

    r, g, b = colorsys.hls_to_rgb(h, target_lightness, s)
    return rgb_to_hex((r, g, b))


def convert_palette_to_light(dark_palette: dict) -> dict:
    """Convert entire dark palette to light variant."""
    light_palette = {}

    # Invert backgrounds
    light_palette["background"] = invert_background(dark_palette["background"])
    light_palette["dark1"] = invert_background(
        dark_palette["dark1"], target_lightness=0.96
    )
    light_palette["dark2"] = invert_background(
        dark_palette["dark2"], target_lightness=0.94
    )

    # Text becomes dark
    light_palette["text"] = "#2d2a2e"

    # Convert accents (desaturate and darken)
    for i in range(1, 7):
        accent_key = f"accent{i}"
        light_palette[accent_key] = convert_accent_to_light(dark_palette[accent_key])

    # Reverse dimmed progression
    for i in range(1, 6):
        dimmed_key = f"dimmed{i}"
        light_palette[dimmed_key] = convert_dimmed_to_light(
            dark_palette[dimmed_key], 6 - i
        )

    return light_palette


def main() -> None:
    """Convert dark palette to light and save result."""
    project_root = Path(__file__).parent.parent
    dark_palette_path = project_root / "palettes" / "monokai-dark.json"
    light_palette_path = project_root / "palettes" / "monokai-light.json"

    # Load dark palette
    with dark_palette_path.open() as f:
        dark_palette = json.load(f)

    # Convert to light
    light_palette = convert_palette_to_light(dark_palette)

    # Save light palette
    with light_palette_path.open("w") as f:
        json.dump(light_palette, f, indent=2)
        f.write("\n")

    print(f"✓ Generated light palette: {light_palette_path}")


if __name__ == "__main__":
    main()
