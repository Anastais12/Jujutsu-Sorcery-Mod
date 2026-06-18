package com.anastas1s12.jjs.system.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Sorcerer grade system from Jujutsu Kaisen.
 * Determines the player's rank, title color, and unlocks.
 */
public enum Grade {

    GRADE_4("Grade 4 Sorcerer", ChatFormatting.DARK_RED, 0xFF9E9E9E, 0),
    GRADE_3("Grade 3 Sorcerer", ChatFormatting.YELLOW, 0xFFFFFFFF, 10),
    GRADE_2("Grade 2 Sorcerer", ChatFormatting.DARK_GREEN, 0xFF4CAF50, 25),
    GRADE_1("Grade 1 Sorcerer", ChatFormatting.GREEN, 0xFF00BCD4, 50),
    SPECIAL_GRADE("Special Grade Sorcerer", ChatFormatting.AQUA, 0xFFFFD700, 100);

    private final String title;
    private final ChatFormatting chatColor;
    private final int color;
    private final int minMastery;

    Grade(String title, ChatFormatting chatColor, int color, int minMastery) {
        this.title = title;
        this.chatColor = chatColor;
        this.color = color;
        this.minMastery = minMastery;
    }

    public String getTitle() {
        return title;
    }

    public ChatFormatting getChatColor() {
        return chatColor;
    }

    public int getColor() {
        return color;
    }

    public int getMinMastery() {
        return minMastery;
    }

    public Component getTitleComponent() {
        return Component.literal(title).withStyle(chatColor);
    }

    /**
     * Get the grade for a given mastery level.
     */
    public static Grade fromMastery(int masteryLevel) {
        Grade result = GRADE_4;
        for (Grade g : values()) {
            if (masteryLevel >= g.minMastery) {
                result = g;
            }
        }
        return result;
    }
}
