# Monokai Islands Theme Plugin

## Project Overview

JetBrains GoLand theme plugin combining Monokai color palette with Islands UI aesthetics. Gradle-based IntelliJ Platform plugin (since build 253, GoLand 2025.3+). Dark and light variants with automated generation from JSON palette definitions.

## Build & Workflow

```bash
mise run lint                             # Run all linters (Python, Kotlin, editorconfig, Markdown)
./gradlew buildPlugin                     # Build plugin (auto-runs generateThemes task)
./gradlew runIde                          # Test in a GoLand instance
python3 scripts/palette-converter.py      # Generate light palette
python3 scripts/generate-themes.py        # Generate theme JSONs
python3 scripts/validate-contrast.py      # Check WCAG compliance
```

## Architecture

### Color System

**Single source of truth**: `palettes/monokai-dark.json` (17 colors: background, dark1-2, text, accent1-6, dimmed1-5)

**Automated pipeline**:

1. `palette-converter.py` → `monokai-light.json` (HSL: desaturate 12%, darken 18% for accents; invert backgrounds to ~#f9f9fa)
2. `generate-themes.py` → `src/main/resources/themes/*.theme.json` (maps palette to Islands UI keys)
3. `validate-contrast.py` → WCAG AA validation (4.5:1 text, 3.0:1 UI elements)

**Known contrast issues** (require manual palette adjustment):

- Dark: `dimmed3` (comments) 2.88:1 vs 4.5:1 required
- Light: `accent3` 1.71:1, `accent4` 2.34:1, `accent5` 2.18:1 vs. 3.0:1 required

### Islands Theme Integration

**Critical properties** for Islands aesthetic:

- `"parent": "Islands Dark"` or `"Islands Light"` (inherit base styling)
- `"islands": 1` (enable Islands mode)
- `"Island.arc": 20, "Island.borderWidth": 5` (rounded corners)
- Transparent borders: `#00000000` for StatusBar, ToolWindow.Stripe, MainToolbar (preserves Islands design)
- `Island.borderColor: {background}` (seamless integration)

**UI mappings** (`generate-themes.py`):

- Main backgrounds: `MainWindow.background` (dark1), `ToolWindow.background` (background)
- Active accents: `EditorTabs.underlineColor` (accent1), `Button.default.focusedBorderColor` (accent5)
- Selections: `Tree.selectionBackground` (dimmed5 + 80% alpha)

### Editor Schemes

Manual export workflow (automated templating not yet implemented):

1. GoLand Settings → Editor → Color Scheme → Duplicate base (Darcula/IntelliJ Light)
2. Map syntax: Keywords=accent1, Strings=accent3, Functions=accent4, Classes=accent5, Constants=accent2, Annotations=accent6, Comments=dimmed3
3. Export via File → Manage IDE Settings → Export Settings
4. Extract `.xml`, place in `editor-schemes/`, reference in theme JSON: `"editorScheme": "/editor-schemes/monokai-islands-{variant}.xml"`

## Plugin Configuration

**build.gradle.kts** (`generateThemes` task dependency):

```kotlin
tasks {
    register("generateThemes", Exec::class) {
        commandLine("python3", "scripts/generate-themes.py")
    }
    buildPlugin { dependsOn("generateThemes") }
}
```

**src/main/resources/META-INF/plugin.xml**:

- `id`: `com.github.smykla.monokai-islands`
- `since-build`: `253` (GoLand 2025.3+)
- `themeProvider` declarations for both variants

## Development Patterns

### Color Modifications

1. Edit `palettes/monokai-dark.json` (source of truth)
2. Run `palette-converter.py` (regenerates light palette)
3. Run `generate-themes.py` (updates theme JSONs)
4. Run `validate-contrast.py` (check accessibility)
5. `./gradlew buildPlugin && ./gradlew runIde` (test visually)

### Adding UI Properties

Edit `generate-themes.py:ui_colors` dict. Reference [IntelliJ Theme Structure](https://plugins.jetbrains.com/docs/intellij/themes-metadata.html) for property keys.

### Gradle Wrapper

Version pinned: 9.2.1 (per project creation). Upgrade via `./gradlew wrapper --gradle-version X.Y.Z`.

## Gotchas

- **Python linting**: Avoid `l` as variable name (E741). Use `lightness` for HLS color space values.
- **Editorconfig**: All Python files require final newline.
- **Theme caching**: GoLand caches theme files. Restart the IDE after manual JSON edits (buildPlugin handles this).
- **HSL order**: `colorsys.rgb_to_hls()` returns `(h, l, s)` not `(h, s, l)`.
- **Alpha hex**: Append transparency to hex colors: `#rrggbbaa` (e.g., `{dimmed5}80` = 50% opacity).
- **Islands inheritance**: Override only specific properties. Parent theme provides base styling. Excessive overrides break Islands aesthetic.

## References

- [Supporting Islands Theme](https://plugins.jetbrains.com/docs/intellij/supporting-islands-theme.html)
- [Theme Structure](https://plugins.jetbrains.com/docs/intellij/themes-metadata.html)
- [WCAG Contrast Guidelines](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html)
- Monokai color palette: `palettes/monokai-dark.json` (canonical)
