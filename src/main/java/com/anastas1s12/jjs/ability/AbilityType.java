package com.anastas1s12.jjs.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Enum representing the 4 tiers of abilities in a technique.
 * Each type has a display name, color, and banner color for UI rendering.
 */
public enum AbilityType {

    /** Basic abilities — low cost, no cooldown, fundamental moves */
    BASIC("Basic", ChatFormatting.GREEN, 0xFF4CAF50, 0xFF1B5E20),

    /** Advanced abilities — moderate cost, short cooldown, upgraded moves */
    ADVANCED("Advanced", ChatFormatting.AQUA, 0xFF00BCD4, 0xFF006064),

    /** Special moves — high cost, long cooldown, signature techniques */
    SPECIAL_MOVE("Special Move", ChatFormatting.LIGHT_PURPLE, 0xFFE040FB, 0xFF4A148C),

    /** Domain Expansion — ultimate ability, massive cost, very long cooldown */
    DOMAIN("Domain Expansion", ChatFormatting.RED, 0xFFFF1744, 0xFFB71C1C);

    private final String displayName;
    private final ChatFormatting chatColor;
    private final int color;
    private final int bannerColor;

    AbilityType(String displayName, ChatFormatting chatColor, int color, int bannerColor) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.color = color;
        this.bannerColor = bannerColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatFormatting getChatColor() {
        return chatColor;
    }

    /** Returns the hex color for rendering text/icons. */
    public int getColor() {
        return color;
    }

    /** Returns the darker banner background color for the ability detail panel. */
    public int getBannerColor() {
        return bannerColor;
    }

    public Component getDisplayComponent() {
        return Component.literal(displayName).withStyle(chatColor);
    }
}
