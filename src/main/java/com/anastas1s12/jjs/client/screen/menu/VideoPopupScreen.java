package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.ability.Ability;
import com.anastas1s12.jjs.ability.AbilityType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * =============================================================================
 * VIDEO POPUP SCREEN — Ability Preview Overlay
 * =============================================================================
 *
 * A popup overlay that appears when pressing [P] on a selected ability.
 * It renders on top of the Abilities tab and shows:
 *
 *   - Dark semi-transparent background overlay
 *   - A custom popup texture (from your 1024x512 texture set)
 *   - Ability name and type banner at the top
 *   - Video player area (center)
 *   - Description text below the video
 *   - Close button (X) in the top right
 *   - "Press ESC to close" hint
 *
 * NOTE ON VIDEO PLAYBACK:
 * Minecraft Forge 1.20.1 does not have built-in video playback in GUIs.
 * This screen provides the UI framework. You need to integrate a video
 * library (like VLCJ) or use an animated texture sequence for the preview.
 *
 * ALTERNATIVE: Use a slideshow of keyframes as a flipbook texture instead
 * of true video playback. This is much simpler to implement and requires
 * no external libraries.
 *
 * TEXTURE PATHS:
 *   - Popup background: textures/gui/menu/video_popup.png
 *   - Video placeholder: textures/gui/menu/video_placeholder.png
 *   - Close button: textures/gui/menu/close_button.png
 *
 * =============================================================================
 */
public class VideoPopupScreen extends Screen {

    // ============================================================
    // TEXTURES
    // ============================================================

    /** Popup window background texture */
    public static final ResourceLocation POPUP_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/video_popup.png");

    /** Placeholder shown while video loads (or instead of video) */
    public static final ResourceLocation VIDEO_PLACEHOLDER =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/video_placeholder.png");

    /** Close button (X) texture */
    public static final ResourceLocation CLOSE_BUTTON =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/close_button.png");

    // ============================================================
    // LAYOUT CONSTANTS
    // ============================================================

    /** Width of the popup window */
    private static final int POPUP_WIDTH = 320;
    /** Height of the popup window */
    private static final int POPUP_HEIGHT = 240;
    /** Height of the type banner at the top */
    private static final int BANNER_HEIGHT = 26;
    /** Height of the video area */
    private static final int VIDEO_AREA_HEIGHT = 160;
    /** Size of the close button */
    private static final int CLOSE_BTN_SIZE = 16;

    // ============================================================
    // FIELDS
    // ============================================================

    /** The parent screen to return to when closing */
    private final Screen parentScreen;

    /** The ability being previewed */
    private final Ability ability;

    /** Center position of the popup */
    private int popupX;
    private int popupY;

    /** Animation timer for the popup open effect */
    private float openProgress = 0.0f;

    /** Whether the close button is hovered */
    private boolean closeHovered = false;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public VideoPopupScreen(Screen parent, Ability ability) {
        super(Component.literal("Ability Preview: " + ability.getName()));
        this.parentScreen = parent;
        this.ability = ability;
    }

    @Override
    protected void init() {
        super.init();
        this.popupX = (this.width - POPUP_WIDTH) / 2;
        this.popupY = (this.height - POPUP_HEIGHT) / 2;
    }

    // ============================================================
    // RENDER
    // ============================================================

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Update open animation
        if (openProgress < 1.0f) {
            openProgress = Math.min(1.0f, openProgress + partialTick * 0.15f);
        }

        // 1. Dark overlay behind the popup
        renderOverlay(graphics);

        // 2. Popup window with scaling animation
        PoseStack pose = graphics.pose();
        pose.pushPose();

        // Scale animation from center
        float scale = 0.8f + (0.2f * easeOutBack(openProgress));
        pose.translate(this.width / 2.0, this.height / 2.0, 0);
        pose.scale(scale, scale, 1.0f);
        pose.translate(-this.width / 2.0, -this.height / 2.0, 0);

        // 3. Popup background
        renderPopupBackground(graphics);

        // 4. Type banner at top
        renderBanner(graphics);

        // 5. Close button
        renderCloseButton(graphics, mouseX, mouseY);

        // 6. Video / preview area
        renderVideoArea(graphics);

        // 7. Ability info below video
        renderAbilityInfo(graphics);

