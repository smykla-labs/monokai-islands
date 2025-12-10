plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.3.0"
    id("dev.detekt") version "2.0.0-alpha.1"
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
}

tasks {
    register("generateThemes", Exec::class) {
        commandLine("python3", "scripts/generate-themes.py")
    }

    buildPlugin {
        dependsOn("generateThemes")
    }
}
