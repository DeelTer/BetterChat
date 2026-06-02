plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
    id("maven-publish")
}

group = "ru.deelter"
version = "1.4.0"
description = "Advanced chat system with bubbles, effects, and cross-server messaging"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.velocitypowered.com/snapshots/") {
        content { includeGroup("com.velocitypowered") }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.2.0")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.apache.commons:commons-text:1.12.0")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")

    compileOnly("org.xerial:sqlite-jdbc:3.46.1.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        relocate("org.bstats", "${project.group}.shaded.bstats")
        relocate("com.github.benmanes.caffeine", "${project.group}.shaded.caffeine")
//        relocate("org.apache.commons.text", "${project.group}.shaded.commons.text")
//        relocate("org.apache.commons.lang3", "${project.group}.shaded.commons.lang3")

        exclude("META-INF/maven/**")
        exclude("META-INF/versions/**")
        exclude("META-INF/services/**")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        exclude("**/module-info.class")

        exclude("**/*.dll")
        exclude("**/*.dylib")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.3")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }
}

publishing {
    publications {
        create<MavenPublication>("BetterChat") {
            from(components["java"])
            artifact(tasks.shadowJar)
        }
    }
}