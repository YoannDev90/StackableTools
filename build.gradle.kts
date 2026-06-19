import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val sysMcVersion: String = System.getProperty("mcVersion") ?: "1.21.4"
val mcVersion: String = (project.findProperty("mcVersion") as? String) ?: sysMcVersion
val is261 = mcVersion.startsWith("26")

plugins {
    id(
        if ((System.getProperty("mcVersion") ?: "1.21.4").startsWith("26"))
            "net.fabricmc.fabric-loom"
        else
            "net.fabricmc.fabric-loom-remap"
    )
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

base {
    archivesName = providers.gradleProperty("archives_base_name")
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("stackabletools") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

if (!is261) {
    fabricApi {
        configureDataGeneration {
            client = true
        }

        configureTests {
            createSourceSet = true
            modId = "stackabletools-test"
            enableGameTests = true
            enableClientGameTests = false
            eula = true
        }
    }
}

sourceSets {
    named("main") {
        java.srcDir("src/mc-$mcVersion/kotlin")
    }
    if (!is261) {
        named("gametest") {
            java.setSrcDirs(listOf("src/mc-$mcVersion/gametest/kotlin"))
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")

    if (is261) {
        implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
        implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
        implementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
    } else {
        @Suppress("UnstableApiUsage")
        project.dependencies.add("mappings", "net.fabricmc:yarn:${providers.gradleProperty("yarn_mappings").get()}:v2")
        @Suppress("UnstableApiUsage")
        project.dependencies.add("modImplementation", "net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
        @Suppress("UnstableApiUsage")
        project.dependencies.add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
        @Suppress("UnstableApiUsage")
        project.dependencies.add("modImplementation", "net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")
    }

    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.json:json:20240303")
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

if (is261) {
    tasks.withType<JavaCompile>().configureEach {
        options.release = 25
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
} else {
    tasks.withType<JavaCompile>().configureEach {
        options.release = 21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.jar {
    inputs.property("archivesName", base.archivesName)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .filter { it.name.contains("toml4j") || it.name.contains("json") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {

    }
}

if (!is261) {
    val enableGameTests = project.hasProperty("enableGameTests") && project.property("enableGameTests") == "true"
    tasks.matching { it.name == "runGameTest" }.configureEach {
        onlyIf { enableGameTests }
    }
}
