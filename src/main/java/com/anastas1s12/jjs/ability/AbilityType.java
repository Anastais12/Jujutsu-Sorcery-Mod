package com.anastas1s12.jjs.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum AbilityType {

    BASIC("Basic", ChatFormatting.GREEN, 0xFF4CAF50, 0xFF1B5E20),
    ADVANCED("Advanced", ChatFormatting.AQUA, 0xFF00BCD4, 0xFF006064),
    SPECIAL_MOVE("Special Move", ChatFormatting.LIGHT_PURPLE, 0xFFE040FB, 0xFF4A148C),
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

    public int getColor() {
        return color;
    }

    public int getBannerColor() {
        return bannerColor;
    }

    public Component getDisplayComponent() {
        return Component.literal(displayName).withStyle(chatColor);
    }
}
