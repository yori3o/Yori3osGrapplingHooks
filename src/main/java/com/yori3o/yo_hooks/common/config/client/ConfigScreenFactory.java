package com.yori3o.yo_hooks.common.config.client;


import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.config.ModConfig;
import com.yori3o.yo_hooks.common.config.categories.VrConfig;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;



public final class ConfigScreenFactory {

    public static Screen create(Screen parent) {
        ModConfig config = ConfigManager.CONFIG.copy();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Yori3o's Grappling Hooks"))

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Common"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("config.yo_hooks.common"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("config.yo_hooks.common.desc"))
                                        .build())

                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.climb_speed"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.climb_speed.desc"))
                                                .build()
                                        )
                                        .binding(
                                                0.115f,
                                                () -> config.climbSpeed,
                                                value -> config.climbSpeed = value
                                        )
                                        .controller(opt ->
                                            FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.01f).formatValue(value ->
                                                Component.literal(String.format(Locale.ROOT, "%.2f", value))
                                            )
                                        )
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.soft_hook"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.soft_hook.desc"))
                                                .build()
                                        )
                                        .binding(
                                                false,
                                                () -> config.softHook,
                                                value -> config.softHook = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.stiffness"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.stiffness.desc"))
                                                .build()
                                        )
                                        .binding(
                                                0.10f,
                                                () -> config.stiffness,
                                                value -> config.stiffness = value
                                        )
                                        .controller(opt ->
                                                FloatSliderControllerBuilder.create(opt).range(0.01f, 1f).step(0.01f).formatValue(value ->
                                                Component.literal(String.format(Locale.ROOT, "%.2f", value))
                                            ))
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.funny_mode"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.funny_mode.desc"))
                                                .build()
                                        )
                                        .binding(
                                                false,
                                                () -> config.funnyMode,
                                                value -> config.funnyMode = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Server"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("config.yo_hooks.server"))

                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.decrease_satiety"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.decrease_satiety.desc"))
                                                .build()
                                        )
                                        .binding(
                                                1.1f,
                                                () -> config.decreaseSatiety,
                                                value -> config.decreaseSatiety = value
                                        )
                                        .controller(opt ->
                                                FloatSliderControllerBuilder.create(opt).range(0f, 5f).step(0.1f))
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.breaking_fragile_blocks"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.breaking_fragile_blocks.desc"))
                                                .build()
                                        )
                                        .binding(
                                                true,
                                                () -> config.breakingFragileBlocks,
                                                value -> config.breakingFragileBlocks = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                //.option(Option.<List<String>>createBuilder()
                                        /*.name(Component.translatable("config.yo_hooks.blocks_blacklist"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.blocks_blacklist.desc"))
                                                .build()
                                        )
                                        .binding(
                                                new LinkedList<>(),
                                                () -> config.blocksBlacklist,
                                                value -> config.blocksBlacklist = value
                                        )
                                        .controller(CyclingListControllerBuilder::create)
                                        .build())*/
                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.blocks_blacklist"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.blocks_blacklist.desc"))
                                                .build()
                                        )
                                        .binding(
                                            "",
                                            () -> String.join(",", config.blocksBlacklist),
                                            value -> {
                                                config.blocksBlacklist = Arrays.stream(value.split(","))
                                                    .map(String::trim)
                                                    .filter(s -> !s.isEmpty())
                                                    .collect(Collectors.toCollection(LinkedList::new));
                                            }
                                        )
                                        .controller(StringControllerBuilder::create)
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.whitelist_mode"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.whitelist_mode.desc"))
                                                .build()
                                        )
                                        .binding(
                                                false,
                                                () -> config.whitelistMode,
                                                value -> config.whitelistMode = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Client"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("config.yo_hooks.client"))

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.hold_hook_tightly"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.hold_hook_tightly.desc"))
                                                .build()
                                        )
                                        .binding(
                                                false,
                                                () -> config.holdHookTightly,
                                                value -> config.holdHookTightly = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("config.yo_hooks.climb_with_mouse_wheel_scroll"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks.climb_with_mouse_wheel_scroll.desc"))
                                                .build()
                                        )
                                        .binding(
                                                false,
                                                () -> config.climbWithMouseWheelScroll,
                                                value -> config.climbWithMouseWheelScroll = value
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())

                                .build())

                        .groupIf(PlatformUtil.isModLoaded("vivecraft"), VrConfig.HANDLER.generateGui().categories().getFirst().groups().getLast())
                        // НЕ трогать эту хуйню, .getFirst() для категории и .getLast() для группы

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Parameters"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("config.yo_hooks.parameters"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("config.yo_hooks.parameters.desc"))
                                        .build())

                                .options(createHookOptions(HookRegistry.hooks, config))

                                .build())

                        .build())

                .save(() -> ClientConfigManager.save(config))
                .build()
                .generateScreen(parent);
    }

    public static List<Option<?>> createHookOptions(
        LinkedHashMap<String, HookDefinition> hooks,
        ModConfig config
    ) {
        List<Option<?>> options = new ArrayList<>();

        for (Map.Entry<String, HookDefinition> entry : hooks.entrySet()) {
            String id = entry.getKey();
            HookDefinition hook = entry.getValue();

            options.add(
                    Option.<Integer>createBuilder()
                            .name(Component.literal(Component.translatable("item.yo_hooks." + id + "_grappling_hook").getString() + Component.translatable("config.yo_hooks.range").getString()))
                            .description(
                                OptionDescription.createBuilder()
                                    .text(Component.translatable("config.yo_hooks.range.desc"))
                                    .build()
                            )
                            .binding(
                                    hook.getLength(),
                                    () -> config.rangeOverlap.getOrDefault(id, hook.length),
                                    value -> {
                                        if (value.intValue() == hook.length) {
                                            config.rangeOverlap.remove(id);
                                        } else {
                                            config.rangeOverlap.put(id, value.intValue());
                                        }
                                    }
                            )
                            .controller(opt ->
                                    IntegerSliderControllerBuilder.create(opt)
                                            .range(1, 99)
                                            .step(1))
                            .build()
            );

            options.add(
                    Option.<Integer>createBuilder()
                            .name(Component.literal(Component.translatable("item.yo_hooks." + id + "_grappling_hook").getString() + Component.translatable("config.yo_hooks.durability").getString()))
                            .binding(
                                    hook.getDurability(),
                                    () -> config.durabilityOverlap.getOrDefault(id, hook.durability),
                                    value -> {
                                        if (value.intValue() == hook.durability) {
                                            config.durabilityOverlap.remove(id);
                                        } else {
                                            config.durabilityOverlap.put(id, value.intValue());
                                        }
                                    }
                            )
                            .description(
                                OptionDescription.createBuilder()
                                    .text(Component.translatable("config.yo_hooks.durability.desc"))
                                    .build()
                            )
                            .controller(opt ->
                                    IntegerSliderControllerBuilder.create(opt)
                                            .range(-1, 960)
                                            .step(1))
                            .build()
            );
        }

        return options;

    }


}
