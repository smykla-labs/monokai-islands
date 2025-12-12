import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
    id("org.jetbrains.intellij.platform") version "2.10.5"
    kotlin("jvm") version "2.2.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

kotlin {
    jvmToolchain(21)
}

group = "com.github.smykla-labs"
version = project.property("version") as String

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Use GoLand as target IDE for development/testing
        // Theme still works across ALL JetBrains IDEs via com.intellij.modules.platform dependency
        goland("2025.3")
    }

    // Exclude Kotlin stdlib from runtime classpath only (keep for compilation)
    // Production features use only Java reflection APIs, don't need Kotlin at runtime
    configurations.named("runtimeClasspath") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

intellijPlatform {
    // Theme-only plugin has no searchable options (settings UI)
    buildSearchableOptions = false

    pluginConfiguration {
        id = "com.github.smykla-labs.monokai-islands"
        name = "Monokai Islands Theme"
        version = project.property("version") as String

        // Extract description from README.md between <!-- Plugin description --> markers
        description = provider { extractPluginDescription() }

        ideaVersion {
            sinceBuild = "253"
            untilBuild = provider { null }
        }

        changeNotes = provider {
            parseChangelogToHtml(project.version.toString())
        }
    }

    signing {
        certificateChain = providers.environmentVariable("JETBRAINS_CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("JETBRAINS_PRIVATE_KEY")
        password = providers.environmentVariable("JETBRAINS_PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN")
    }

    pluginVerification {
        ides {
            // Verify against GoLand only to avoid CI disk space issues
            create(IntelliJPlatformType.GoLand, "2025.3")
        }
    }
}

detekt {
    toolVersion = "1.23.8"
    config.setFrom("$projectDir/detekt.yml")
    buildUponDefaultConfig = true
}

/**
 * Extracts plugin description from README.md between <!-- Plugin description --> markers
 * and converts Markdown to simple HTML.
 */
fun extractPluginDescription(): String {
    val readmeFile = file("README.md")
    if (!readmeFile.exists()) {
        return "<p>A dark theme for JetBrains IDEs combining Monokai colors with Islands UI.</p>"
    }

    val content = readmeFile.readText()
    val startMarker = "<!-- Plugin description -->"
    val endMarker = "<!-- Plugin description end -->"

    val startIndex = content.indexOf(startMarker)
    val endIndex = content.indexOf(endMarker)

    if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
        return "<p>A dark theme for JetBrains IDEs combining Monokai colors with Islands UI.</p>"
    }

    val description = content.substring(startIndex + startMarker.length, endIndex).trim()

    // Convert Markdown to simple HTML
    return description
        .replace(Regex("""^### (.+)$""", RegexOption.MULTILINE)) { "<h3>${it.groupValues[1]}</h3>" }
        .replace(Regex("""^## (.+)$""", RegexOption.MULTILINE)) { "<h2>${it.groupValues[1]}</h2>" }
        .replace(Regex("""\*\*([^*]+)\*\*""")) { "<b>${it.groupValues[1]}</b>" }
        .replace(Regex("""^\s*-\s+(.+)$""", RegexOption.MULTILINE)) { "<li>${it.groupValues[1]}</li>" }
        .replace(Regex("""(<li>.*</li>\n?)+""")) { "<ul>${it.value}</ul>" }
        .replace(Regex("""\n\n+""")) { "</p><p>" }
        .let { "<p>$it</p>" }
        .replace("<p></p>", "")
        .replace("<p><h", "<h")
        .replace("</h2></p>", "</h2>")
        .replace("</h3></p>", "</h3>")
        .replace("<p><ul>", "<ul>")
        .replace("</ul></p>", "</ul>")
        .trim()
}

/**
 * Parses CHANGELOG.md (conventional-changelog format from semantic-release) and extracts
 * the latest version's content as HTML for plugin.xml change-notes.
 */
