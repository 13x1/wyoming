plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    maven("https://maven.wispforest.io")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    // owo
    modImplementation("io.wispforest:owo-lib:${property("owo_version")}")
    annotationProcessor("io.wispforest:owo-lib:${property("owo_version")}")

    // wynntils
    modImplementation("com.github.llamalad7.mixinextras:mixinextras-fabric:${property("mixinextras_version")}")
    modImplementation(files("libs/wynntils-${property("wynntils_version")}-fabric.jar"))
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/sources/")
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
        }
    }
    jar {
        from("LICENSE")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

java {
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}
