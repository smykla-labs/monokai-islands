# Monokai Islands Theme Plugin

JetBrains GoLand theme plugin combining Monokai color palette with Islands UI aesthetics. Gradle-based IntelliJ Platform plugin (since build 253, GoLand 2025.3+). Dark variant with automated generation from JSON palette definition.

## Project Structure

```text
palettes/                    # Color definitions (source of truth)
  monokai-dark.json          # 17-color palette
scripts/
  generate-themes.py         # Palette → theme JSON generator
  validate-contrast.py       # WCAG AA contrast checker
src/main/resources/
  themes/                    # Generated theme JSON (do not edit directly)
  editor-schemes/            # Editor color scheme XML
  META-INF/plugin.xml        # Plugin manifest
src/main/kotlin/             # Kotlin code (production features only)
```

## Build & Workflow

```bash
mise run lint                             # Run all linters (Python, Kotlin, editorconfig, Markdown)
./gradlew buildPlugin                     # Build plugin (auto-runs generateThemes task)
./gradlew runIde                          # Test in a GoLand instance
python3 scripts/generate-themes.py        # Generate theme JSON
python3 scripts/validate-contrast.py      # Check WCAG compliance
```

### Development Workflow

**Theme plugins cannot hot reload** in IntelliJ Platform - even pure resource JARs have classloader retention issues due to UIDefaults caching, component references, and LafManager state.

**Recommended workflow:**

```bash
./gradlew dev
# or
./scripts/dev.sh
```

**What happens:**

1. Starts sandbox IDE with `-PdevMode` flag (produces pure theme JAR, no Kotlin classes)
2. Starts palette file watcher (auto-runs `generate-themes.py` on changes)
3. Starts continuous build watcher (auto-runs `buildPlugin` on changes)
4. Edit `palettes/monokai-dark.json` → theme regenerates + rebuilds automatically
5. **Manual IDE restart required** to see theme changes (Cmd+Q, then re-run script)

**First-time setup (one-time per sandbox):**

1. Run `./gradlew dev`
2. IDE opens with default "Islands Dark" theme (plugin loads but theme not auto-selected)
3. Go to Settings → Appearance & Behavior → Appearance
4. Select "Monokai Islands Dark" from Theme dropdown
5. Theme persists in `laf.xml` for all future dev runs

**Why manual selection is needed:** IntelliJ's LAF (Look & Feel) initializes before plugins load, so plugin-provided themes can't be auto-selected on first run. After manual selection once, the theme persists.

**Technical details:**

- **Dev mode** (`-PdevMode` flag): Comments out `postStartupActivity` and `applicationListeners` in plugin.xml, strips Kotlin classes/metadata from JAR → produces pure 9.9K theme-only JAR
- **Production mode**: Includes all features (Markdown CSS customization via ThemeChangeListener)
- **Why hot reload fails**: Theme resources cached in Swing UIDefaults hold classloader references; even theme-only plugins cannot be unloaded by GC
- Cross-platform stat detection: handles BSD `stat`, GNU `gstat`, Linux `stat`
- Three concurrent processes: runIde (bg), palette watcher (bg), buildPlugin --continuous (fg)
- Cleanup on Ctrl+C kills all processes

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

- `laf.xml` — UI theme selection (set to Monokai Islands Dark after manual selection)
- `colors.scheme.xml` — Editor color scheme (Monokai Islands Dark)
- `other.xml` — UI font settings (default Inter at size 15)
- `editor.xml` — Editor font (Fira Code, size 15, ligatures enabled)
- `terminal.xml` — Terminal font (Fira Code, size 15)
- `ui.lnf.xml` — Tab placement (bottom), toolbar visibility
- `window.info.xml` — Tool window layout (Project pane width)
- `registry.xml` — UI inspector settings
- `disabled_plugins.txt` — Faster startup (disables Copyright, Database, Terminal, etc.)
- `idea.properties` — `idea.is.internal=true` for dev features

**Font configuration:**

- **UI**: Default JetBrains font (Inter) at size 15
- **Editor**: Fira Code at size 15 with ligatures
- **Terminal**: Fira Code at size 15

**Environment variables** (`.envrc`):

```bash
# Auto-open projects/files in sandbox IDE
export RUNIDE_PROJECT_PATHS="../project1,../project2"
export RUNIDE_FILES="../project1/main.go"
```

## Conditional Build System (Dev vs Production)

**Purpose:** Produce minimal theme-only JAR in dev mode while keeping production features (Markdown CSS) in releases.

**Mechanism:**

1. **Token markers** in `src/main/resources/META-INF/plugin.xml`:

   ```xml
   @@PRODUCTION_FEATURES_START@@
   <postStartupActivity implementation="..." />
   <applicationListeners>...</applicationListeners>
   @@PRODUCTION_FEATURES_END@@
   ```

2. **Dev mode detection** (`build.gradle.kts`):

   ```kotlin
   val requestedTasks = gradle.startParameter.taskNames
   val isDevMode = requestedTasks.any {
     it.contains("runIde") || it.contains("prepareSandbox") || it.contains("dev")
   } || project.hasProperty("devMode")
   ```

3. **Token replacement** (`processResources.doLast`):
   - Dev: `@@PRODUCTION_FEATURES_START@@` → `<!-- Production features disabled`
   - Prod: Both markers → `` (removed)

4. **JAR stripping** (`prepareSandbox.doLast`):
   - Dev: Uses `zip -d` to remove Kotlin classes, empty directories, and module metadata from sandbox JAR
   - Prod: JAR unchanged (includes all production features)
   - Result: Dev JAR ~9.9K (pure resources), Prod JAR ~19K (includes classes)

**Note:** Even with pure theme-only JARs, hot reload doesn't work due to IntelliJ Platform's UIDefaults caching holding classloader references. The build optimization reduces JAR size and simplifies dev builds but doesn't enable hot reload.

## Anti-Patterns

**DO NOT:**

- Add colors directly to theme JSON — define in `palettes/monokai-dark.json` (base palette) or `generate-themes.py` (derived colors)
- Override Islands parent properties unnecessarily — inherit where possible
- Skip contrast validation — run `validate-contrast.py` before committing color changes
- Add Kotlin/Java code for theme-only features — theme plugins should be declarative (JSON/XML)
- Create new extension points in plugin.xml — they break hot reload; use token markers for dev/prod split

**Simplicity Principles:**

- This is a theme plugin — no business logic, no complex state management
- Base palette in `palettes/monokai-dark.json`, derived/UI colors defined in `generate-themes.py`
- Automated pipeline: palette → generate-themes.py → theme JSON → buildPlugin
- Minimal Kotlin code — only for production features (Markdown CSS) that require runtime behavior

## Gotchas

- **Python linting**: Avoid `l` as variable name (E741). Use `lightness` for HLS color space values.
- **Editorconfig**: All Python files require final newline.
- **Theme caching**: GoLand caches theme files. Restart the IDE after manual JSON edits (buildPlugin handles this).
- **HSL order**: `colorsys.rgb_to_hls()` returns `(h, l, s)` not `(h, s, l)`.
- **Alpha hex**: Append transparency to hex colors: `#rrggbbaa` (e.g., `{dimmed5}80` = 50% opacity).
- **Islands inheritance**: Override only specific properties. Parent theme provides base styling. Excessive overrides break Islands aesthetic.
- **Dev mode flag**: Always use `-PdevMode` when running dev tasks, otherwise production features will block hot reload.

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
