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
        goland("2025.3")
    }
}

intellijPlatform {
    // Theme-only plugin has no searchable options (settings UI)
    buildSearchableOptions = false

    pluginConfiguration {
        id = "com.github.smykla-labs.monokai-islands"
        name = "Monokai Islands Theme"
        version = project.property("version") as String

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
            recommended()
        }
    }
}

detekt {
    toolVersion = "1.23.8"
    config.setFrom("$projectDir/detekt.yml")
    buildUponDefaultConfig = true
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

    buildPlugin {
        dependsOn("generateThemes")
    }

    runIde {
        // Auto-open project from RUNIDE_PROJECT_PATH env var (optional)
        providers.environmentVariable("RUNIDE_PROJECT_PATH").orNull?.let { projectPath ->
            args = listOf(project.file(projectPath).absolutePath)
        }

        systemProperty("idea.is.internal", "true")
        systemProperty("idea.trust.all.projects", "true")

        // Set Monokai Islands Dark as default theme
        jvmArgs = listOf(
            "-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark"
        )
    }
}
