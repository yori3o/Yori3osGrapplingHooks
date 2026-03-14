package com.yori3o.yo_hooks.common.client.gui;


import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.config.ServerConfig;
import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.FileOpenUtil;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;



public class ConfigScreen extends Screen {


    private final Screen parent;

    private Component labelCommon, labelServer, labelNote;

    private float decreaseSatiety = DynamicConfigHandler.server().decreaseSatiety;
    private boolean breakingFragileBlocks = DynamicConfigHandler.server().breakingFragileBlocks;

    private boolean softHook = DynamicConfigHandler.common().softHook;
    private float stiffness = DynamicConfigHandler.common().stiffness;
    private float climbSpeed = DynamicConfigHandler.common().climbSpeed;
    private boolean funnyMode = DynamicConfigHandler.common().funnyMode;

    private Slider slider;

    private final String on;
    private final String off;
    
    
    public ConfigScreen(Screen parent) {
        super(Component.literal("Config"));
        this.parent = parent;
        
        on = Component.translatable("options.on").getString();
        off = Component.translatable("options.off").getString();
    }


    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        int colWidth = 200;
        int colSpacing = 20;
        int startX = this.width / 2 - colWidth - colSpacing / 2;
        int startY = this.height / 4;

        String on = ": " + this.on;
        String off = ": " + this.off;


        // =========================
        // Column 1: Common
        // =========================
        int x1 = startX;
        int y = startY;

        String on1 = (DynamicConfigHandler.common().funnyMode) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.funny_mode").append(on1), b -> toggleButton(b, 1)).bounds(x1, y, colWidth, 20).build());
        y += 25;
        String on2 = (DynamicConfigHandler.common().softHook) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.soft_hook").append(on2), b -> toggleButton(b, 2)).bounds(x1, y, colWidth, 20).build());
        y += 30;
        float value1 = DynamicConfigHandler.common().stiffness;
        slider = addRenderableWidget(new Slider(x1, y, colWidth, 20, 0.01, 1, value1, Component.translatable("settings.yo_hooks.stiffness").getString(), 1, this));
        slider.active = DynamicConfigHandler.common().softHook;
        y += 25;
        float value2 = DynamicConfigHandler.common().climbSpeed;
        addRenderableWidget(new Slider(x1, y, colWidth, 20, 0, 1, value2, Component.translatable("settings.yo_hooks.climb_speed").getString(), 2, this));


        // =========================
        // Column 2: Server
        // =========================
        int x2 = startX + colWidth + colSpacing;
        y = startY;

        float value3 = DynamicConfigHandler.server().decreaseSatiety;
        addRenderableWidget(new Slider(x2, y, colWidth, 20, 0, 5, value3, Component.translatable("settings.yo_hooks.decrease_satiety").getString(), 3, this));
        y += 25;
        String on3 = (DynamicConfigHandler.server().breakingFragileBlocks) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.breaking_fragile_blocks").append(on3), b -> toggleButton(b, 3)).bounds(x2, y, colWidth, 20).build());


