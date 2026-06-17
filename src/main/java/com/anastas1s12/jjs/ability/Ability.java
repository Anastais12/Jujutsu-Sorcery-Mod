package com.anastas1s12.jjs.ability;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class representing a single ability in the player's technique.
 * Stores all info needed for rendering in the UI: name, type, cost, cooldown, etc.
 * This is a pure data container — actual behavior logic lives elsewhere.
 */
public class Ability {

    private final String id;
    private final String name;
    private final AbilityType type;
    private final ResourceLocation icon;
    private final ResourceLocation previewVideo;
    private final float ceCost;
    private final int cooldownSeconds;
    private final DamageType damageType;
    private final List<String> description;
    private final List<String> requirements;
    private boolean unlocked;
    private int hotbarSlot;

    /**
     * Builder-style constructor. Use Ability.Builder to create instances cleanly.
     */
    public Ability(String id, String name, AbilityType type,
                   ResourceLocation icon, ResourceLocation previewVideo,
                   float ceCost, int cooldownSeconds, DamageType damageType,
                   List<String> description, List<String> requirements) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.previewVideo = previewVideo;
        this.ceCost = ceCost;
        this.cooldownSeconds = cooldownSeconds;
        this.damageType = damageType;
        this.description = description;
        this.requirements = requirements;
        this.unlocked = false;
        this.hotbarSlot = -1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AbilityType getType() {
        return type;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public ResourceLocation getPreviewVideo() {
        return previewVideo;
    }

    public float getCeCost() {
        return ceCost;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public int getHotbarSlot() {
        return hotbarSlot;
    }


    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public void setHotbarSlot(int slot) {
        this.hotbarSlot = slot;
    }

    /**
     * Returns a formatted CE cost string for display.
     */
    public String getCostDisplay() {
        if (ceCost <= 0) return "Passive";
        if (ceCost == (int) ceCost) return String.format("%d CE", (int) ceCost);
        return String.format("%.1f CE", ceCost);
    }

    /**
     * Returns formatted cooldown string.
     */
    public String getCooldownDisplay() {
        if (cooldownSeconds <= 0) return "No cooldown";
        if (cooldownSeconds < 60) return cooldownSeconds + "s";
        int mins = cooldownSeconds / 60;
        int secs = cooldownSeconds % 60;
        return secs > 0 ? mins + "m " + secs + "s" : mins + "m";
    }


    /**
     * Builder for clean Ability construction.
     *
     * Example usage:
     * Ability blue = new Ability.Builder("limitless_blue", "Limitless: Blue", AbilityType.ADVANCED)
     *     .ceCost(40f).cooldown(8).damageType(DamageType.SPATIAL)
     *     .description("Creates a singularity that pulls enemies toward a focal point.")
     *     .requirement("Mastery Level 15")
     *     .build();
     */
    public static class Builder {
        private final String id;
        private final String name;
        private final AbilityType type;
        private ResourceLocation icon;
        private ResourceLocation previewVideo;
        private float ceCost = 0;
        private int cooldownSeconds = 0;
        private DamageType damageType = DamageType.PHYSICAL;
        private final List<String> description = new ArrayList<>();
        private final List<String> requirements = new ArrayList<>();

        // Default icon location — override if you use custom paths
        private static final String DEFAULT_ICON_PATH = "textures/gui/abilities/";
        private static final String DEFAULT_VIDEO_PATH = "videos/abilities/";

        public Builder(String id, String name, AbilityType type) {
            this.id = id;
            this.name = name;
            this.type = type;
            // Auto-generate default icon/video paths from the ID
            this.icon = ResourceLocation.fromNamespaceAndPath("jjs", DEFAULT_ICON_PATH + id + ".png");
            this.previewVideo = ResourceLocation.fromNamespaceAndPath("jjs", DEFAULT_VIDEO_PATH + id + ".mp4");
        }

        public Builder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder previewVideo(ResourceLocation video) {
            this.previewVideo = video;
            return this;
        }

        public Builder ceCost(float cost) {
            this.ceCost = cost;
            return this;
        }

        public Builder cooldown(int seconds) {
            this.cooldownSeconds = seconds;
            return this;
        }

        public Builder damageType(DamageType dt) {
            this.damageType = dt;
            return this;
        }

        public Builder description(String line) {
            this.description.add(line);
            return this;
        }

        public Builder requirement(String req) {
            this.requirements.add(req);
            return this;
        }

        public Ability build() {
            return new Ability(id, name, type, icon, previewVideo, ceCost, cooldownSeconds,
                    damageType, description, requirements);
        }
    }
}
