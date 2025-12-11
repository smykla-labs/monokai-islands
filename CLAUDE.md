# Monokai Islands Theme Plugin

## Project Overview

JetBrains GoLand theme plugin combining Monokai color palette with Islands UI aesthetics. Gradle-based IntelliJ Platform plugin (since build 253, GoLand 2025.3+). Dark variant with automated generation from JSON palette definition.

## Build & Workflow

```bash
mise run lint                             # Run all linters (Python, Kotlin, editorconfig, Markdown)
./gradlew buildPlugin                     # Build plugin (auto-runs generateThemes task)
./gradlew runIde                          # Test in a GoLand instance
python3 scripts/generate-themes.py        # Generate theme JSON
python3 scripts/validate-contrast.py      # Check WCAG compliance
```

## Architecture

### Color System

**Single source of truth**: `palettes/monokai-dark.json` (17 colors: background, dark1-2, text, accent1-6, dimmed1-5)

**Automated pipeline**:

1. `generate-themes.py` → `src/main/resources/themes/monokai-islands-dark.theme.json` (maps palette to Islands UI keys)
2. `validate-contrast.py` → WCAG AA validation (4.5:1 text, 3.0:1 UI elements)

**Known contrast issues** (require manual palette adjustment):

- Dark: `dimmed3` (comments) 2.88:1 vs 4.5:1 required

### Islands Theme Integration

**Critical properties** for Islands aesthetic:

- `"parent": "Islands Dark"` (inherit base styling)
- `"islands": 1` (enable Islands mode)
- `"Island.arc": 20, "Island.borderWidth": 5` (rounded corners)
- Transparent borders: `#00000000` for StatusBar, ToolWindow.Stripe, MainToolbar (preserves Islands design)
- `Island.borderColor: {background}` (seamless integration)

**UI mappings** (`generate-themes.py`):

- Main backgrounds: `MainWindow.background` (dark1), `ToolWindow.background` (background)
- Active accents: `EditorTabs.underlineColor` (accent1), `Button.default.focusedBorderColor` (accent5)
- Selections: `Tree.selectionBackground` (dimmed5 + 80% alpha)

### Editor Schemes

**Location**: `src/main/resources/editor-schemes/monokai-islands-dark.xml`

Manual editing workflow:

1. Edit the XML directly in `src/main/resources/editor-schemes/`
2. Key sections:
   - `<colors>`: Simple color values (SELECTION_BACKGROUND, CARET_COLOR, etc.)
   - `<attributes>`: TextAttributes with nested values (DIFF_INSERTED, syntax highlighting, etc.)
3. Reference in theme JSON: `"editorScheme": "/editor-schemes/monokai-islands-dark.xml"`
4. Rebuild with `./gradlew clean buildPlugin` to pick up changes

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
- `themeProvider` declaration for dark variant

## Changelog & Release Notes

**Fully automated** from conventional commits via semantic-release.

**Pipeline**:

1. `@semantic-release/changelog` generates `CHANGELOG.md` from conventional commits
2. `parseChangelogToHtml()` in `build.gradle.kts` reads CHANGELOG.md and converts to HTML
3. `patchPluginXml` injects HTML into plugin.xml `<change-notes>`
4. GitHub release created with same notes

**Commit message format** (determines version bump):

- `feat(scope): description` → minor version bump, appears in "Features"
- `fix(scope): description` → patch version bump, appears in "Bug Fixes"
- `perf(scope): description` → patch version bump, appears in "Performance"
- `feat!:` or `BREAKING CHANGE:` → major version bump

**Release automation** (semantic-release):

1. Analyzes commits to determine version bump
2. Generates CHANGELOG.md from commit messages
3. Updates `gradle.properties` with new version
4. Builds, signs, and publishes plugin to JetBrains Marketplace
5. Commits CHANGELOG.md and gradle.properties
6. Creates GitHub release with artifacts

## Development Patterns

### Color Modifications

1. Edit `palettes/monokai-dark.json` (source of truth)
2. Run `generate-themes.py` (updates theme JSON)
3. Run `validate-contrast.py` (check accessibility)
4. `./gradlew buildPlugin && ./gradlew runIde` (test visually)

