plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.0"
	id("fabric-loom") version "1.3-SNAPSHOT"
	java
}

val archives_base_name: String by project
base.archivesName.set(archives_base_name)

val javaVersion = 17

repositories {
	maven("https://notnite.github.io/blockbuild/mvn/") {
        content { includeGroup("pm.n2") }
    }

    maven("https://maven.svc.adryd.com/releases/") {
        content { includeGroup("com.adryd") }
    }

    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        content { includeGroup("me.djtheredstoner") }
    }

    maven("https://maven.terraformersmc.com/releases/") {
        content { includeGroup("com.terraformersmc") }
    }
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    modApi(include("com.adryd:cauldron:${property("cauldron_version")}")!!)
    modApi("pm.n2:hajlib:${property("hajlib_version")}")

    modCompileOnly("com.terraformersmc:modmenu:${property("modmenu_version")}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${property("devauth_version")}")
}

tasks {
	compileKotlin {
		kotlinOptions {
			jvmTarget = javaVersion.toString()
		}
	}

    compileJava {
        this.options.encoding = "UTF-8"
        this.options.release = javaVersion
    }

	processResources {
		filteringCharset = "UTF-8"
		inputs.property("version", project.version)
		filesMatching("fabric.mod.json") {
			expand(mapOf("version" to project.version))
		}
	}

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName}" }
        }
    }
}

java {
	withSourcesJar()
}

loom {
	accessWidenerPath = file("src/main/resources/tangerine.accesswidener")
}
