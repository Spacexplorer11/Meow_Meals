import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.3.0"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

// These lines make the mod filename `{mod.id}-${mod.version}+${sc.current.version}.jar`
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String
group = property("maven_group") as String

loom {
	splitEnvironmentSourceSets()

	mods {
		create("meowmeals") {
			sourceSet(sourceSets.getByName("main"))
			sourceSet(sourceSets.getByName("client"))
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${sc.current.version}")

	implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	implementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric_kotlin")}")
}

tasks.named<ProcessResources>("processResources") {
	inputs.property("minecraft_version", sc.current.version)
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand(
			mapOf(
				"minecraft_version" to sc.current.version,
				"version" to version
			)
		)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.named<Jar>("jar") {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${base.archivesName}" }
	}
}

publishMods {
	file = tasks.jar.flatMap { it.archiveFile }
	additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })

	type = STABLE

	displayName = "Meow Meals v${property("mod.version")} for mc ${sc.current.version}"
	version = "${property("mod.version")}+${sc.current.version}"

	changelog = provider {
		rootProject.file("CHANGELOG.md").readText()
	}

	modLoaders.add("fabric")
	dryRun = false

	modrinth {
		projectId = "sVLmG6J3"
		accessToken = providers.environmentVariable("MODRINTH_API_KEY")

		minecraftVersions.add(sc.current.version)

		requires("fabric-api")
		requires("fabric-language-kotlin")
	}
}