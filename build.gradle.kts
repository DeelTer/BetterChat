plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    compileOnly("org.joml:joml:1.10.5")

    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.apache.commons:commons-text:1.12.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        relocate("org.bstats", "${project.group}.shaded.bstats")
        relocate("com.github.benmanes.caffeine", "${project.group}.shaded.caffeine")
        minimize()
    }
    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("26.1.2")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}