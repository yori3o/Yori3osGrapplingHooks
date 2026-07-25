package com.yori3o.yo_hooks.common.config.client;


import com.yori3o.yo_hooks.common.config.categories.*;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public final class ConfigScreenFactory {

    public static Screen create(Screen parent) {
        //ModConfig config = ConfigManager.CONFIG.copy();

        OptionGroup commonGroup = CommonConfig.HANDLER.generateGui().categories().getFirst().groups().getLast();
        OptionGroup clientGroup = ClientConfig.HANDLER.generateGui().categories().getFirst().groups().getLast();
        OptionGroup serverGroup = ServerConfig.HANDLER.generateGui().categories().getFirst().groups().getLast();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Yori3o's Grappling Hooks"))

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Common"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("yacl3.config.yo_hooks:common_config.category.common.group.common"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("config.yo_hooks:common.desc"))
                                        .build())
                                .options(commonGroup.options())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Server"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("yacl3.config.yo_hooks:server_config.category.server.group.server"))

                                .options(serverGroup.options())

                                .option(Option.<String>createBuilder()
                                        .name(Component.translatable("config.yo_hooks:server_config.blocksBlacklist"))
                                        .description(
                                            OptionDescription.createBuilder()
                                                .text(Component.translatable("config.yo_hooks:server_config.blocksBlacklist.desc"))
                                                .build()
                                        )
                                        .binding(
                                            "",
                                            () -> String.join(",", ServerConfig.HANDLER.instance().blocksBlacklist),
                                            value -> {
                                                ServerConfig.HANDLER.instance().blocksBlacklist = Arrays.stream(value.split(","))
                                                    .map(String::trim)
                                                    .filter(s -> !s.isEmpty())
                                                    .collect(Collectors.toCollection(LinkedList::new));
                                            }
                                        )
                                        .controller(StringControllerBuilder::create)
                                        .build())

                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Client"))

                        .group(clientGroup)

                        .groupIf(PlatformUtil.isModLoaded("vivecraft"), VrConfig.HANDLER.generateGui().categories().getFirst().groups().getLast())
                        // НЕ трогать эту хуйню, .getFirst() для категории и .getLast() для группы

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Parameters"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("yacl3.config.yo_hooks:parameters.category.parameters.group.parameters"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("config.yo_hooks:parameters.desc"))
                                        .build())

                                .options(createHookOptions(HookRegistry.hooks, OverlapConfig.HANDLER.instance()))

                                .build())

                        .build())

                .save(() -> ClientConfigManager.save())
                .build()
                .generateScreen(parent);
    }

    public static List<Option<?>> createHookOptions(
        LinkedHashMap<String, HookDefinition> hooks,
        OverlapConfig config
    ) {
        List<Option<?>> options = new ArrayList<>();

        for (Map.Entry<String, HookDefinition> entry : hooks.entrySet()) {
            String id = entry.getKey();
            HookDefinition hook = entry.getValue();

            options.add(
                    Option.<Integer>createBuilder()
                            .name(Component.literal(Component.translatable("item.yo_hooks." + id + "_grappling_hook").getString() + Component.translatable("config.yo_hooks:range").getString()))
                            .description(
                                OptionDescription.createBuilder()
                                    .text(Component.translatable("config.yo_hooks:range.desc"))
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
                            .name(Component.literal(Component.translatable("item.yo_hooks." + id + "_grappling_hook").getString() + Component.translatable("config.yo_hooks:durability").getString()))
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
                                    .text(Component.translatable("config.yo_hooks:durability.desc"))
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
