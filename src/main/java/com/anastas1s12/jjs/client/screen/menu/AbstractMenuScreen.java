package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * =============================================================================
 * AbstractMenuScreen — Texture-based full-screen menu
 * =============================================================================
 *
 * TEXTURE FILES  (place in assets/jjs/textures/gui/menu/)
 * --------------------------------------------------------
 *
 *   screen_template.png  (512×256)
 *     Shared background for Stats, Technique, Domain tabs.
 *     All chrome (tab bar area, sidebars, hotbar row, close button) is baked in.
 *
 *   abilities_screen.png  (512×256)
 *     Background for the Abilities tab only.
 *     Same chrome as the template plus the 4 coloured ability rows baked in.
 *
 *   menu_tab.png  (72×16)
 *     Single tab sprite drawn for every tab.
 *     The ACTIVE tab is blitted normally.
 *     INACTIVE tabs are blitted with a dark tint overlay.
 *     HOVERED  tabs are blitted with a subtle bright tint overlay.
 *     (One sprite — code handles the state colouring.)
 *
 *     HOW TO DRAW menu_tab.png in Aseprite:
 *       Canvas 72×16, transparent bg.
 *       Fill with a dark rounded rectangle — same style as the baked tab areas
 *       in your background textures.  e.g. dark fill #1A1A2E, 1px border #6666AA.
 *       The code will draw this sprite at each tab position and tint it to
 *       indicate active vs inactive state.
 *
 * =============================================================================
 *
 * TEXTURE-SPACE HIT-BOX COORDINATES  (on the 512×256 source images)
 * ------------------------------------------------------------------
 * All TX_/TY_ constants below are measured in TEXTURE PIXELS (0-512, 0-256).
 * They are multiplied by scaleX/scaleY at runtime to get screen pixels.
 * Change a constant if you move that element in Aseprite.
 *
 *  Tabs — four tabs, each drawn with menu_tab.png (72×16)
 *    Tab 0:  TX=  4, TY= 2   ← top-left of tab 0 on the background texture
 *    Tab 1:  TX= 80, TY= 2   ← top-left of tab 1
 *    Tab 2:  TX=156, TY= 2   ← top-left of tab 2
 *    Tab 3:  TX=232, TY= 2   ← top-left of tab 3
 *    TX_TAB_W = 72            ← tab sprite width  (matches menu_tab.png)
 *    TY_TAB_H = 16            ← tab sprite height (matches menu_tab.png)
 *
 *  Close button — baked into the background texture, no separate sprite
 *    TX_CLOSE   = 495, TY_CLOSE   =  2   ← top-left
 *    TX_CLOSE_W =  14, TY_CLOSE_H = 14   ← size
 *
 *  Left sidebar (ability list area)
 *    TX_LEFT_SBX =  6, TY_SB    = 40   ← top-left
 *    TX_SB_W     = 77, TY_SB_H  = 205  ← size  (83-6=77, 245-40=205)
 *
 *  Right sidebar (ability info area)
 *    TX_RIGHT_SBX = 428                ← left edge  (same TY_SB, TX_SB_W, TY_SB_H)
 *
 *  Center content area (between sidebars, where the rows sit)
 *    TX_CENTER_X =  84  ← left edge  (6 + 77 + 1 gap)
 *    TY_CENTER_Y =  40  ← top edge
 *    TX_CENTER_W = 343  ← width      (428 - 1 gap - 84)
 *    TY_CENTER_H = 205  ← height
 *
 *  Hotbar slots — 9 slots, each 28×28, y=222
 *    Individual X positions: 106,140,174,208,242,276,310,344,378
 *    TY_HOTBAR       = 222
 *    TX_HOTBAR_SLOT_W = 28
 *    TY_HOTBAR_SLOT_H = 28
 *
 * =============================================================================
 */
public abstract class AbstractMenuScreen extends Screen {

    // =========================================================================
    // Textures
    // =========================================================================

