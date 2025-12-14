<p align="center">
  <img src="src/main/resources/META-INF/pluginIcon.svg" alt="Monokai Islands" width="128" height="128">
</p>

<h1 align="center">Monokai Islands Theme</h1>

<!-- Plugin description -->
<p align="center">
  A dark theme for JetBrains IDEs combining the <b>Monokai</b> color palette with the modern <b>Islands</b> UI.
</p>
<!-- Plugin description end -->

<p align="center">
  <a href="https://plugins.jetbrains.com/plugin/29325-monokai-islands-theme"><img src="https://img.shields.io/jetbrains/plugin/v/29325?label=JetBrains%20Marketplace&logo=jetbrains" alt="JetBrains Plugin"></a>
  <a href="https://plugins.jetbrains.com/plugin/29325-monokai-islands-theme"><img src="https://img.shields.io/jetbrains/plugin/d/29325" alt="Downloads"></a>
  <a href="https://plugins.jetbrains.com/plugin/29325-monokai-islands-theme"><img src="https://img.shields.io/jetbrains/plugin/r/rating/29325" alt="Rating"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/smykla-labs/monokai-islands" alt="License"></a>
</p>

---

## Features

- **Monokai Colors** - Classic warm palette with pink keywords, green functions, yellow strings, and purple constants
- **Islands UI Integration** - Rounded corners, subtle borders, and modern aesthetic
- **Semantic Syntax Highlighting** - Carefully tuned colors for enhanced code readability across multiple languages
- **Warm Purple-Tinted UI** - Consistent warm tones throughout the interface
- **Custom Markdown Preview Styling** - Optional custom styling for Markdown preview with Monokai theme colors (disabled by default)

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
- **Via Tools Menu**: Go to **Tools** → **Monokai Islands: Toggle Markdown Preview Styling**
- **Via Find Action**: Press <kbd>⌘</kbd><kbd>⇧</kbd><kbd>A</kbd> (macOS) or <kbd>Ctrl</kbd><kbd>Shift</kbd><kbd>A</kbd> (Windows/Linux) and search for "Monokai Islands"

When enabled, the Markdown preview will use Monokai-themed colors for code blocks, headings, and other elements. The setting persists across IDE restarts and automatically applies when switching to the Monokai Islands theme.

## Color Palette

<table>
  <thead>
    <tr>
      <th align="center">Color</th>
      <th>Hex</th>
      <th>Usage</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Pink-ff6188?style=flat&color=ff6188" alt="Pink"/></td>
      <td><code>#ff6188</code></td>
      <td>Keywords (<code>func</code>, <code>var</code>, <code>if</code>, <code>return</code>)</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Green-a9dc76?style=flat&color=a9dc76" alt="Green"/></td>
      <td><code>#a9dc76</code></td>
      <td>Function declarations and calls</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Yellow-ffd866?style=flat&color=ffd866" alt="Yellow"/></td>
      <td><code>#ffd866</code></td>
      <td>Strings</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Orange-fc9867?style=flat&color=fc9867" alt="Orange"/></td>
      <td><code>#fc9867</code></td>
      <td>Numbers, parameters</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Cyan-78dce8?style=flat&color=78dce8" alt="Cyan"/></td>
      <td><code>#78dce8</code></td>
      <td>Types, packages, method calls, builtins</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Purple-ab9df2?style=flat&color=ab9df2" alt="Purple"/></td>
      <td><code>#ab9df2</code></td>
      <td>Constants, struct fields</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/White-fcfcfa?style=flat&color=fcfcfa" alt="White"/></td>
      <td><code>#fcfcfa</code></td>
      <td>Text</td>
    </tr>
    <tr>
      <td align="center"><img src="https://img.shields.io/badge/Background-221f22?style=flat&color=221f22" alt="Background"/></td>
      <td><code>#221f22</code></td>
      <td>Editor background</td>
    </tr>
  </tbody>
</table>

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

## Credits

- Color palette inspired by [Monokai Pro](https://monokai.pro/)
- Built for JetBrains [Islands UI](https://plugins.jetbrains.com/docs/intellij/supporting-islands-theme.html)

## License

[MIT](LICENSE)
