package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.ability.Grade;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
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

/**
 * =============================================================================
 * BASE MENU SCREEN — Jujutsu Kaisen Sorcerer Menu
 * =============================================================================
 *
 * This is the foundation for all 8 tab screens in the mod's menu system.
 * It handles:
 *   - The 1024x512 background texture
 *   - 8 clickable tabs at the top
 *   - Left panel: player model, name, grade, mastery progress bar
 *   - Left panel lower: CE stats (Output, Efficiency, Control, etc.)
 *   - Right content area: rendered by the active tab subclass
 *
 * TEXTURE NOTES (1024x512):
 *   Background: textures/gui/menu/menu_background.png
 *   Tab icons:  textures/gui/menu/tab_<name>.png (20x20 each)
 *   Stat icons: textures/gui/menu/stat_<name>.png (16x16 each)
 *
 * To create a new tab:
 *   1. Add a Tab entry to the TABS list below
 *   2. Create a new class extending BaseMenuScreen
 *   3. Override renderContent() to draw your tab's UI
 *   4. The tab switching is handled automatically
 *
 * =============================================================================
 */
public abstract class BaseMenuScreen extends Screen {

    // ============================================================
    // TEXTURE CONSTANTS — Update these to match your asset paths
    // ============================================================

    /** Main background texture (1024x512) */
    public static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/menu_background.png");

    /** Tab icon texture sheet — all 8 tab icons in one texture (160x20 = 8 icons * 20px each) */
    public static final ResourceLocation TAB_ICONS =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/tab_icons.png");

    /** Stat icon texture sheet — all 9 stat icons in one texture */
    public static final ResourceLocation STAT_ICONS =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/stat_icons.png");

    // ============================================================
    // LAYOUT CONSTANTS
    // ============================================================

    /** Width of the background texture */
    protected static final int BG_WIDTH = 400;
    /** Height of the background texture */
    protected static final int BG_HEIGHT = 240;

    /** Left panel width (player model + stats area) */
    protected static final int LEFT_PANEL_WIDTH = 130;
    /** Top offset where the content area starts (below tabs) */
    protected static final int CONTENT_TOP = 35;
    /** Bottom offset where content ends (above any bottom bars) */
    protected static final int CONTENT_BOTTOM = 220;

    // ============================================================
    // TAB DEFINITIONS
    // ============================================================

    /**
     * Defines each tab: index, display name, and icon UV coordinates.
     * The index determines the order left-to-right.
     */
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
        public final int iconU; // X position in the tab icon texture sheet
        public final int iconV; // Y position in the tab icon texture sheet