    /** Shared background for Stats / Technique / Domain tabs. 512×256. */
    protected static final ResourceLocation TEX_TEMPLATE =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                    "textures/gui/menu/screen_template.png");

    /** Background for the Abilities tab (has the 4 coloured rows baked in). 512×256. */
    protected static final ResourceLocation TEX_ABILITIES =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                    "textures/gui/menu/abilities_screen.png");

    /**
     * Single tab sprite. 72×16.
     * Drawn at each tab position; active tab is drawn at full brightness,
     * inactive tabs get a dark tint applied on top.
     */
    private static final ResourceLocation TEX_TAB =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                    "textures/gui/menu/menu_tab.png");

    // =========================================================================
    // Source texture size
    // =========================================================================

    protected static final int TEX_W = 512; // ← background texture width  (px)
    protected static final int TEX_H = 256; // ← background texture height (px)

    // =========================================================================
    // Texture-space coordinates  (measured on the 512×256 background textures)
    // =========================================================================

    // ---- Tabs ---------------------------------------------------------------
    // Each tab is drawn by blitting TEX_TAB (72×16) at the position below.
    // Tab X positions on the texture:
    private static final int[] TX_TAB_X = { 4, 80, 156, 232 }; // ← left edge of each tab
    private static final int   TY_TAB   = 2;                    // ← top edge of all tabs
    private static final int   TX_TAB_W = 72;                   // ← tab sprite width  (must match menu_tab.png)
    private static final int   TY_TAB_H = 16;                   // ← tab sprite height (must match menu_tab.png)

    // ---- Close button -------------------------------------------------------
    private static final int TX_CLOSE   = 495; // ← left edge on texture
    private static final int TY_CLOSE   =   2; // ← top edge on texture
    private static final int TX_CLOSE_W =  14; // ← width
    private static final int TY_CLOSE_H =  14; // ← height

    // ---- Left sidebar -------------------------------------------------------
    protected static final int TX_LEFT_SBX =   6; // ← left edge on texture
    protected static final int TY_SB       =  40; // ← top edge (both sidebars)
    protected static final int TX_SB_W     =  77; // ← width   (83 - 6  = 77)
    protected static final int TY_SB_H     = 205; // ← height  (245 - 40 = 205)

    // ---- Right sidebar ------------------------------------------------------
    protected static final int TX_RIGHT_SBX = 428; // ← left edge on texture (505 - 77 = 428)

    // ---- Center content area ------------------------------------------------
    protected static final int TX_CENTER_X =  84; // ← left edge  (6 + 77 + 1)
    protected static final int TY_CENTER_Y =  40; // ← top edge
    protected static final int TX_CENTER_W = 343; // ← width      (428 - 1 - 84)
    protected static final int TY_CENTER_H = 205; // ← height

    // ---- Hotbar slots -------------------------------------------------------
    // Each slot is 28×28. Y position is 222.
    // X positions: 106, 140, 174, 208, 242, 276, 310, 344, 378
    private static final int   TY_HOTBAR        = 222; // ← top edge of every slot
    private static final int   TX_HOTBAR_SLOT_W =  28; // ← slot width
    private static final int   TY_HOTBAR_SLOT_H =  28; // ← slot height
    private static final int[] TX_HOTBAR_SLOT_X = {    // ← left edge of each slot
            106, 140, 174, 208, 242, 276, 310, 344, 378
    };

    // =========================================================================
    // Constants
    // =========================================================================

    protected static final int TAB_COUNT    = 4;
    protected static final int HOTBAR_SLOTS = 9;

    // Tint colors applied on top of the tab sprite to show state
    private static final int TAB_TINT_INACTIVE = 0xAA000000; // ← dark overlay on inactive tabs
    private static final int TAB_TINT_HOVER    = 0x22FFFFFF; // ← bright overlay on hovered tabs

    // Tab label text colors
    private static final int TAB_COLOR_ACTIVE   = 0xFFFFFFFF;
    private static final int TAB_COLOR_INACTIVE = 0xFF888899;

    protected static final int COLOR_TEXT    = 0xFFFFFFFF;
    protected static final int COLOR_SUBTEXT = 0xFF888899;

    private static final String[] TAB_LABELS = { "Abilities", "Stats", "Technique", "Domain" };

    // =========================================================================
    // Computed screen-space fields  (set in init from texture coords × scale)
    // =========================================================================

    protected float scaleX, scaleY;

    // Left sidebar screen bounds
    protected int leftSidebarX, leftSidebarY, leftSidebarW, leftSidebarH;

    // Right sidebar screen bounds
    protected int rightSidebarX, rightSidebarY, rightSidebarW, rightSidebarH;

    // Center area screen bounds
    protected int centerX, centerY, centerW, centerH;

    // Hotbar: per-slot X positions and shared Y/size (screen space)
    protected int   hotbarY;
    protected int   hotbarSlotW, hotbarSlotH;
    protected int[] hotbarSlotScreenX = new int[HOTBAR_SLOTS];

    // Close button screen bounds
    private int closeX, closeY, closeW, closeH;

    // =========================================================================
    // State
    // =========================================================================

    protected final int activeTab;
    private   final ResourceLocation backgroundTex;

    // =========================================================================
    // Constructor
    // =========================================================================

    protected AbstractMenuScreen(Component title, int activeTab, ResourceLocation backgroundTex) {
        super(title);
        this.activeTab     = activeTab;
        this.backgroundTex = backgroundTex;
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================

    @Override
    protected void init() {
        super.init();

        // Scale: background texture (512×256) → current screen size
        scaleX = (float) this.width  / TEX_W;
        scaleY = (float) this.height / TEX_H;

        // Sidebars
        leftSidebarX  = toScreenX(TX_LEFT_SBX);
        leftSidebarY  = toScreenY(TY_SB);
        leftSidebarW  = toScreenW(TX_SB_W);
        leftSidebarH  = toScreenH(TY_SB_H);

        rightSidebarX = toScreenX(TX_RIGHT_SBX);
        rightSidebarY = toScreenY(TY_SB);
        rightSidebarW = toScreenW(TX_SB_W);
        rightSidebarH = toScreenH(TY_SB_H);

        // Center area
        centerX = toScreenX(TX_CENTER_X);
        centerY = toScreenY(TY_CENTER_Y);
        centerW = toScreenW(TX_CENTER_W);
        centerH = toScreenH(TY_CENTER_H);

        // Hotbar
        hotbarY      = toScreenY(TY_HOTBAR);
        hotbarSlotW  = toScreenW(TX_HOTBAR_SLOT_W);
        hotbarSlotH  = toScreenH(TY_HOTBAR_SLOT_H);
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            hotbarSlotScreenX[i] = toScreenX(TX_HOTBAR_SLOT_X[i]);
        }

        // Close button
        closeX = toScreenX(TX_CLOSE);
        closeY = toScreenY(TY_CLOSE);
        closeW = toScreenW(TX_CLOSE_W);
        closeH = toScreenH(TY_CLOSE_H);
    }

    // =========================================================================
    // Rendering
    // =========================================================================

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        // 1. Scale-blit the background texture to fill the whole screen
        //    Source: full 512×256 texture → Dest: full screen
        graphics.blit(backgroundTex,
                0, 0, this.width, this.height, // ← dest: entire screen
                0, 0, TEX_W, TEX_H,            // ← source UV: whole texture
                TEX_W, TEX_H);                  // ← atlas size

        // 2. Draw tab sprites on top of the baked tab areas
        renderTabs(graphics, mouseX, mouseY);

        // 3. Subclass draws text/icons/highlights on top
        renderContent(graphics, mouseX, mouseY, partialTick);

        // 4. Widgets & tooltips
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // ---- Tab rendering ------------------------------------------------------

    /**
     * Draws the tab sprite (menu_tab.png, 72×16) at each of the four tab
     * positions, then applies a tint overlay to show active/inactive/hover state,
     * then draws the text label centered on each tab.
     *
     * Positions in texture space (converted to screen space via scale):
     *   Tab 0: TX=  4, TY= 2
     *   Tab 1: TX= 80, TY= 2
     *   Tab 2: TX=156, TY= 2
     *   Tab 3: TX=232, TY= 2
     *   Size:  72×16  (must match menu_tab.png canvas size)
     */
    private void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        int tabScreenW = toScreenW(TX_TAB_W); // ← rendered width  of one tab on screen
        int tabScreenH = toScreenH(TY_TAB_H); // ← rendered height of one tab on screen

        for (int i = 0; i < TAB_COUNT; i++) {
            int tx = toScreenX(TX_TAB_X[i]); // ← screen X of this tab
            int ty = toScreenY(TY_TAB);       // ← screen Y of all tabs

            boolean active  = (i == activeTab);
            boolean hovered = !active && isMouseOverTab(i, mouseX, mouseY);

            // Blit the tab sprite (same source sprite for all tabs — 72×16 full texture)
            graphics.blit(TEX_TAB,
                    tx, ty,            // ← screen dest position
                    tabScreenW,        // ← dest width  (scaled)
                    tabScreenH,        // ← dest height (scaled)
                    0, 0,              // ← source UV: top-left of menu_tab.png
                    TX_TAB_W,          // ← source width  in texture (72)
                    TY_TAB_H,          // ← source height in texture (16)
                    TX_TAB_W,          // ← full texture width  (72)
                    TY_TAB_H);         // ← full texture height (16)

            // Overlay tint: darken inactive tabs, brighten hovered ones
            if (!active && !hovered) {
                // Dark overlay makes inactive tabs visually recede
                graphics.fill(tx, ty, tx + tabScreenW, ty + tabScreenH, TAB_TINT_INACTIVE);
            } else if (hovered) {
                // Subtle bright overlay on hover
                graphics.fill(tx, ty, tx + tabScreenW, ty + tabScreenH, TAB_TINT_HOVER);
            }
            // Active tab: no tint, drawn at full brightness

            // Label centered in tab
            String label = TAB_LABELS[i];
            int lx = tx + (tabScreenW  - font.width(label)) / 2;
            int ly = ty + (tabScreenH  - font.lineHeight)   / 2;
            graphics.drawString(font, label, lx, ly,
                    active ? TAB_COLOR_ACTIVE : TAB_COLOR_INACTIVE, false);
        }
    }

    // =========================================================================
    // Mouse Handling
    // =========================================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Close button hit-box
            if (mouseX >= closeX && mouseX < closeX + closeW
                    && mouseY >= closeY && mouseY < closeY + closeH) {
                if (this.minecraft != null) this.minecraft.setScreen(null);
                return true;
            }
            // Tab hit-boxes
            for (int i = 0; i < TAB_COUNT; i++) {
                if (i != activeTab && isMouseOverTab(i, (int) mouseX, (int) mouseY)) {
                    switchToTab(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** True if the mouse is over tab {@code i} (screen-space). */
    protected boolean isMouseOverTab(int i, int mouseX, int mouseY) {
        int tx = toScreenX(TX_TAB_X[i]);
        int ty = toScreenY(TY_TAB);
        int tw = toScreenW(TX_TAB_W);
        int th = toScreenH(TY_TAB_H);
        return mouseX >= tx && mouseX < tx + tw
                && mouseY >= ty && mouseY < ty + th;
    }

    /** True if the mouse is over hotbar slot {@code i} (screen-space). */
    protected boolean isMouseOverHotbarSlot(int i, int mouseX, int mouseY) {
        int sx = hotbarSlotScreenX[i];
        return mouseX >= sx && mouseX < sx + hotbarSlotW
                && mouseY >= hotbarY && mouseY < hotbarY + hotbarSlotH;
    }

    private void switchToTab(int idx) {
        if (this.minecraft == null) return;
        Screen next = switch (idx) {
            case 0 -> new AbilitiesTabScreen();
            case 1 -> new StatsTabScreen();
            case 2 -> new TechniqueTabScreen();
            case 3 -> new DomainTabScreen();
            default -> null;
        };
        if (next != null) this.minecraft.setScreen(next);
    }

    /** Texture-space X → screen X. */
    protected int toScreenX(int texX) { return Math.round(texX * scaleX); }

    /** Texture-space Y → screen Y. */
    protected int toScreenY(int texY) { return Math.round(texY * scaleY); }

    /** Texture-space width → screen width (minimum 1). */
    protected int toScreenW(int texW) { return Math.max(1, Math.round(texW * scaleX)); }

    /** Texture-space height → screen height (minimum 1). */
    protected int toScreenH(int texH) { return Math.max(1, Math.round(texH * scaleY)); }

    // =========================================================================
    // Abstract hook
    // =========================================================================

    /**
     * Draw dynamic content (text, icons, highlights) on top of the texture.
     *
     * Available screen-space layout fields:
     *   leftSidebarX/Y/W/H   — left panel area
     *   rightSidebarX/Y/W/H  — right panel area
     *   centerX/Y/W/H        — center content area
     *   hotbarSlotScreenX[]  — per-slot X positions
     *   hotbarY              — slot row Y
     *   hotbarSlotW/H        — slot size
     */
    protected abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    // =========================================================================
    // Screen Config
    // =========================================================================

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
