package com.anastas1s12.jjs.client.gui;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.ability.Ability;
import com.anastas1s12.jjs.client.ClientAbilityData;
import com.anastas1s12.jjs.client.ClientSorcererState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Renders the Sorcerer ability hotbar when sorcerer mode is active.
 *
 * Texture files required (place in assets/jjs/textures/gui/sorcerer_hotbar/):
 *
 *   sorcerer_hotbar.png   178×45
 *     The full hotbar background. Contains all 9 slot outlines baked in.
 *
 *   selection.png         18×18
 *     Highlight sprite drawn on top of the currently selected slot.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * TEXTURE-SPACE COORDINATES  (on sorcerer_hotbar.png, 178×45)
 * ─────────────────────────────────────────────────────────────────────────────
 *
 *  Hotbar background
 *    drawn at screen center-bottom, scaled from 178×45.
 *
 *  Ability slots — 9 slots, each 16×16 (icon draw area inside the baked border)
 *    Y of all slot icons: 27   ← top edge of icon area inside each slot
 *    X of each slot icon: 5, 24, 43, 62, 81, 100, 119, 138, 157
 *    (Icon is 16×16 source → drawn at 16×16 inside the slot)
 *
 *  Selection highlight — 18×18 sprite, 1px larger than the icon area
 *    Y: 26                     ← 1px above the icon area
 *    X per slot: 4, 23, 42, 61, 80, 99, 118, 137, 156
 *    (1px to the left of each icon X — centered on the slot)
 *
 *  Domain Expansion icon  (16×16, baked-in slot at x=81, y=1 on the texture)
 *    // See renderDomainExpansionIcon() — currently commented out.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * DISPLAY SCALE
 * ─────────────────────────────────────────────────────────────────────────────
 *  The hotbar is rendered at 2× pixel scale so it is clearly visible.
 *  Change DISPLAY_SCALE below if you want a different size.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class SorcererHotbarOverlay implements IGuiOverlay {

    public static final SorcererHotbarOverlay INSTANCE = new SorcererHotbarOverlay();
    private SorcererHotbarOverlay() {}

    // ── Textures ─────────────────────────────────────────────────────────────

    private static final ResourceLocation TEX_HOTBAR =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                    "textures/gui/sorcerer_hotbar/sorcerer_hotbar.png");

    private static final ResourceLocation TEX_SELECTION =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                    "textures/gui/sorcerer_hotbar/selection.png");

    // ── Source texture sizes ──────────────────────────────────────────────────

    /** Width of sorcerer_hotbar.png in pixels. */
    private static final int TEX_HOTBAR_W = 178; // ← change if you resize the texture
    /** Height of sorcerer_hotbar.png in pixels. */
    private static final int TEX_HOTBAR_H =  45; // ← change if you resize the texture

    /** Width of selection.png in pixels. */
    private static final int TEX_SEL_W = 18; // ← change if you resize the selection texture
    /** Height of selection.png in pixels. */
    private static final int TEX_SEL_H = 18; // ← change if you resize the selection texture

    // ── Slot icon positions in texture space (on sorcerer_hotbar.png) ────────

    /**
     * X positions of each slot's icon area (texture pixels).
     * ← Update if you move slots in Aseprite.
     */
    private static final int[] SLOT_ICON_TX = { 5, 24, 43, 62, 81, 100, 119, 138, 157 };

    /**
     * Y position of all slot icon areas (texture pixels).
     * ← Update if you move slots in Aseprite.
     */
    private static final int SLOT_ICON_TY = 27;

    /** Each slot icon is 16×16 in the texture. ← matches ability icon size. */
    private static final int SLOT_ICON_SIZE = 16;

    // ── Selection sprite positions in texture space ───────────────────────────

    /**
     * X positions of each slot's selection sprite (texture pixels).
     * 1px to the left of the matching SLOT_ICON_TX → centers the 18×18 on the 16×16 icon.
     * ← Update if you move slots in Aseprite.
     */
    private static final int[] SEL_TX = { 4, 23, 42, 61, 80, 99, 118, 137, 156 };

    /**
     * Y position of the selection sprite (texture pixels).
     * 1px above the slot icon area.
     * ← Update if you move slots in Aseprite.
     */
    private static final int SEL_TY = 26;

    // ── Display scale ─────────────────────────────────────────────────────────

    /**
     * Pixel scale factor for the hotbar.
     * 2 = each texture pixel renders as 2×2 screen pixels.
     * ← Change this if the hotbar looks too small or too large.
     */
    private static final int DISPLAY_SCALE = 2;

    // ── Rendered sizes (texture px × scale) ──────────────────────────────────

    private static final int HOTBAR_SCREEN_W = TEX_HOTBAR_W * DISPLAY_SCALE;
    private static final int HOTBAR_SCREEN_H = TEX_HOTBAR_H * DISPLAY_SCALE;
    private static final int ICON_SCREEN_SIZE = SLOT_ICON_SIZE * DISPLAY_SCALE;
    private static final int SEL_SCREEN_W = TEX_SEL_W * DISPLAY_SCALE;
    private static final int SEL_SCREEN_H = TEX_SEL_H * DISPLAY_SCALE;

    /** Bottom margin from screen edge to hotbar bottom. */
    private static final int BOTTOM_MARGIN = 2; // ← pixels from screen bottom

    // ── IGuiOverlay ──────────────────────────────────────────────────────────

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick,
                       int screenWidth, int screenHeight) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.options.hideGui) return;
        if (!ClientSorcererState.isSorcererModeActive()) return;

        // Anchor: centered horizontally, BOTTOM_MARGIN px from screen bottom
        int drawX = (screenWidth  - HOTBAR_SCREEN_W) / 2;
        int drawY =  screenHeight - HOTBAR_SCREEN_H   - BOTTOM_MARGIN;

        RenderSystem.enableBlend();

        // 1. Draw hotbar background (full 178×45 texture → HOTBAR_SCREEN_W × HOTBAR_SCREEN_H)
        //    Source: full texture (0,0 → 178,45)  Dest: drawX,drawY → drawX+W, drawY+H
        graphics.blit(TEX_HOTBAR,
                drawX, drawY,                  // ← screen dest top-left
                HOTBAR_SCREEN_W, HOTBAR_SCREEN_H, // ← dest size on screen
                0, 0,                          // ← source UV: top-left of texture
                TEX_HOTBAR_W, TEX_HOTBAR_H,   // ← source size in texture
                TEX_HOTBAR_W, TEX_HOTBAR_H);  // ← full texture size (178×45)

        // 2. Draw selection sprite over the active slot
        renderSelection(graphics, drawX, drawY);

        // 3. Draw ability icons in each slot
        renderSlotIcons(graphics, drawX, drawY, mc);

        // 4. Domain Expansion icon (commented out — system not yet implemented)
        // renderDomainExpansionIcon(graphics, drawX, drawY);

        RenderSystem.disableBlend();
    }

    // ── Selection sprite ─────────────────────────────────────────────────────

    /**
     * Blits selection.png (18×18) over the currently selected slot.
     *
     * Selection positions in texture space:
     *   Y = 26, X per slot = { 4, 23, 42, 61, 80, 99, 118, 137, 156 }
     * Rendered at DISPLAY_SCALE × source size.
     */
    private void renderSelection(GuiGraphics graphics, int hotbarDrawX, int hotbarDrawY) {
        int slot = ClientSorcererState.getSelectedSlot();
        if (slot < 0 || slot >= SEL_TX.length) return;

        // Convert texture-space position to screen position
        // selTX * DISPLAY_SCALE gives the offset from the hotbar's top-left corner
        int sx = hotbarDrawX + SEL_TX[slot] * DISPLAY_SCALE; // ← screen X of selection sprite
        int sy = hotbarDrawY + SEL_TY         * DISPLAY_SCALE; // ← screen Y of selection sprite

        graphics.blit(TEX_SELECTION,
                sx, sy,                    // ← screen dest top-left
                SEL_SCREEN_W, SEL_SCREEN_H, // ← dest size on screen
                0, 0,                      // ← source UV: full 18×18 texture
                TEX_SEL_W, TEX_SEL_H,     // ← source size in texture (18×18)
                TEX_SEL_W, TEX_SEL_H);    // ← full texture size (18×18)
    }

    // ── Slot icons ───────────────────────────────────────────────────────────

    /**
     * Draws each ability icon into its slot.
     *
     * Icon positions in texture space:
     *   Y = 27, X per slot = { 5, 24, 43, 62, 81, 100, 119, 138, 157 }
     *
     * The ability icon texture is 16×16 (SOURCE_ICON_W/H in AbilitiesTabScreen).
     * It is scaled up to ICON_SCREEN_SIZE × ICON_SCREEN_SIZE on screen.
     */
    private void renderSlotIcons(GuiGraphics graphics, int hotbarDrawX, int hotbarDrawY,
                                  Minecraft mc) {
        for (int i = 0; i < ClientAbilityData.HOTBAR_SLOTS; i++) {
            Ability ability = ClientAbilityData.getSlotAbility(i);
            if (ability == null) continue;

            // Convert texture-space slot icon position to screen position
            int ix = hotbarDrawX + SLOT_ICON_TX[i] * DISPLAY_SCALE; // ← screen X of icon
            int iy = hotbarDrawY + SLOT_ICON_TY     * DISPLAY_SCALE; // ← screen Y of icon

            // Blit the ability icon, scaling 16×16 source → ICON_SCREEN_SIZE × ICON_SCREEN_SIZE
            graphics.blit(ability.getIcon(),
                    ix, iy,               // ← dest top-left on screen
                    ICON_SCREEN_SIZE,     // ← dest width  (16 * DISPLAY_SCALE)
                    ICON_SCREEN_SIZE,     // ← dest height (16 * DISPLAY_SCALE)
                    0, 0,                 // ← source UV: top-left of icon texture
                    16, 16,               // ← source size in icon texture (16×16)
                    16, 16);              // ← full icon texture size (16×16)
        }
    }

    // ── Domain Expansion icon (DISABLED — uncomment when system is ready) ────

    /*
     * renderDomainExpansionIcon
     * ─────────────────────────
     * Draws a 16×16 domain expansion icon at texture position (81, 1) on the
     * hotbar texture — this maps to the center slot's top area.
     *
     * In screen space that is:
     *   x = hotbarDrawX + 81 * DISPLAY_SCALE
     *   y = hotbarDrawY +  1 * DISPLAY_SCALE
     *
     * Texture for the icon: assets/jjs/textures/gui/sorcerer_hotbar/domain_icon.png (16×16)
     * ← Replace the ResourceLocation below with the actual icon path when ready.
     *
     private static final ResourceLocation TEX_DOMAIN_ICON =
             ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID,
                     "textures/gui/sorcerer_hotbar/domain_icon.png");

     private void renderDomainExpansionIcon(GuiGraphics graphics, int hotbarDrawX, int hotbarDrawY) {
         int iconX = hotbarDrawX + 81 * DISPLAY_SCALE; // ← texture x=81, scaled
         int iconY = hotbarDrawY +  1 * DISPLAY_SCALE; // ← texture y=1,  scaled
         int size  = 16 * DISPLAY_SCALE;               // ← 16px source → scaled on screen

         graphics.blit(TEX_DOMAIN_ICON,
                 iconX, iconY,    // ← dest top-left on screen
                 size, size,      // ← dest size on screen
                 0, 0,            // ← source UV: top-left of icon texture
                 16, 16,          // ← source size in texture (16×16)
                 16, 16);         // ← full texture size (16×16)
     }
     */
}
