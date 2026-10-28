plugins {
    id("net.neoforged.moddev") version "2.0.147"
    id("neoforge-mutex")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

sourceSets.main.get().resources.srcDir(rootProject.file(
    if (sc.current.parsed >= "26.1") "src/modern/resources" else "src/legacy/resources"
))
sourceSets.main.get().resources.srcDir(rootProject.file(
    if (sc.current.parsed >= "26.3") "src/26.3/resources" else "src/pre26.3/resources"
))

sourceSets.main.get().resources.srcDir(rootProject.file(
    if (sc.current.parsed >= "26.2") "src/26.2-plus/resources" else "src/pre26.2/resources"
))

if (sc.current.parsed < "26.1") sourceSets.main.get().java.srcDir(rootProject.file("src/legacy/java"))
if (sc.current.parsed >= "26.1" && sc.current.parsed < "26.3") {
    sourceSets.main.get().resources.srcDir(rootProject.file("src/26.1-26.2/resources"))
}

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://maven.ryanhcode.dev/releases", "Sable", "dev.ryanhcode.sable", "dev.ryanhcode.sable-companion")
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    if (sc.current.parsed < "26.1") compileOnly("dev.ryanhcode.sable:sable-common-1.21.1:2.0.6")
    implementation("maven.modrinth:yacl:${property("deps.yacl")}")
    compileOnly("maven.modrinth:vivecraft:${property("deps.vivecraft")}")
}

neoForge {
    version = property("deps.neo_loader") as String

    mods {
        register("yo_hooks") {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = rootProject.file("run/${project.name}")
            client()
        }

        register("server") {
            gameDirectory = rootProject.file("run/${project.name}")
            server()
        }
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, value: String) {
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", sc.properties["mod.id"])
            register("name", sc.properties["mod.name"])
            register("version", sc.properties["mod.version"])
            register("minecraft", sc.properties["mod.mc_compat"])
            register("java_version", requiredJava.majorVersion)
        }

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        val clientMixins = mutableListOf("MultiPlayerGameModeMixin", "MouseMixin", "HeldItemMixin")
        if (sc.current.parsed >= "26.1") clientMixins.addAll(listOf("HookActivePropertyMixin", "AvatarRendererMixin"))
        val refmap = ""
        filesMatching("*.mixins.json") {
            expand("java" to mixinJava, "client_mixins" to clientMixins.joinToString(", ") { "\"$it\"" }, "refmap" to refmap)
        }

        exclude("fabric.mod.json", "*.ct", "*.classtweaker", "*.accesswidener")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    // Includes the license file in the built mod
    withType<Jar> {
        includeEmptyDirs = false
        val name = project.property("mod.id")
        inputs.property("mod_id", name)
        from("../../LICENSE") { rename { "$it-$name" } }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}

