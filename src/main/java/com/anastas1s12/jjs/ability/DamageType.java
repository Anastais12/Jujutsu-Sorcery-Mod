package com.anastas1s12.jjs.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Types of damage that abilities can deal.
 * Each has a display name and color for the UI.
 */
public enum DamageType {

    PHYSICAL("Physical", ChatFormatting.WHITE, 0xFFFFFFFF),
    SLASHING("Slashing", ChatFormatting.RED, 0xFFFF5252),
    BLUNT("Blunt", ChatFormatting.GRAY, 0xFF9E9E9E),
    PIERCING("Piercing", ChatFormatting.YELLOW, 0xFFFFD740),
    EXPLOSION("Explosion", ChatFormatting.GOLD, 0xFFFF6D00),
    VOID("Void", ChatFormatting.DARK_PURPLE, 0xFF7B1FA2),
    CURSED("Cursed", ChatFormatting.DARK_GREEN, 0xFF388E3C),
    SPATIAL("Spatial", ChatFormatting.BLUE, 0xFF2979FF),
    TRUE("True", ChatFormatting.DARK_RED, 0xFFB71C1C);

    private final String displayName;
    private final ChatFormatting chatColor;
    private final int color;

    DamageType(String displayName, ChatFormatting chatColor, int color) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public Component getDisplayComponent() {
        return Component.literal(displayName).withStyle(chatColor);
    }
}
