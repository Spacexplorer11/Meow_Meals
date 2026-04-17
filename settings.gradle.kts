pluginManagement {
	val isCI = System.getenv("CI") != null
	repositories {
		gradlePluginPortal()
		if (!isCI) {
			maven("http://raspberrypi.local:8080/Fabric") {
				name = "Fabric"
				isAllowInsecureProtocol = true
			}
			maven("http://raspberrypi.local:8080/Maven-Central") {
				name = "MavenCentral"
				isAllowInsecureProtocol = true
			}
			maven("http://raspberrypi.local:8080/REI") {
				name = "piREI"
				isAllowInsecureProtocol = true
			}
		} else {
			mavenCentral()
			maven("https://maven.fabricmc.net") {
				name = "Fabric"

			}
		}
		maven("https://maven.kikugie.dev/snapshots") {
			name = "KikuGie Snapshots"
		}
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9"
}

dependencyResolutionManagement {
	val isCI = System.getenv("CI") != null
	repositories {
		maven("https://libraries.minecraft.net") {
			name = "Mojang"
			content {
				includeGroup("org.lwjgl")
				includeGroup("net.minecraft")
				includeGroup("com.mojang")
			}
		}
		if (!isCI) {
			maven("http://raspberrypi.local:8080/Fabric") {
				name = "piFabric"
				isAllowInsecureProtocol = true
				content {
					includeGroupByRegex("net\\.fabricmc.*")
				}
			}
			maven("http://raspberrypi.local:8080/REI") {
				name = "piREI"
				isAllowInsecureProtocol = true
				content {
					includeGroupByRegex("me\\.shedaniel.*")
					includeGroupByRegex("dev\\.architectury.*")
				}
			}
			maven("http://raspberrypi.local:8080/Maven-Central") {
				name = "piMavenCentral"
				isAllowInsecureProtocol = true
			}
		}
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		maven("https://maven.shedaniel.me")
		mavenCentral()

		maven(".gradle/loom-cache/remapped_mods") {
			name = "LoomRemappedMods"
			content {
				includeGroupByRegex("remapped.*")
			}
		}
		maven(".gradle/loom-cache/minecraftMaven") {
			name = "LoomMinecraftStuff"
			content {
				includeGroupByRegex("net.minecraft.*")
			}
		}
	}
	repositoriesMode = RepositoriesMode.PREFER_SETTINGS
}

stonecutter {
	centralScript = "build.gradle.kts"

	// Configuration goes here
	create(rootProject) {
		versions(
			"1.21",
			"1.21.1",
			"1.21.2",
			"1.21.3",
			"1.21.4",
			"1.21.5",
			"1.21.6",
			"1.21.7",
			"1.21.8",
			"1.21.9",
			"1.21.10",
			"1.21.11"
		)
		vcsVersion = "1.21"
	}
}