### Adding UI Properties

Edit `generate-themes.py:ui_colors` dict. Reference [IntelliJ Theme Structure](https://plugins.jetbrains.com/docs/intellij/themes-metadata.html) for property keys.

### Gradle Wrapper

Version pinned: 9.2.1 (per project creation). Upgrade via `./gradlew wrapper --gradle-version X.Y.Z`.

### Development Sandbox

The `prepareSandbox` task auto-configures the IDE sandbox for theme development:

**Config files** (in `build/idea-sandbox/GO-2025.3/config/options/`):

- `laf.xml` — UI theme selection (Monokai Islands Dark)
- `colors.scheme.xml` — Editor color scheme
- `ui.lnf.xml` — Tab placement (right side), toolbar visibility
- `window.manager.xml`, `window.state.xml` — Window maximization
- `window.info.xml` — Tool window layout (Project pane width)
- `registry.xml` — UI inspector settings
- `disabled_plugins.txt` — Faster startup (disables Copyright, Database, Terminal, etc.)
- `idea.properties` — `idea.is.internal=true` for dev features

**Environment variables** (`.envrc`):

```bash
# Auto-open projects/files in sandbox IDE
export RUNIDE_PROJECT_PATHS="../project1,../project2"
export RUNIDE_FILES="../project1/main.go"
```

## Gotchas

- **Python linting**: Avoid `l` as variable name (E741). Use `lightness` for HLS color space values.
- **Editorconfig**: All Python files require final newline.
- **Theme caching**: GoLand caches theme files. Restart the IDE after manual JSON edits (buildPlugin handles this).
- **HSL order**: `colorsys.rgb_to_hls()` returns `(h, l, s)` not `(h, s, l)`.
- **Alpha hex**: Append transparency to hex colors: `#rrggbbaa` (e.g., `{dimmed5}80` = 50% opacity).
- **Islands inheritance**: Override only specific properties. Parent theme provides base styling. Excessive overrides break Islands aesthetic.

## References

### UI Theme Structure

- [Supporting Islands Theme](https://plugins.jetbrains.com/docs/intellij/supporting-islands-theme.html)
- [Theme Structure](https://plugins.jetbrains.com/docs/intellij/themes-metadata.html)
- [Platform Theme Colors](https://plugins.jetbrains.com/docs/intellij/platform-theme-colors.html)
- [WCAG Contrast Guidelines](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html)
- Monokai color palette: `palettes/monokai-dark.json` (canonical)

### Editor Color Schemes

**Official Documentation:**

- [Themes - Editor Schemes and Background Images](https://plugins.jetbrains.com/docs/intellij/themes-extras.html) - Editor scheme integration
- [Color Scheme Management](https://plugins.jetbrains.com/docs/intellij/color-scheme-management.html) - API and structure
- [EditorColors.java](https://github.com/JetBrains/intellij-community/blob/master/platform/editor-ui-api/src/com/intellij/openapi/editor/colors/EditorColors.java) - Color key definitions (SELECTION_BACKGROUND, CARET_COLOR, etc.)

**Reference Color Schemes:**

- [Darcula.icls](https://github.com/phillipjohnson/intellij-colorblind-settings/blob/master/Darcula.icls) - Official dark theme reference
- [GitHub.icls](https://github.com/orrsella/intellij-github-scheme/blob/master/GitHub.icls) - Light theme example
- [Solarized Dark.icls](https://raw.githubusercontent.com/jkaving/intellij-colors-solarized/master/Solarized%20Dark.icls) - Diff attributes reference

**Community Resources:**

- [Diff viewer colors explained](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000243060-What-do-the-colors-in-the-Diff-viewer-mean-) - DIFF_INSERTED, DIFF_DELETED, DIFF_MODIFIED
- [Editor selection color configuration](https://intellij-support.jetbrains.com/hc/en-us/community/posts/206801025-Help-with-Editor-Selection-Color-Font-Scheme) - SELECTION_BACKGROUND usage
