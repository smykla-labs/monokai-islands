---
name: jetbrains-theme-investigation
description: Investigates JetBrains IntelliJ Platform theme properties by searching source code. Use when user asks about UI theming, icon colors, tab spacing, component styling, or needs to find what properties are themeable vs hardcoded in IntelliJ-based IDEs.
---

# JetBrains Theme Investigation

Investigate theme properties in the IntelliJ Platform source code to determine what is themeable via JSON vs hardcoded in source.

## Prerequisites

First check if intellij-community is already cloned:

```bash
find ~/Projects -type d -name "intellij-community" -maxdepth 4 2>/dev/null | head -1
```

Then set `$INTELLIJ_COMMUNITY` to the found path (or clone it first if not found):

```bash
export INTELLIJ_COMMUNITY="/path/to/intellij-community"
```

## Quick Investigation Workflow

1. **Search theme metadata first** (what properties exist):

   ```bash
   grep -i "PropertyName" $INTELLIJ_COMMUNITY/platform/platform-resources/src/themes/metadata/*.json
   ```

2. **Check reference themes** (how properties are used):

   ```bash
   grep -r "PropertyName" $INTELLIJ_COMMUNITY/platform/platform-resources/src/themes/ --include="*.json"
   ```

3. **Search JBUI.java** for programmatic color access:

   ```bash
   grep -A3 "namedColor.*PropertyName" $INTELLIJ_COMMUNITY/platform/util/ui/src/com/intellij/util/ui/JBUI.java
   ```

4. **Search source for hardcoded values** if not found above:

   ```bash
   grep -rn "propertyName\|PROPERTY_NAME" $INTELLIJ_COMMUNITY/platform/platform-*/src --include="*.kt" --include="*.java"
   ```

## Key Directories

| Path                                                  | Content                                                                                  |
|-------------------------------------------------------|------------------------------------------------------------------------------------------|
| `platform/platform-resources/src/themes/metadata/`    | Available theme properties (IntelliJPlatform.themeMetadata.json, JDK.themeMetadata.json) |
| `platform/platform-resources/src/themes/`             | Reference themes (expUI, islands, darcula, HighContrast)                                 |
| `platform/util/ui/src/com/intellij/util/ui/JBUI.java` | CurrentTheme methods mapping to JSON keys                                                |
| `platform/icons/src/`                                 | SVG icons (check colors used)                                                            |

## Detailed References

For specific subsystems, see:

- **Icon colors**: [icon-colors.md](icon-colors.md) - ColorPalette, stroke colors, named keys
- **Tab system**: [tab-system.md](tab-system.md) - Tab gaps, insets, layouts
- **Islands theme**: [islands-theme.md](islands-theme.md) - Islands-specific properties
- **Known limitations**: [limitations.md](limitations.md) - What is NOT themeable

## Determining if Themeable

| Found In                             | Result                               |
|--------------------------------------|--------------------------------------|
| Theme metadata JSON                  | Themeable via JSON                   |
| JBUI.java with `namedColor`/`getInt` | Themeable via JSON                   |
| Source code with hardcoded values    | NOT themeable (requires code change) |
