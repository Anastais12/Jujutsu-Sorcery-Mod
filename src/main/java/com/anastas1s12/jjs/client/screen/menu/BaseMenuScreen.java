package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.ability.Grade;
import com.anastas1s12.jjs.capability.ICursedEnergy;
import com.anastas1s12.jjs.client.ClientCEData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMenuScreen extends Screen {

    private static final ResourceLocation ABILITIES_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/abilities_screen.png");
    private static final ResourceLocation CURSED_ENERGY_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/cursed_energy_screen.png");
    private static final ResourceLocation TECHNIQUE_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/technique_screen.png");
    private static final ResourceLocation MASTERY_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/mastery_screen.png");
    private static final ResourceLocation DOMAIN_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/domain_screen.png");
    private static final ResourceLocation INVENTORY_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/inventory_screen.png");
    private static final ResourceLocation STATS_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/stats_screen.png");
    private static final ResourceLocation SETTINGS_BG = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/screen/settings_screen.png");
    public static final ResourceLocation TAB_ICONS = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/tab_icons.png");
    public static final ResourceLocation STAT_ICONS = ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/stat_icons.png");

    protected static final int BG_WIDTH = 400;
    protected static final int BG_HEIGHT = 240;
    protected static final int LEFT_PANEL_WIDTH = 130;
    protected static final int CONTENT_TOP = 35;
    protected static final int CONTENT_BOTTOM = 220;

    public enum Tab {
        ABILITIES(0, "Abilities", 0, 0),
        CURSED_ENERGY(1, "Cursed Energy", 20, 0),
        TECHNIQUE(2, "Technique", 40, 0),
        MASTERY(3, "Mastery", 60, 0),
        DOMAIN(4, "Domain", 80, 0),
        INVENTORY(5, "Tools", 100, 0),
        STATS(6, "Stats", 120, 0),
        SETTINGS(7, "Settings", 140, 0);

        public final int index;
        public final String name;
        public final int iconU;
        public final int iconV;

        Tab(int index, String name, int iconU, int iconV) {
            this.index = index;
            this.name = name;
            this.iconU = iconU;
            this.iconV = iconV;
        }
    }

    protected final Tab activeTab;
    protected int centerX;
    protected int centerY;
    protected int panelLeft;
    protected int panelTop;

    protected final List<TabArea> tabAreas = new ArrayList<>();
    protected String tooltipText = null;
    protected int tooltipX = 0;
    protected int tooltipY = 0;

    protected BaseMenuScreen(Tab activeTab) {
        super(Component.literal("Jujutsu Sorcery Menu"));
        this.activeTab = activeTab;
    }

    @Override
    protected void init() {
        super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.panelLeft = centerX - BG_WIDTH / 2;
        this.panelTop = centerY - BG_HEIGHT / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackgroundTexture(graphics);
        renderTabs(graphics, mouseX, mouseY);
        renderLeftPanel(graphics, mouseX, mouseY, partialTick);
        renderContent(graphics, mouseX, mouseY, partialTick);

        if (tooltipText != null) {
            graphics.renderTooltip(this.font, Component.literal(tooltipText), tooltipX, tooltipY);
            tooltipText = null;
        }
    }

    protected void renderBackgroundTexture(GuiGraphics graphics) {
        ResourceLocation bgTexture = getBackgroundForTab(activeTab);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();

        graphics.blit(bgTexture, 0, 0, 0, 0, this.width, this.height, 1024, 512);
        graphics.fill(0, 0, this.width, this.height, 0x99000000); // dark overlay

        RenderSystem.disableBlend();
    }

    private ResourceLocation getBackgroundForTab(Tab tab) {
        return switch (tab) {
            case ABILITIES -> ABILITIES_BG;
            case CURSED_ENERGY -> CURSED_ENERGY_BG;
            case TECHNIQUE -> TECHNIQUE_BG;
            case MASTERY -> MASTERY_BG;
            case DOMAIN -> DOMAIN_BG;
            case INVENTORY -> INVENTORY_BG;
            case STATS -> STATS_BG;
            case SETTINGS -> SETTINGS_BG;
        };
    }

    protected void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        tabAreas.clear();
        int tabY = panelTop - 25;

        for (Tab tab : Tab.values()) {
            int tabX = panelLeft + tab.index * 50;
            boolean isActive = tab == activeTab;
            int color = isActive ? 0xFFFFFFFF : 0xFFAAAAAA;

            graphics.fill(tabX, tabY, tabX + 48, tabY + 22, isActive ? 0xFF333333 : 0xFF1A1A1A);
            graphics.renderOutline(tabX, tabY, 48, 22, color);

            graphics.blit(TAB_ICONS, tabX + 4, tabY + 3, tab.iconU, tab.iconV, 16, 16, 256, 256);
            graphics.drawString(this.font, tab.name, tabX + 24, tabY + 7, color, false);

            tabAreas.add(new TabArea(tab, tabX, tabY, 48, 22));
        }
    }

    protected void renderLeftPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int leftX = panelLeft;
        int leftY = panelTop;

        graphics.fill(leftX, leftY, leftX + LEFT_PANEL_WIDTH, leftY + BG_HEIGHT, 0xCC1A1A1A);
        graphics.renderOutline(leftX, leftY, LEFT_PANEL_WIDTH, BG_HEIGHT, 0xFF555555);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                    leftX + 65, leftY + 100, 35,
                    leftX + 65 - mouseX, leftY + 70 - mouseY, player);
        }

        ICursedEnergy ce = ClientCEData.getPlayerData();
        String grade = ce != null ? Grade.fromMastery(ce.getMasteryLevel()).getTitle() : "Grade 4";
        graphics.drawString(this.font, "Grade: " + grade, leftX + 10, leftY + 140, 0xFFAAAAAA, false);
    }

    protected abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    protected int contentLeft() {
        return panelLeft + LEFT_PANEL_WIDTH + 10;
    }

    protected int contentTop() {
        return panelTop + CONTENT_TOP;
    }

    protected int contentWidth() {
        return BG_WIDTH - LEFT_PANEL_WIDTH - 20;
    }

    protected static class TabArea {
        final Tab tab;
        final int x, y, width, height;

        TabArea(Tab tab, int x, int y, int width, int height) {
            this.tab = tab;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean contains(int mx, int my) {
            return mx >= x && mx < x + width && my >= y && my < y + height;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (TabArea area : tabAreas) {
            if (area.contains((int) mouseX, (int) mouseY) && area.tab != activeTab) {
                Minecraft.getInstance().setScreen(createScreenForTab(area.tab));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Screen createScreenForTab(Tab tab) {
        return switch (tab) {
            case ABILITIES -> new AbilitiesTabScreen();
            // TODO: Add other tab screens here when you create them
            default -> this;
        };
    }
}