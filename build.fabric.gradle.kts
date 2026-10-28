plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

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
    strictMaven("https://maven.quiltmc.org/repository/release", "Quilt Parsers", "org.quiltmc.parsers")
    strictMaven("https://maven.ryanhcode.dev/releases", "Sable", "dev.ryanhcode.sable", "dev.ryanhcode.sable-companion")
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    if (sc.current.parsed < "26.1") compileOnly("dev.ryanhcode.sable:sable-common-1.21.1:2.0.6")
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    modImplementation("maven.modrinth:yacl:${property("deps.yacl")}")
    if (sc.current.parsed < "26.1") {
        // Legacy Loom strips nested-mod declarations from remapped development jars.
        // YACL already bundles these libraries for installed mods.
        runtimeOnly("org.quiltmc.parsers:gson:0.2.1")
        runtimeOnly("com.twelvemonkeys.imageio:imageio-webp:3.12.0")
    }
    modCompileOnly("maven.modrinth:modmenu:${property("deps.modmenu")}")
    modLocalRuntime("maven.modrinth:modmenu:${property("deps.modmenu")}")
    modCompileOnly("maven.modrinth:vivecraft:${property("deps.vivecraft")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection

    if (sc.current.parsed < "26.1") {
        mixin {
            useLegacyMixinAp = true
            defaultRefmapName = "yo_hooks.refmap.json"
        }
    }

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run/${project.name}")
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
            register("loader", Regex("\\d+\\.\\d+").find(sc.properties.get<String>("deps.fabric_loader"))!!.value)
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        val clientMixins = mutableListOf("MultiPlayerGameModeMixin", "MouseMixin", "HeldItemMixin")
        if (sc.current.parsed >= "26.1") clientMixins.addAll(listOf("HookActivePropertyMixin", "AvatarRendererMixin"))
        val refmap = if (sc.current.parsed < "26.1") "\"refmap\": \"yo_hooks.refmap.json\",\n" else ""
        filesMatching("*.mixins.json") {
            expand("java" to mixinJava, "client_mixins" to clientMixins.joinToString(", ") { "\"$it\"" }, "refmap" to refmap)
        }

        exclude("META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg")
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
        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}
