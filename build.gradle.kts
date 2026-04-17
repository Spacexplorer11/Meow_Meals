import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.16-SNAPSHOT"
	maven - publish
	id("org.jetbrains.kotlin.jvm") version "2.3.0"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

// These lines make the mod filename `{mod.id}-${mod.version}+${sc.current.version}.jar`
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String
group = property("maven_group") as String

loom {
	splitEnvironmentSourceSets()

	mods {
		"meowmeals" {
			sourceSet(sourceSets.main)
			sourceSet(sourceSets.client)
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

tasks.withType(KotlinCompile).configureEach {
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

tasks.named<Jar>("jar") {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties.archivesName}"}
	}
}

tasks.named("processResources") {
	dependsOn(":${stonecutter.current.project}:stonecutterGenerate")
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(jar.map { it.archiveFile })
	into(rootProject.layout.buildDirectory.file("libs/${property("mod.version")}"))
	dependsOn("build")
}

tasks.named<ProcessResources>("processResources") {
	inputs.property("minecraft_version", stonecutter.current.version)
	inputs.property("version", version)
	filesMatching("fabric.mod.json") {
		expand(
			mapOf(
				"minecraft_version" to stonecutter.current.version,
				"version" to version
			)
		)
	}
}

fletchingTable {
	j52j.register("main") {
		extension("json", "data/meowmeals/recipes/**/*.json5")
	}
}