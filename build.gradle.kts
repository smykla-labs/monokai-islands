plugins {
    id("java")
    id("dev.detekt") version "2.0.0-alpha.1"
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
    id("org.jetbrains.intellij.platform") version "2.10.5"
    id("org.jmailen.kotlinter") version "5.2.0"
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
}

tasks {
    register("generateThemes", Exec::class) {
        commandLine("python3", "scripts/generate-themes.py")
    }

    buildPlugin {
        dependsOn("generateThemes")
    }

    // Skip buildSearchableOptions to avoid "Only one instance can run" error
    buildSearchableOptions {
        enabled = false
    }

    runIde {
        // Auto-open project from RUNIDE_PROJECT_PATH env var (optional)
        providers.environmentVariable("RUNIDE_PROJECT_PATH").orNull?.let { projectPath ->
            args = listOf(project.file(projectPath).absolutePath)
        }

        // Set Monokai Islands Dark as default theme
        systemProperty("idea.is.internal", "true")
        systemProperty("idea.trust.all.projects", "true")
        jvmArgs = listOf(
            "-Didea.theme.id=com.github.smykla-labs.monokai-islands-dark"
        )
    }
}