        // =========================
        // Labels
        // =========================
        labelCommon = Component.literal("Common");
        labelServer = Component.literal("Server");
        labelNote = Component.translatable("settings.yo_hooks.file_note");


        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.open_file").append(" (common)"), b -> FileOpenUtil.revealConfigFile(new File(PlatformUtil.getConfigDir().resolve("yo_hooks_common.json").toUri()))).bounds(x1, this.height - 90, colWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.open_file").append(" (server)"), b -> FileOpenUtil.revealConfigFile(new File(PlatformUtil.getConfigDir().resolve("yo_hooks_server.json").toUri()))).bounds(x2, this.height - 90, colWidth, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.reset_settings"), b -> resetConfig()).bounds(x1, this.height - 65, colWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.save_and_close"), b -> this.onClose()).bounds(x2, this.height - 65, colWidth, 20).build());
    }


    private void toggleButton(Button button, int configValueNumber) {
        String text = button.getMessage().getString();
        boolean on = text.endsWith(this.on);
        button.setMessage(Component.literal(text.replace(on ? this.on : this.off, on ? this.off : this.on)));
        if (configValueNumber == 1) {
            funnyMode = !on;
        } else if (configValueNumber == 2) {
            softHook = !on;
            slider.active = !on;
        } else if (configValueNumber == 3) {
            breakingFragileBlocks = !on;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int colWidth = 200;
        int colSpacing = 20;
        int startX = this.width / 2 - colWidth - colSpacing / 2;

        super.render(graphics, mouseX, mouseY, delta);

        int labelY = this.height / 4 - 15;
        graphics.drawString(this.font, labelCommon, startX, labelY, 0xFFFFFFFF);
        graphics.drawString(this.font, labelServer, startX + colWidth + colSpacing, labelY, 0xFFFFFFFF);
        graphics.drawString(this.font, labelNote, startX, this.height - 110, 0xFFFFFFFF);

    }


    // ==========================
    // slider
    // ==========================
    private static class Slider extends AbstractSliderButton {
        private final double min, max;
        private final String label;
        private final int configValueNumber;
        private final ConfigScreen cs;
        private float sliderValue;

        public Slider(int x, int y, int width, int height, double min, double max, double value, String label, int configValueNumber, ConfigScreen cs) {
            super(x, y, width, height, Component.literal(""), (value - min) / (max - min));
            this.min = min;
            this.max = max;
            this.label = label;
            this.configValueNumber = configValueNumber;
            this.cs = cs;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(label + ": " + String.format("%.2f", min + this.value * (max - min))));
            
        }

        @Override
        protected void applyValue() {
            this.sliderValue = (float) (min + this.value * (max - min));
            if (configValueNumber == 1) {
                cs.stiffness = sliderValue;
            } else if (configValueNumber == 2) {
                cs.climbSpeed = sliderValue;
            } else if (configValueNumber == 3) {
                cs.decreaseSatiety = sliderValue;
            }
            
            updateMessage();
        }
    }


    @Override
    public void onClose() {
        
        saveConfig();

        Minecraft.getInstance().setScreen(parent);
    }

    private void resetConfig() {

        ServerConfig.Values scv = new ServerConfig.Values();
        CommonConfig.Values ccv = new CommonConfig.Values();
        
        decreaseSatiety = scv.decreaseSatiety;
        breakingFragileBlocks = scv.breakingFragileBlocks;
 
        softHook = ccv.softHook;
        stiffness = ccv.stiffness;
        climbSpeed = ccv.climbSpeed;
        funnyMode = ccv.funnyMode;

        saveConfig();

        init();
    }

    private void saveConfig() {
        
        CommonConfig.Values ccv = DynamicConfigHandler.common();
        ServerConfig.Values scv = DynamicConfigHandler.server();

        ccv.softHook = softHook;
        ccv.stiffness = stiffness;
        ccv.climbSpeed = climbSpeed;
        ccv.funnyMode = funnyMode;
        scv.decreaseSatiety = decreaseSatiety;
        scv.breakingFragileBlocks = breakingFragileBlocks;

        DynamicConfigHandler.cc.save();
        DynamicConfigHandler.sc.save();

        if (Minecraft.getInstance().isLocalServer()) {   

            PhysicVariables.updateCommonVariables(softHook, stiffness, climbSpeed);
            PhysicVariables.updateFunnyModeConfig(funnyMode);

            sendCommonConfigToAllPlayersInMultiplayer();
        }
    }

    private void sendCommonConfigToAllPlayersInMultiplayer() {
        Minecraft mc = Minecraft.getInstance();
        boolean isLanOpen = false;

        isLanOpen = mc.getSingleplayerServer().isPublished();

        if (isLanOpen) {
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                var players = server.getPlayerList().getPlayers();

                ServerPlayer host = server.getPlayerList().getPlayer(mc.player.getUUID());

                for (ServerPlayer p : players) {
                    if (host != null && p.getUUID().equals(host.getUUID())) continue;

                    ServerSender.sendCommonConfig(p, DynamicConfigHandler.cc.get());
                }
            }

        }

    }
}