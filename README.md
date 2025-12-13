# Monokai Islands Theme

<!-- Plugin description -->
A dark theme for JetBrains IDEs combining the iconic **Monokai** color palette with the modern **Islands** UI design language.

## Features

- **Monokai Colors** - Classic warm palette with pink keywords, green functions, yellow strings, and purple constants
- **Islands UI Integration** - Rounded corners, subtle borders, and modern aesthetic
- **Go-Optimized Syntax Highlighting** - Carefully tuned colors for Go development with semantic differentiation
- **Warm Purple-Tinted UI** - Consistent warm tones throughout the interface
- **Custom Markdown Preview Styling** - Optional Monokai-themed CSS for Markdown preview (opt-in)
<!-- Plugin description end -->

## Installation

### From JetBrains Marketplace

1. Open your JetBrains IDE (GoLand, IntelliJ IDEA, WebStorm, etc.)
2. Go to **Settings** → **Plugins** → **Marketplace**
3. Search for "Monokai Islands Theme"
4. Click **Install** and restart the IDE

### Manual Installation

1. Download the latest release from [Releases](https://github.com/smykla-labs/monokai-islands/releases)
2. Go to **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk...**
3. Select the downloaded `.zip` file
4. Restart the IDE

## Configuration

### Markdown Preview Styling

The theme includes optional custom CSS for Markdown preview that matches the Monokai color scheme. This feature is **disabled by default** to respect your existing Markdown settings.

**To enable:**

- **Via Settings**: Go to **Settings** → **Appearance & Behavior** → **Monokai Islands** → Enable "Custom Markdown Preview Styling"
- **Via Tools Menu**: Go to **Tools** → **Monokai Islands: Custom Markdown Preview Styling**
- **Via Find Action**: Press <kbd>⌘</kbd><kbd>⇧</kbd><kbd>A</kbd> (macOS) or <kbd>Ctrl</kbd><kbd>Shift</kbd><kbd>A</kbd> (Windows/Linux) and search for "Monokai Islands"

When enabled, the Markdown preview will use Monokai-themed colors for code blocks, headings, and other elements. The setting persists across IDE restarts and automatically applies when switching to the Monokai Islands theme.

## Color Palette

| Color                                                                      | Hex       | Usage                                    |
|----------------------------------------------------------------------------|-----------|------------------------------------------|
| ![#ff6188](https://via.placeholder.com/16/ff6188/ff6188?text=+) Pink       | `#ff6188` | Keywords (`func`, `var`, `if`, `return`) |
| ![#a9dc76](https://via.placeholder.com/16/a9dc76/a9dc76?text=+) Green      | `#a9dc76` | Function declarations and calls          |
| ![#ffd866](https://via.placeholder.com/16/ffd866/ffd866?text=+) Yellow     | `#ffd866` | Strings                                  |
| ![#fc9867](https://via.placeholder.com/16/fc9867/fc9867?text=+) Orange     | `#fc9867` | Numbers, parameters                      |
| ![#78dce8](https://via.placeholder.com/16/78dce8/78dce8?text=+) Cyan       | `#78dce8` | Types, packages, method calls, builtins  |
| ![#ab9df2](https://via.placeholder.com/16/ab9df2/ab9df2?text=+) Purple     | `#ab9df2` | Constants, struct fields                 |
| ![#fcfcfa](https://via.placeholder.com/16/fcfcfa/fcfcfa?text=+) White      | `#fcfcfa` | Text                                     |
| ![#221f22](https://via.placeholder.com/16/221f22/221f22?text=+) Background | `#221f22` | Editor background                        |

## Compatibility

- **IDE Version**: 2025.3+ (build 253+)
- **Supported IDEs**: All JetBrains IDEs (GoLand, IntelliJ IDEA, WebStorm, PyCharm, etc.)

## Development

### Prerequisites

- JDK 17+
- Python 3.x (for theme generation scripts)

### Build

```bash
./gradlew buildPlugin
```

The plugin will be generated at `build/distributions/monokai-islands-*.zip`

### Test in IDE

```bash
./gradlew runIde
```

### Generate Themes

Theme JSON is auto-generated from the palette definition:

```bash
python3 scripts/generate-themes.py
```

### Validate Contrast (WCAG)

```bash
python3 scripts/validate-contrast.py
```

## Project Structure

```text
├── palettes/
│   └── monokai-dark.json          # Color palette definition (source of truth)
├── scripts/
│   ├── generate-themes.py         # Generates theme JSON from palette
│   └── validate-contrast.py       # WCAG contrast validation
├── src/main/resources/
│   ├── META-INF/plugin.xml        # Plugin configuration
│   ├── themes/                    # Generated theme JSON
│   └── editor-schemes/            # Editor color schemes
└── build.gradle.kts               # Gradle build configuration
```

## License

[MIT](LICENSE)

## Credits

- Color palette inspired by [Monokai Pro](https://monokai.pro/)
- Built for JetBrains [Islands UI](https://plugins.jetbrains.com/docs/intellij/supporting-islands-theme.html)
