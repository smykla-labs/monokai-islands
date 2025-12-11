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
