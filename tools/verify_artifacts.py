"""Audit the packaged matrix using only the Python standard library."""
from pathlib import Path
import json
import struct
import tomllib
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
PROPERTIES = tomllib.loads((ROOT / "stonecutter.properties.toml").read_text(encoding="utf-8"))
MOD_VERSION = PROPERTIES["mod"]["version"]


def verify(version: str, loader: str) -> None:
    path = ROOT / "build/libs" / MOD_VERSION / f"yo_hooks-{loader}-{MOD_VERSION}+{version}.jar"
    assert path.is_file(), f"Missing artifact: {path}"
    with ZipFile(path) as jar:
        names = set(jar.namelist())
        assert len(names) == len(jar.namelist()), f"Duplicate ZIP entries: {path.name}"
        resources = {name: json.loads(jar.read(name)) for name in names if name.endswith(".json")}
        fragile = resources["data/yo_hooks/tags/block/fragile.json"]["values"]
        assert ("minecraft:sulfur_spike" in fragile) == (version in {"26.2", "26.3"}), f"Wrong sulfur spike compatibility: {path.name}"
        mixins = resources["yo_hooks-common.mixins.json"]
        expected_java = 21 if version == "1.21.1" else 25
        assert mixins["compatibilityLevel"] == f"JAVA_{expected_java}"
        for mixin in mixins["mixins"] + mixins["client"]:
            name = mixins["package"].replace(".", "/") + "/" + mixin + ".class"
            assert name in names, f"Missing mixin: {name}"
        own_classes = [n for n in names if n.startswith("com/yori3o/yo_hooks/") and n.endswith(".class")]
        for name in own_classes:
            major = struct.unpack(">H", jar.read(name)[6:8])[0]
            assert major == expected_java + 44, f"Wrong Java version: {name}: {major}"
        other = "neoforge" if loader == "fabric" else "fabric"
        assert not any(n.startswith(f"com/yori3o/yo_hooks/{other}/") and n.endswith(".class") for n in names)
        assert not any("com/example/" in n or "template" in n for n in names)
        assert not any("HookRegression" in n or "yo_hooks_regression" in n or "SableTransformTests" in n for n in names), "Test harness leaked into release jar"
        if loader == "fabric":
            metadata = resources["fabric.mod.json"]
            assert metadata["id"] == "yo_hooks" and metadata["version"] == MOD_VERSION
            assert metadata["depends"]["minecraft"] == PROPERTIES["fabric"][version]["mod"]["mc_compat"]
            for entries in metadata["entrypoints"].values():
                for entry in entries:
                    assert entry.replace(".", "/") + ".class" in names
            assert "META-INF/neoforge.mods.toml" not in names
            if version == "1.21.1":
                assert mixins["refmap"] == "yo_hooks.refmap.json"
                assert resources["yo_hooks.refmap.json"]["mappings"], "Empty legacy refmap"
        else:
            metadata = tomllib.loads(jar.read("META-INF/neoforge.mods.toml").decode())
            assert metadata["mods"][0]["modId"] == "yo_hooks"
            assert metadata["mods"][0]["version"] == MOD_VERSION
            assert "fabric.mod.json" not in names
        for material in ["copper", "iron", "gold", "diamond", "netherite"]:
            recipe = resources[f"data/yo_hooks/recipe/{material}_grappling_hook.json"]
            ingredient = recipe.get("key", {}).get("B")
            if ingredient is not None:
                expected = {"item": "minecraft:chain"} if version == "1.21.1" else "minecraft:iron_chain"
                assert ingredient == expected, f"Wrong chain ingredient: {path.name}"
            item = f"assets/yo_hooks/items/{material}_grappling_hook.json"
            assert (item in names) == (version != "1.21.1")
            if version == "1.21.1":
                model = resources[f"assets/yo_hooks/models/item/{material}_grappling_hook.json"]
                assert model["overrides"][0]["predicate"] == {"yo_hooks:hook_active": 1}
        loot = resources["data/yo_hooks/loot_table/minecraft/end_city_treasure.json"]["pools"][0]["entries"][0]
        assert ("modifier" in loot) == (version == "26.3")
        assert ("functions" in loot) == (version != "26.3")
        assert ("AvatarRendererMixin" in mixins["client"]) == (version != "1.21.1")
    print(f"OK {version:6} {loader:8} {len(own_classes)} classes, {len(resources)} JSON resources")


if __name__ == "__main__":
    for mc in ["1.21.1", "26.1", "26.2", "26.3"]:
        for mod_loader in ["fabric", "neoforge"]:
            verify(mc, mod_loader)