fun parseChangelogToHtml(version: String): String {
    val changelogFile = file("CHANGELOG.md")
    if (!changelogFile.exists()) {
        return "<p>See <a href=\"https://github.com/smykla-labs/monokai-islands/releases\">GitHub Releases</a></p>"
    }

    val content = changelogFile.readText()
    val versionPattern = """^#+ \[?${Regex.escape(version)}]?""".toRegex(RegexOption.MULTILINE)
    val nextVersionPattern = """^#+ \[?\d+\.\d+\.\d+]?""".toRegex(RegexOption.MULTILINE)

    val versionMatch = versionPattern.find(content) ?: return "<p>Version $version</p>"
    val startIndex = versionMatch.range.first
    val remainingContent = content.substring(versionMatch.range.last + 1)
    val nextMatch = nextVersionPattern.find(remainingContent)
    val endIndex = if (nextMatch != null) {
        versionMatch.range.last + 1 + nextMatch.range.first
    } else {
        content.length
    }

    val versionContent = content.substring(startIndex, endIndex).trim()

    // Convert markdown to simple HTML
    // Pattern: * **scope:** description (scope may or may not include trailing colon)
    val boldItemPattern = Regex("""^\* \*\*([^*:]+):?\*\* (.+)$""", RegexOption.MULTILINE)

    return versionContent
        .lines()
        .drop(1) // Skip version header
        .joinToString("\n")
        .trim()
        .replace(Regex("""^### (.+)$""", RegexOption.MULTILINE)) {
            "<h3>${it.groupValues[1]}</h3>"
        }
        .replace(boldItemPattern) {
            "<li><b>${it.groupValues[1]}</b>: ${it.groupValues[2]}</li>"
        }
        .replace(Regex("""^\* (.+)$""", RegexOption.MULTILINE)) {
            "<li>${it.groupValues[1]}</li>"
        }
        .replace(Regex("""^- (.+)$""", RegexOption.MULTILINE)) {
            "<li>${it.groupValues[1]}</li>"
        }
        .replace(Regex("""(<li>.*</li>\n?)+""")) { "<ul>${it.value}</ul>" }
        .replace(Regex("""\[([^\]]+)\]\([^)]+\)""")) { it.groupValues[1] }
        .trim()
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "21"
    }

    withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
        jvmTarget = "21"
    }

    register("generateThemes", Exec::class) {
        commandLine("python3", "scripts/generate-themes.py")
    }

    // Process plugin.xml based on build mode (dev vs production)
    processResources {
        doLast {
            // Dev mode: running sandbox tasks (runIde, prepareSandbox) or has devMode property
            // Production mode: buildPlugin, verifyPlugin, publishPlugin
            val requestedTasks = gradle.startParameter.taskNames
            val isDevMode = requestedTasks.any { task ->
                task.contains("runIde") ||
                task.contains("prepareSandbox") ||
                task.contains("dev")
            } || project.hasProperty("devMode")

            val pluginXml = File(destinationDir, "META-INF/plugin.xml")
            if (pluginXml.exists()) {
                val content = pluginXml.readText()
                val processed = if (isDevMode) {
                    // Dev mode: comment out production features for hot reload support
                    content
                        .replace("@@PRODUCTION_FEATURES_START@@", "<!-- Production features disabled in dev mode")
                        .replace("@@PRODUCTION_FEATURES_END@@", "-->")
                } else {
                    // Production mode: include all features (Markdown CSS customization)
                    content
                        .replace("@@PRODUCTION_FEATURES_START@@", "")
                        .replace("@@PRODUCTION_FEATURES_END@@", "")
                }
                pluginXml.writeText(processed)
            }
        }
    }


    buildPlugin {
        dependsOn("generateThemes")
    }

    prepareSandbox {
        outputs.upToDateWhen { false }  // Always run to ensure sandbox config is fresh
        doLast {
            val configDir = sandboxConfigDirectory.get().asFile.resolve("options")
            configDir.mkdirs()

            println("🎨 Configuring sandbox IDE theme and fonts...")

            // Configure sandbox to auto-open projects in new window (0=new window, 1=same window, -1=ask)
            configDir.resolve("ide.general.xml").writeText("""
                <application>
                    <component name="GeneralSettings">
                        <option name="confirmOpenNewProject2" value="0" />
                    </component>
                </application>
            """.trimIndent())

            // Set theme in laf.xml (works on second run after plugin is installed)
            configDir.resolve("laf.xml").writeText("""
                <application>
                    <component name="LafManager" autodetect="false">
                        <laf themeId="com.github.smykla-labs.monokai-islands-dark" />
                    </component>
                </application>
            """.trimIndent())

            // UI settings: tabs at bottom, UI font size (default font at size 15)
            configDir.resolve("ui.lnf.xml").writeText("""
                <application>
                    <component name="UISettings">
                        <option name="EDITOR_TAB_PLACEMENT" value="4" />
                        <option name="HIDE_TOOL_STRIPES" value="false" />
                        <option name="SHOW_MAIN_TOOLBAR" value="true" />
                        <option name="overrideLafFonts" value="true" />
                    </component>
                </application>
            """.trimIndent())

            // UI font settings: default font (Inter) at size 15
            configDir.resolve("other.xml").writeText("""
                <application>
                    <component name="NotRoamableUiSettings">
                        <option name="overrideLafFonts" value="true" />
                        <option name="fontSize" value="15.0" />
                    </component>
                </application>
            """.trimIndent())

            // Editor color scheme selection
            configDir.resolve("colors.scheme.xml").writeText("""
                <application>
                    <component name="EditorColorsManagerImpl">
                        <global_color_scheme name="Monokai Islands Dark" />
                    </component>
                </application>
            """.trimIndent())

            // Editor font settings (Fira Code with ligatures)
            configDir.resolve("editor.xml").writeText("""
                <application>
                    <component name="EditorSettings">
                        <option name="IS_ENSURE_NEWLINE_AT_EOF" value="true" />
                    </component>
                    <component name="DefaultFont">
                        <option name="FONT_FAMILY" value="Fira Code" />
                        <option name="FONT_SIZE" value="15" />
                        <option name="FONT_SIZE_2D" value="15.0" />
                        <option name="LINE_SPACING" value="1.2" />
                        <option name="FONT_LIGATURES" value="true" />
                    </component>
                </application>
            """.trimIndent())

            // Terminal font settings (even though plugin is disabled, config ready if enabled)
            configDir.resolve("terminal.xml").writeText("""
                <application>
                    <component name="TerminalOptionsProvider">
                        <option name="myFontFace" value="Fira Code" />
                        <option name="myFontSize" value="15" />
                    </component>
                </application>
            """.trimIndent())

            // Tool window layout: Project pane width
            configDir.resolve("window.info.xml").writeText("""
                <application>
                    <component name="WindowInfo">
                        <window_info id="Project" active="true" anchor="left" auto_hide="false"
                            internal_type="DOCKED" type="DOCKED" visible="true"
                            weight="0.15" sideWeight="0.5" order="0" side_tool="false" />
                    </component>
                </application>
            """.trimIndent())

            // Configure registry settings for UI development
            configDir.resolve("registry.xml").writeText("""
                <application>
                    <component name="Registry">
                        <entry key="ui.inspector.save.stacktraces" value="true" />
                        <entry key="ui.inspector.accessibility.audit" value="true" />
                        <entry key="ide.debugMode" value="true" />
                    </component>
                </application>
            """.trimIndent())

            // Enable internal mode for additional IDE features
            sandboxConfigDirectory.get().asFile.resolve("idea.properties").writeText("""
                idea.is.internal=true
            """.trimIndent())

            // Disable unnecessary plugins for faster dev startup
            // Note: Git4Idea and yaml required by other bundled plugins (Kubernetes, GitLab, Backup and Sync)
            sandboxConfigDirectory.get().asFile.resolve("disabled_plugins.txt").writeText(
                listOf(
                    "com.intellij.copyright",
                    "org.intellij.plugins.markdown",
                    "com.intellij.database",
                    "com.intellij.httpClient",
                    "org.jetbrains.plugins.terminal",
                    "com.jetbrains.sh",
                    "org.jetbrains.plugins.github",
                    "com.intellij.tasks",
                    "org.intellij.intelliLang",
                    "com.goide.golinter",
                ).joinToString("\n")
            )

            println("✓ Sandbox configured (select theme manually on first run)")

            // In dev mode, strip production classes from plugin JAR (smaller dev builds)
            val requestedTasks = gradle.startParameter.taskNames
            val isDevMode = requestedTasks.any { task ->
                task.contains("runIde") ||
                task.contains("prepareSandbox") ||
                task.contains("dev")
            } || project.hasProperty("devMode")

            if (isDevMode) {
                val pluginLib = sandboxPluginsDirectory.get().asFile
                    .resolve("monokai-islands/lib")
                val jarFile = pluginLib.listFiles()?.firstOrNull { it.name.endsWith(".jar") }

                if (jarFile != null && jarFile.exists()) {
                    // Remove Kotlin classes and metadata for pure theme-only dev JAR (~10KB vs ~19KB)
                    val patterns = listOf(
                        "com/github/smykla/monokaiislands/listeners/*",
                        "com/github/smykla/monokaiislands/startup/*",
                        "com/github/smykla/*",
                        "com/github/*",
                        "com/*",
                        "META-INF/*.kotlin_module"
                    )
                    patterns.forEach { pattern ->
                        ProcessBuilder("zip", "-d", jarFile.name, pattern)
                            .directory(jarFile.parentFile)
                            .redirectErrorStream(true)
                            .start()
                            .waitFor()
                    }
                    println("🗑️ Stripped production classes from dev JAR")
                }
            }
        }
    }

    runIde {
        // Auto-open projects/files from env vars (comma-separated, optional)
        // Example: RUNIDE_PROJECT_PATHS="~/project1" RUNIDE_FILES="~/project1/main.go" ./gradlew runIde
        val projectPaths = providers.environmentVariable("RUNIDE_PROJECT_PATHS").orNull
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.map { file(it).absolutePath }
            ?: emptyList()

        val filePaths = providers.environmentVariable("RUNIDE_FILES").orNull
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.map { file(it).absolutePath }
            ?: emptyList()

        val allArgs = projectPaths + filePaths
        if (allArgs.isNotEmpty()) {
            args = allArgs
        }

        systemProperty("idea.is.internal", "true")
        systemProperty("idea.trust.all.projects", "true")
    }

    // Theme development workflow helper
    register<Exec>("dev") {
        group = "development"
        description = "Start theme development with auto-rebuild"
        commandLine("bash", "scripts/dev.sh")
    }
}

// Additional runIde tasks for testing in different IDEs
// Usage: ./gradlew runGoLand, ./gradlew runPyCharm, etc.
intellijPlatformTesting {
    runIde {
        register("runGoLand") {
            type = IntelliJPlatformType.GoLand
            version = "2025.3"
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf("-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark")
                }
            }
        }

        register("runPyCharm") {
            type = IntelliJPlatformType.PyCharm
            version = "2025.3"
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf("-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark")
                }
            }
        }

        register("runWebStorm") {
            type = IntelliJPlatformType.WebStorm
            version = "2025.3"
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf("-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark")
                }
            }
        }

        register("runRustRover") {
            type = IntelliJPlatformType.RustRover
            version = "2025.3"
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf("-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark")
                }
            }
        }

        register("runCLion") {
            type = IntelliJPlatformType.CLion
            version = "2025.3"
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf("-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark")
                }
            }
        }
    }
}
