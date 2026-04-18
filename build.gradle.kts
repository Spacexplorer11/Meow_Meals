import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.16-SNAPSHOT"
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

	runConfigs.all {
		runDir = "../../run"
		isIdeConfigGenerated = true
	}

}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${sc.current.version}")
	mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")

	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric_kotlin")}")

	modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${property("deps.rei")}")
	modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${property("deps.rei")}")
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_21
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("sourcesJar") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar>("jar") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from("LICENSE") {
		rename { "${it}_${base.archivesName}" }
	}
}

tasks.named("processResources") {
	dependsOn(":${stonecutter.current.project}:stonecutterGenerate")
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from(tasks.remapJar.map { it.archiveFile })
	into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
	dependsOn("build")
}

tasks.named<ProcessResources>("processResources") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	inputs.property("minecraft_version", stonecutter.current.version)
	inputs.property("version", version)
	filesMatching("fabric.mod.json") {
		expand(
			mapOf(
				"minecraft_version" to stonecutter.current.version,
				"version" to version,
				"rei_version" to project.property("deps.rei"),
				"fabric_api_version" to project.property("deps.fabric_api"),
			)
		)
	}
}

publishMods {
	file = tasks.remapJar.map { it.archiveFile.get() }
	additionalFiles.from(tasks.named<Jar>("sourcesJar").map { it.archiveFile.get() })
	type = STABLE
	displayName = "v${property("mod.version")} for mc ${stonecutter.current.version}"
	version = "${property("mod.version")}+${stonecutter.current.version}"
	changelog = provider { rootProject.file("CHANGELOG.md").readText() }
	modLoaders.add("fabric")
	dryRun = false

	modrinth {
		projectId = "sVLmG6J3"
		accessToken = providers.environmentVariable("MODRINTH_API_KEY")
		minecraftVersions.add(stonecutter.current.version)
		requires("fabric-api")
		requires("fabric-language-kotlin")
		optional("rei")
	}
}