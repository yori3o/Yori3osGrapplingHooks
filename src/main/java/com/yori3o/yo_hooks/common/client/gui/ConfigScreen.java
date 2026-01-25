package com.yori3o.yo_hooks.common.client.gui;


import java.io.File;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.utils.FileOpenUtil;
import com.yori3o.yo_hooks.common.utils.PhysicVariables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.fabricmc.loader.api.FabricLoader;



public class ConfigScreen extends Screen {

    private final Screen parent;

    private Component labelCommon, labelServer;

    private float decreaseSatiety = DynamicConfigHandler.decreaseSatiety;
    private boolean breakingFragileBlocks = DynamicConfigHandler.breakingFragileBlocks;

    private boolean softHook = DynamicConfigHandler.softHook;
    private float stiffness = DynamicConfigHandler.stiffness;
    private float climbSpeed = DynamicConfigHandler.climbSpeed;
    private boolean funnyMode = DynamicConfigHandler.funnyMode;

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

        String on1 = (DynamicConfigHandler.funnyMode) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.funny_mode").append(on1), b -> toggleButton(b, 1)).bounds(x1, y, colWidth, 20).build());
        y += 25;
        String on2 = (DynamicConfigHandler.softHook) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.soft_hook").append(on2), b -> toggleButton(b, 2)).bounds(x1, y, colWidth, 20).build());
        y += 30;
        float value1 = DynamicConfigHandler.stiffness;
        slider = addRenderableWidget(new Slider(x1, y, colWidth, 20, 0.01, 1, value1, Component.translatable("settings.yo_hooks.stiffness").getString(), 1, this));
        slider.active = DynamicConfigHandler.softHook;
        y += 25;
        float value2 = DynamicConfigHandler.climbSpeed;
        addRenderableWidget(new Slider(x1, y, colWidth, 20, 0, 1, value2, Component.translatable("settings.yo_hooks.climb_speed").getString(), 2, this));


        // =========================
        // Column 2: Server
        // =========================
        int x2 = startX + colWidth + colSpacing;
        y = startY;

        float value3 = DynamicConfigHandler.decreaseSatiety;
        addRenderableWidget(new Slider(x2, y, colWidth, 20, 0, 5, value3, Component.translatable("settings.yo_hooks.decrease_satiety").getString(), 3, this));
        y += 25;
        String on3 = (DynamicConfigHandler.breakingFragileBlocks) ? on : off;
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.breaking_fragile_blocks").append(on3), b -> toggleButton(b, 3)).bounds(x2, y, colWidth, 20).build());


        // =========================
        // Labels (подписи)
        // =========================
        labelCommon = Component.literal("Common");
        labelServer = Component.literal("Server");


        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.open_file"), b -> FileOpenUtil.revealConfigFile(new File(FabricLoader.getInstance().getConfigDir().resolve("yo_hooks_common.json").toUri()))).bounds(x1, this.height - 90, colWidth, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.open_file"), b -> FileOpenUtil.revealConfigFile(new File(FabricLoader.getInstance().getConfigDir().resolve("yo_hooks_server.json").toUri()))).bounds(x2, this.height - 90, colWidth, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("settings.yo_hooks.reset_settings"), b -> ResetConfig()).bounds(x1, this.height - 65, colWidth * 2 + 20, 20).build());
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
        //this.renderMenuBackground(graphics, mouseX, mouseY, delta);
        int colWidth = 200;
        int colSpacing = 20;
        int startX = this.width / 2 - colWidth - colSpacing / 2;
        //int startY = this.height / 4;

        super.render(graphics, mouseX, mouseY, delta);

        int labelY = this.height / 4 - 15;
        graphics.drawString(this.font, labelCommon, startX, labelY, 0xFFFFFF);
        graphics.drawString(this.font, labelServer, startX + colWidth + colSpacing, labelY, 0xFFFFFF);

        //if (!softHook) graphics.fill(startX, startY + 55, startX + 200, startY + 55 + 20, 0x885C5C5C);
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
        
        SaveConfig();

        Minecraft.getInstance().setScreen(parent);
    }

    private void ResetConfig() {
        
        decreaseSatiety = 1.1f;
        breakingFragileBlocks = true;

        softHook = false;
        stiffness = 0.1f;
        climbSpeed = 0.125f;
        funnyMode = false;

        SaveConfig();

        init();
    }

    private void SaveConfig() {
        
        DynamicConfigHandler.setBreakingFragileBlocks(breakingFragileBlocks);
        DynamicConfigHandler.setClimbSpeed(climbSpeed);
        DynamicConfigHandler.setDecreaseSatiety(decreaseSatiety);
        DynamicConfigHandler.setFunnyMode(funnyMode);
        DynamicConfigHandler.setSoftHook(softHook);
        DynamicConfigHandler.setStiffness(stiffness);

        if (Minecraft.getInstance().isLocalServer()) {
            PhysicVariables.UpdateCommonVariables(softHook, stiffness, climbSpeed);
            PhysicVariables.UpdateFunnyModeConfig(funnyMode);
        }
    }
}