        Tab(int index, String name, int iconU, int iconV) {
            this.index = index;
            this.name = name;
            this.iconU = iconU;
            this.iconV = iconV;
        }
    }

    // ============================================================
    // FIELDS
    // ============================================================

    /** Which tab is currently active */
    protected final Tab activeTab;

    /** Screen center coordinates (computed in init/render) */
    protected int centerX;
    protected int centerY;

    /** Top-left corner of the background panel */
    protected int panelLeft;
    protected int panelTop;

    /** List of tab buttons (computed each render for click detection) */
    protected final List<TabArea> tabAreas = new ArrayList<>();

    /** Tooltip text to render this frame (set by subclasses) */
    protected String tooltipText = null;
    protected int tooltipX = 0;
    protected int tooltipY = 0;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected BaseMenuScreen(Tab activeTab) {
        super(Component.literal("Jujutsu Sorcery Menu"));
        this.activeTab = activeTab;
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void init() {
        super.init();
        // Center the 1024x512 panel on screen
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.panelLeft = centerX - BG_WIDTH / 2;
        this.panelTop = centerY - BG_HEIGHT / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 1. Draw dark background behind the menu
        this.renderBackground(graphics);

        // 2. Draw the main menu background texture (1024x512 scaled to fit)
        renderBackgroundTexture(graphics);

        // 3. Draw the 8 tabs at the top
        renderTabs(graphics, mouseX, mouseY);

        // 4. Draw the left panel (player model, name, grade, stats)
        renderLeftPanel(graphics, mouseX, mouseY, partialTick);

        // 5. Draw the tab-specific content (implemented by subclasses)
        PoseStack pose = graphics.pose();
        pose.pushPose();
        // Clip to content area so tabs don't draw outside
        renderContent(graphics, mouseX, mouseY, partialTick);
        pose.popPose();

        // 6. Draw tooltip if set
        if (tooltipText != null) {
            graphics.renderTooltip(this.font, Component.literal(tooltipText), tooltipX, tooltipY);
            tooltipText = null;
        }
    }

    // ============================================================
    // RENDERING — Background
    // ============================================================

    /**
     * Draws the 1024x512 background texture centered on screen.
     * The texture is scaled to fit the BG_WIDTH x BG_HEIGHT area.
     */
    protected void renderBackgroundTexture(GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();

        // Draw the background texture stretched to BG_WIDTH x BG_HEIGHT
        graphics.blit(BACKGROUND_TEXTURE, panelLeft, panelTop, 0, 0,
                BG_WIDTH, BG_HEIGHT, 1024, 512);

        RenderSystem.disableBlend();
    }

    // ============================================================
    // RENDERING — Tabs
    // ============================================================

    /**
     * Renders the 8 tab buttons across the top of the menu.
     * Active tab is highlighted. Hovering shows the tab name.
     */
    protected void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        tabAreas.clear();
        int tabWidth = 44;
        int tabHeight = 22;
        int startX = panelLeft + 10;
        int tabY = panelTop + 5;

        for (Tab tab : Tab.values()) {
            int tabX = startX + tab.index * (tabWidth + 2);
            boolean isActive = tab == this.activeTab;
            boolean isHovered = mouseX >= tabX && mouseX < tabX + tabWidth
                    && mouseY >= tabY && mouseY < tabY + tabHeight;

            // Tab background — active tab is brighter
            int bgColor = isActive ? 0xFF3A3A3A : (isHovered ? 0xFF2A2A2A : 0xFF1A1A1A);
            int borderColor = isActive ? 0xFF00E676 : (isHovered ? 0xFFAAAAAA : 0xFF444444);

            // Draw tab background
            graphics.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, bgColor);
            // Draw tab border
            graphics.renderOutline(tabX, tabY, tabWidth, tabHeight, borderColor);

            // Draw tab icon (from texture sheet)
            // Icon is 16x16, centered in the tab
            int iconX = tabX + (tabWidth - 16) / 2;
            int iconY = tabY + 2;

            // For now, render a colored square as placeholder icon
            // Replace this with actual icon blit from your texture sheet
            int iconColor = getTabColor(tab);
            graphics.fill(iconX, iconY, iconX + 16, iconY + 16, iconColor);

            // Store clickable area
            tabAreas.add(new TabArea(tab, tabX, tabY, tabWidth, tabHeight));

            // Tooltip on hover
            if (isHovered && !isActive) {
                tooltipText = tab.name;
                tooltipX = mouseX;
                tooltipY = mouseY - 15;
            }
        }
    }

    /** Returns a color for each tab's placeholder icon. Replace with actual icons. */
    protected int getTabColor(Tab tab) {
        return switch (tab) {
            case ABILITIES -> 0xFF4CAF50;    // Green
            case CURSED_ENERGY -> 0xFF00E676; // Bright green
            case TECHNIQUE -> 0xFF2196F3;     // Blue
            case MASTERY -> 0xFFFF9800;       // Orange
            case DOMAIN -> 0xFFF44336;        // Red
            case INVENTORY -> 0xFF9C27B0;     // Purple
            case STATS -> 0xFF00BCD4;         // Cyan
            case SETTINGS -> 0xFF607D8B;      // Blue-gray
        };
    }

    // ============================================================
    // RENDERING — Left Panel
    // ============================================================

    /**
     * Renders the left side panel containing:
     *   - Player entity model (rotatable)
     *   - Player name
     *   - Grade title with color
     *   - Mastery progress bar
     *   - CE stat list (Output, Efficiency, Control, etc.)
     */
    protected void renderLeftPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int leftX = panelLeft + 8;
        int contentY = panelTop + CONTENT_TOP;

        // ---- Player Name ----
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            String name = player.getName().getString();
            graphics.drawString(this.font, name, leftX, contentY, 0xFFFFFFFF, true);
        }
        contentY += 12;

        // ---- Grade ----
        Grade grade = getPlayerGrade();
        String gradeText = grade.getTitle();
        graphics.drawString(this.font,
                Component.literal(gradeText).withStyle(grade.getChatColor()),
                leftX, contentY, grade.getColor(), true);
        contentY += 14;

        // ---- Mastery Progress Bar ----
        int mastery = ClientCEData.getMasteryLevel();
        int masteryXP = ClientCEData.getMasteryXP();
        int xpNeeded = com.anastas1s12.jjs.capability.CursedEnergy.MASTERY_XP_PER_LEVEL;
        float masteryProgress = (float) masteryXP / xpNeeded;

        renderProgressBar(graphics, leftX, contentY, 110, 8,
                masteryProgress,
                String.format("Mastery %d", mastery),
                0xFFFFD700, 0xFF444444);
        contentY += 16;

        // ---- Divider ----
        graphics.hLine(leftX, leftX + 110, contentY, 0xFF555555);
        contentY += 6;

        // ---- CE Stats List ----
        // Each stat: icon + label + value bar
        renderStatRow(graphics, leftX, contentY, "CE",
                String.format("%.0f / %.0f", ClientCEData.getCurrentCE(), ClientCEData.getMaxCE()),
                ClientCEData.getCERatio(), 0xFF00E676);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Output",
                String.format("%.1fx", ClientCEData.getOutput()),
                Math.min(ClientCEData.getOutput() / 5.0f, 1.0f), 0xFFFF5722);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Efficiency",
                String.format("%.0f%%", ClientCEData.getEfficiency() * 100),
                ClientCEData.getEfficiency() / 0.95f, 0xFF2196F3);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Control",
                getControlText(), getControlRatio(), 0xFF9C27B0);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Mastery",
                String.valueOf(ClientCEData.getMasteryLevel()),
                Math.min(mastery / 100.0f, 1.0f), 0xFFFF9800);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "BF Rate",
                getBFRateText(), getBFRateRatio(), 0xFFFF1744);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "RCT",
                ClientCEData.isRctActive() ? "Active" : "Locked",
                ClientCEData.isRctActive() ? 1.0f : 0.0f, 0xFFE040FB);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Dom. Stability",
                getDomainStabilityText(), getDomainStabilityRatio(), 0xFFF44336);
        contentY += 18;

        renderStatRow(graphics, leftX, contentY, "Dom. Mastery",
                getDomainMasteryText(), getDomainMasteryRatio(), 0xFF673AB7);
    }

    /**
     * Renders a single stat row: colored icon placeholder, label, value text, and a mini bar.
     */
    protected void renderStatRow(GuiGraphics graphics, int x, int y,
                                  String label, String valueText,
                                  float fillRatio, int color) {
        // Icon placeholder (8x8 colored square)
        graphics.fill(x, y + 2, x + 8, y + 10, color);

        // Label
        graphics.drawString(this.font, label, x + 12, y, 0xFFAAAAAA, false);

        // Value text (right-aligned)
        int valueWidth = this.font.width(valueText);
        graphics.drawString(this.font, valueText, x + 110 - valueWidth, y, color, false);

        // Mini progress bar below
        int barY = y + 11;
        graphics.fill(x + 12, barY, x + 110, barY + 3, 0xFF333333);
        int fillWidth = (int) ((110 - 12) * Math.max(0, Math.min(1, fillRatio)));
        if (fillWidth > 0) {
            graphics.fill(x + 12, barY, x + 12 + fillWidth, barY + 3, color);
        }
    }

    /**
     * Renders a progress bar with a label in the center.
     */
    protected void renderProgressBar(GuiGraphics graphics, int x, int y, int width, int height,
                                      float progress, String label, int fillColor, int bgColor) {
        // Background
        graphics.fill(x, y, x + width, y + height, bgColor);
        // Fill
        int fillWidth = (int) (width * Math.max(0, Math.min(1, progress)));
        if (fillWidth > 0) {
            graphics.fill(x, y, x + fillWidth, y + height, fillColor);
        }
        // Border
        graphics.renderOutline(x, y, width, height, 0xFF666666);
        // Label centered
        int labelWidth = this.font.width(label);
        graphics.drawString(this.font, label, x + (width - labelWidth) / 2, y + 1,
                0xFFFFFFFF, true);
    }

    // ============================================================
    // PLAYER MODEL RENDERING
    // ============================================================

    /**
     * Renders the player's entity model in the left panel area.
     * Call this from a tab screen if you want the model displayed.
     *
     * @param graphics   GuiGraphics
     * @param x          Center X of where to draw the model
     * @param y          Top Y of where to draw the model
     * @param mouseX     Current mouse X (for rotation interaction)
     * @param mouseY     Current mouse Y
     * @param scale      Scale factor for the model
     * @param partialTick Partial tick for smooth animation
     */
    protected void renderPlayerModel(GuiGraphics graphics, int x, int y,
                                      int mouseX, int mouseY, float scale, float partialTick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        float rotX = (float) Math.atan((mouseX - x) / 40.0f) * 20.0f;
        float rotY = (float) Math.atan((mouseY - (y + 40)) / 40.0f) * -20.0f;

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                x + 30,
                y + 100,
                (int) (30 * scale),
                rotX,
                rotY,
                player
        );

    }

    // ============================================================
    // ABSTRACT METHOD — Subclasses implement this
    // ============================================================

    /**
     * Render the tab-specific content in the right panel area.
     * Coordinate system: (0,0) is the top-left of the content area.
     * Available space: roughly (panelLeft + LEFT_PANEL_WIDTH + 10) to (panelLeft + BG_WIDTH - 10)
     *                  and (panelTop + CONTENT_TOP) to (panelTop + CONTENT_BOTTOM)
     *
     * @param graphics    GuiGraphics for drawing
     * @param mouseX      Absolute mouse X
     * @param mouseY      Absolute mouse Y
     * @param partialTick Partial tick for animations
     */
    protected abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    // ============================================================
    // MOUSE HANDLING — Tab Clicks
    // ============================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if a tab was clicked
        for (TabArea area : tabAreas) {
            if (area.contains((int) mouseX, (int) mouseY) && area.tab != this.activeTab) {
                switchToTab(area.tab);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Switch to a different tab screen. Override in the concrete screen classes
     * to open the correct screen for each tab.
     */
    protected void switchToTab(Tab tab) {
        Minecraft mc = Minecraft.getInstance();
        // Close current screen and open the new tab's screen
        mc.popGuiLayer();

        BaseMenuScreen newScreen = switch (tab) {
            case ABILITIES -> new AbilitiesTabScreen();
            case CURSED_ENERGY -> new CursedEnergyTabScreen();
            case TECHNIQUE -> new TechniqueTabScreen();
            case MASTERY -> new MasteryTabScreen();
            case DOMAIN -> new DomainTabScreen();
            case INVENTORY -> new InventoryTabScreen();
            case STATS -> new StatsTabScreen();
            case SETTINGS -> new SettingsTabScreen();
        };

        mc.pushGuiLayer(newScreen);
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    /** Returns the content area bounds for the right panel. */
    protected int contentLeft() {
        return panelLeft + LEFT_PANEL_WIDTH + 15;
    }

    protected int contentRight() {
        return panelLeft + BG_WIDTH - 10;
    }

    protected int contentTop() {
        return panelTop + CONTENT_TOP;
    }

    protected int contentWidth() {
        return contentRight() - contentLeft();
    }

    protected int contentHeight() {
        return CONTENT_BOTTOM - CONTENT_TOP;
    }

    /** Get the player's grade from their mastery level. */
    protected Grade getPlayerGrade() {
        return Grade.fromMastery(ClientCEData.getMasteryLevel());
    }

    // ---- Placeholder stat getters — override or replace with real data ----

    protected String getControlText() {
        int mastery = ClientCEData.getMasteryLevel();
        if (mastery < 20) return "Low";
        if (mastery < 50) return "Medium";
        if (mastery < 80) return "High";
        return "Master";
    }

    protected float getControlRatio() {
        return Math.min(ClientCEData.getMasteryLevel() / 100.0f, 1.0f);
    }

    protected String getBFRateText() {
        // Placeholder — will be replaced with actual Black Flash rate stat
        return "12%";
    }

    protected float getBFRateRatio() {
        return 0.12f;
    }

    protected String getDomainStabilityText() {
        int mastery = ClientCEData.getMasteryLevel();
        if (mastery < 50) return "Unstable";
        if (mastery < 75) return "Stable";
        if (mastery < 100) return "Very Stable";
        return "Perfect";
    }

    protected float getDomainStabilityRatio() {
        return Math.min(ClientCEData.getMasteryLevel() / 100.0f, 1.0f);
    }

    protected String getDomainMasteryText() {
        int mastery = ClientCEData.getMasteryLevel();
        if (mastery < 50) return "Locked";
        return mastery + "%";
    }

    protected float getDomainMasteryRatio() {
        int mastery = ClientCEData.getMasteryLevel();
        if (mastery < 50) return 0.0f;
        return Math.min((mastery - 50) / 50.0f, 1.0f);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game while in the menu
    }

    // ============================================================
    // INNER CLASSES
    // ============================================================

    /**
     * Represents a clickable tab area on screen.
     */
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
}
