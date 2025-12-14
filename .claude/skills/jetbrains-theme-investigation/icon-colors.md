# Icon Color System

## Key Source Files

```text
platform/util/ui/src/com/intellij/ui/icons/stroke.kt       # toStrokeIcon function
platform/icons/src/expui/actions/                          # New UI action icons
platform/icons/src/actions/                                # Classic action icons
```

## ColorPalette in Theme JSON

Remap icon colors globally via `icons.ColorPalette`:

```json
"icons": {
  "ColorPalette": {
    "#3592C4": "#19181a",
    "Actions.Grey": "#19181a"
  }
}
```

## Named Color Keys (Targeted)

From HighContrast.theme.json:

**Actions (toolbar icons):**

- `Actions.Grey`, `Actions.Red`, `Actions.Yellow`, `Actions.Green`, `Actions.Blue`
- `Actions.GreyInline`, `Actions.GreyInline.Dark`

**Objects (list/tree icons):**

- `Objects.Grey`, `Objects.Red`, `Objects.Blue`, `Objects.Purple`, etc.
- `Objects.BlackText`, `Objects.YellowDark`, `Objects.GreenAndroid`

**Checkboxes:**

- `Checkbox.Background.Default.Dark`, `Checkbox.Border.Default.Dark`
- `Checkbox.Foreground.Selected.Dark`, `Checkbox.Focus.Wide.Dark`

## Standard Stroke Colors (from stroke.kt)

These hex colors can be remapped in ColorPalette:

```text
#818594, #6c707e, #3574f0, #5fb865, #e35252,
#eb7171, #e3ae4d, #fcc75b, #f28c35, #955ae0,
#a8adbd, #ced0d6
```

## How Icon Recoloring Works

1. SVG icons have hardcoded colors (e.g., `#CED0D6`)
2. `ColorPalette` in theme JSON remaps globally
3. Named keys (e.g., `Actions.Grey`) are more targeted but require icon support
4. `toStrokeIcon(icon, color)` recolors programmatically at runtime (code must call it)

## Button Icon Issue

Button icons don't auto-recolor. The `toStrokeIcon` function must be called explicitly:

```kotlin
icon = toStrokeIcon(icon, JBUI.CurrentTheme.Button.defaultButtonForeground())
```

Dialog buttons don't use this - icons display with original colors. Fix via ColorPalette hex mapping.

## Finding Icon Colors

```bash
# Find what color an icon uses
cat $INTELLIJ_COMMUNITY/platform/icons/src/expui/actions/checked_dark.svg | grep -o 'stroke="[^"]*"\|fill="[^"]*"'

# Find all icons using a specific color
find $INTELLIJ_COMMUNITY/platform/icons -name "*.svg" | xargs grep -l "#CED0D6"
```
