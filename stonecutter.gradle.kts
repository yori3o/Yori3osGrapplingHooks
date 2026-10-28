plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3-fabric"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Makes version- and loader-specific properties apply from `stoncutter.properties.toml`
    properties {
        tags(version, loader)
    }

    // Adds constants to Stonecutter comments (i.e. for `//? if fabric {...`)
    constants {
        match(loader, "fabric", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"

    replacements {
        string(current.parsed >= "26.1") {
            replace("FMLLoader.getDist()", "FMLLoader.getCurrent().getDist()")
            replace("PacketDistributor.sendToServer(payload)", "net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload)")
        }

        string(current.parsed < "26.3") {
            replace("InputConstants.Type.KEYBOARD", "InputConstants.Type.KEYSYM")
        }
        string(current.parsed < "26.1") {
            replace("net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents", "net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents")
            replace("net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab", "net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup")
            replace("CreativeModeTabEvents", "ItemGroupEvents")
            replace("FabricCreativeModeTab", "FabricItemGroup")
            replace("modifyOutputEvent", "modifyEntriesEvent")
            replace("net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper", "net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper")
            replace("KeyMappingHelper", "KeyBindingHelper")
            replace("registerKeyMapping", "registerKeyBinding")
            replace("key.identifier()", "key.location()")
            replace("Inventory;setSelectedSlot(I)V", "Inventory;swapPaint(D)V")
            replace("PayloadTypeRegistry.clientboundPlay()", "PayloadTypeRegistry.playS2C()")
            replace("PayloadTypeRegistry.serverboundPlay()", "PayloadTypeRegistry.playC2S()")
        }

        string(current.parsed < "26.3") {
            replace("net.minecraft.client.player.FirstPersonHandsAndItems", "net.minecraft.client.renderer.ItemInHandRenderer")
            replace("net/minecraft/client/player/FirstPersonHandsAndItems", "net/minecraft/client/renderer/ItemInHandRenderer")
            replace("FirstPersonHandsAndItems", "ItemInHandRenderer")
        }

        string(current.parsed < "26.1") {
            replace("Identifier", "ResourceLocation")
        }

    }
}

// Compile, package and collect every supported loader/version combination.
val supportedTargets = listOf("1.21.1", "26.1", "26.2", "26.3")
    .flatMap { version -> listOf("fabric", "neoforge").map { loader -> "$version-$loader" } }
tasks.register("buildAll") {
    group = "build"
    description = "Builds and collects all eight Minecraft/loader variants"
    dependsOn(supportedTargets.map { ":$it:buildAndCollect" })
}