        pose.popPose();
    }

    // ============================================================
    // RENDER COMPONENTS
    // ============================================================

    /**
     * Renders the dark semi-transparent overlay behind the popup.
     */
    private void renderOverlay(GuiGraphics graphics) {
        // Full-screen dark overlay with fade-in
        int alpha = (int) (0xBB * openProgress) << 24;
        graphics.fill(0, 0, this.width, this.height, alpha | 0x000000);
    }

    /**
     * Renders the popup window background.
     */
    private void renderPopupBackground(GuiGraphics graphics) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();

        // Main popup background
        graphics.blit(POPUP_TEXTURE, popupX, popupY, 0, 0,
                POPUP_WIDTH, POPUP_HEIGHT, 512, 384);

        // Fallback: if texture is missing, draw a solid panel
        // (Remove this once you have the texture)
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + POPUP_HEIGHT, 0xFF1E1E1E);
        graphics.renderOutline(popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT, 0xFF555555);

        RenderSystem.disableBlend();
    }

    /**
     * Renders the colored type banner at the top of the popup.
     */
    private void renderBanner(GuiGraphics graphics) {
        AbilityType type = ability.getType();

        // Banner background
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + BANNER_HEIGHT, type.getBannerColor());

        // Type icon placeholder (small colored square)
        graphics.fill(popupX + 8, popupY + 5, popupX + 20, popupY + 17, type.getColor());

        // Ability name (large, bold)
        graphics.drawString(this.font,
                net.minecraft.network.chat.Component.literal(ability.getName()).withStyle(net.minecraft.ChatFormatting.BOLD),
                popupX + 26, popupY + 6, 0xFFFFFFFF, true);

        // Type label (right side)
        String typeLabel = type.getDisplayName();
        int typeWidth = this.font.width(typeLabel);
        graphics.drawString(this.font, typeLabel,
                popupX + POPUP_WIDTH - typeWidth - 30, popupY + 6, type.getColor(), true);
    }

    /**
     * Renders the close (X) button in the top-right corner.
     */
    private void renderCloseButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int btnX = popupX + POPUP_WIDTH - CLOSE_BTN_SIZE - 6;
        int btnY = popupY + 5;

        closeHovered = mouseX >= btnX && mouseX < btnX + CLOSE_BTN_SIZE
                && mouseY >= btnY && mouseY < btnY + CLOSE_BTN_SIZE;

        int btnColor = closeHovered ? 0xFFFF4444 : 0xFF888888;

        // X drawn with lines
        graphics.drawString(this.font, "X", btnX + 4, btnY + 2,
                closeHovered ? 0xFFFFFFFF : 0xFFCCCCCC, false);

        // Hovered background
        if (closeHovered) {
            graphics.fill(btnX - 2, btnY - 2, btnX + CLOSE_BTN_SIZE + 2, btnY + CLOSE_BTN_SIZE + 2,
                    0x30FF0000);
        }
    }

    /**
     * Renders the video/preview area in the center of the popup.
     *
     * IMPORTANT: This is where you would integrate actual video playback.
     * Options:
     *   1. Use VLCJ library to play MP4 files in a Minecraft GUI
     *   2. Use an animated texture (sequence of PNG frames played as flipbook)
     *   3. Use a static image with particle effects overlaid
     *
     * For now, this renders a placeholder with the ability's damage type color.
     */
    private void renderVideoArea(GuiGraphics graphics) {
        int videoX = popupX + 10;
        int videoY = popupY + BANNER_HEIGHT + 6;
        int videoW = POPUP_WIDTH - 20;
        int videoH = VIDEO_AREA_HEIGHT;

        // Video area background
        graphics.fill(videoX, videoY, videoX + videoW, videoY + videoH, 0xFF0A0A0A);
        graphics.renderOutline(videoX, videoY, videoW, videoH, 0xFF333333);

        // Placeholder content
        // Option A: Show ability icon large in center
        int centerX = videoX + videoW / 2;
        int centerY = videoY + videoH / 2;

        // Large colored rectangle representing the ability
        int previewSize = 64;
        graphics.fill(centerX - previewSize / 2, centerY - previewSize / 2,
                centerX + previewSize / 2, centerY + previewSize / 2,
                ability.getDamageType().getColor() & 0xFF555555);

        // Ability icon symbol (placeholder text)
        String iconText = ability.getName().substring(0, 1);
        int textW = this.font.width(iconText);
        graphics.drawString(this.font, iconText,
                centerX - textW / 2, centerY - 4, ability.getDamageType().getColor(), true);

        // "Preview" label
        String previewLabel = "[ Ability Preview ]";
        int labelW = this.font.width(previewLabel);
        graphics.drawString(this.font, previewLabel,
                centerX - labelW / 2, videoY + videoH - 20, 0xFF666666, false);

        // VIDEO INTEGRATION NOTE:
        // To play actual video, you would:
        // 1. Initialize a video player (VLCJ or custom) in the constructor
        // 2. Render the video frame to a texture here
        // 3. Draw that texture with graphics.blit()
        //
        // Example (conceptual):
        // if (videoPlayer != null && videoPlayer.isPlaying()) {
        //     int textureId = videoPlayer.getCurrentFrameTexture();
        //     RenderSystem.setShaderTexture(0, textureId);
        //     graphics.blit(...);
        // }
    }

    /**
     * Renders ability info below the video area.
     */
    private void renderAbilityInfo(GuiGraphics graphics) {
        int infoY = popupY + BANNER_HEIGHT + VIDEO_AREA_HEIGHT + 14;
        int infoX = popupX + 12;

        // CE Cost and Cooldown on one line
        String infoLine = String.format("CE Cost: %s  |  Cooldown: %s  |  Damage: %s",
                ability.getCostDisplay(),
                ability.getCooldownDisplay(),
                ability.getDamageType().getDisplayName());
        graphics.drawString(this.font, infoLine, infoX, infoY, 0xFFCCCCCC, false);

        infoY += 14;

        // Short description (first line only)
        if (!ability.getDescription().isEmpty()) {
            String desc = ability.getDescription().get(0);
            if (desc.length() > 55) desc = desc.substring(0, 52) + "...";
            graphics.drawString(this.font, desc, infoX, infoY, 0xFF999999, false);
        }

        infoY += 14;

        // Close hint
        String hint = "Press ESC or click X to close";
        int hintW = this.font.width(hint);
        graphics.drawString(this.font, hint,
                popupX + (POPUP_WIDTH - hintW) / 2, infoY, 0xFF666666, false);
    }

    // ============================================================
    // INPUT HANDLING
    // ============================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Close button click
        if (closeHovered) {
            closePopup();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC closes the popup
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closePopup();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Closes the popup and returns to the parent screen.
     */
    private void closePopup() {
        Minecraft mc = Minecraft.getInstance();
        mc.popGuiLayer(); // Remove this popup
        // Parent screen is already underneath
    }

    // ============================================================
    // ANIMATION UTILITY
    // ============================================================

    /**
     * Easing function for smooth popup open animation.
     * "Back" easing — slightly overshoots then settles.
     */
    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
