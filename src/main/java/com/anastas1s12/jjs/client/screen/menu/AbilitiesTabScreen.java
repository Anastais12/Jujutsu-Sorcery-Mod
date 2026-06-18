package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.ability.Ability;
import com.anastas1s12.jjs.ability.AbilityType;
import com.anastas1s12.jjs.ability.DamageType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * =============================================================================
 * ABILITIES TAB SCREEN — The Main Ability Browser
 * =============================================================================
 *
 * Uses the new full-screen per-tab background from BaseMenuScreen.
 * =============================================================================
 */
public class AbilitiesTabScreen extends BaseMenuScreen {

    /** Hotbar slot background texture */
    public static final ResourceLocation HOTBAR_SLOT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/hotbar_slot.png");

    /** Locked ability overlay (red X or dim) */
    public static final ResourceLocation LOCKED_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(JujutsuSorcery.MOD_ID, "textures/gui/menu/locked_overlay.png");

    /** Width of the ability list area (left side of content) */
    private static final int LIST_WIDTH = 195;
    /** Width of the detail panel (right side of content) */
    private static final int DETAIL_WIDTH = 150;
    /** Height of each section header */
    private static final int SECTION_HEADER_HEIGHT = 16;
    /** Size of each ability icon */
    private static final int ICON_SIZE = 28;
    /** Gap between ability icons */
    private static final int ICON_GAP = 4;
    /** Icons per row in a section */
    private static final int ICONS_PER_ROW = 5;
    /** Height of the hotbar area at the bottom */
    private static final int HOTBAR_HEIGHT = 45;
    /** Size of each hotbar slot */
    private static final int SLOT_SIZE = 32;

    // ============================================================
    // DATA — REPLACE WITH ACTUAL PLAYER ABILITY DATA
    // ============================================================

    /** The currently selected ability (shown in detail panel) */
    private Ability selectedAbility = null;

    /** Abilities assigned to the 9 hotbar slots. null = empty slot. */
    private final Ability[] hotbarAbilities = new Ability[9];

    /** Track which section is expanded/collapsed */
    private final boolean[] sectionExpanded = {true, true, false, false};

    /** All abilities available to the player — populate from your data system */
    private final List<Ability> allAbilities = new ArrayList<>();

    /** Clickable areas for ability icons (recalculated each frame) */
    private final List<AbilityClickArea> abilityClickAreas = new ArrayList<>();

    /** Clickable areas for hotbar slots */
    private final List<HotbarSlotArea> hotbarSlotAreas = new ArrayList<>();

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AbilitiesTabScreen() {
        super(Tab.ABILITIES);

        // ============================================================
        // EXAMPLE ABILITIES — Replace this with your actual data!
        // ============================================================

        allAbilities.add(new Ability.Builder("punch", "Cursed Punch", AbilityType.BASIC)
                .ceCost(5f).cooldown(0).damageType(DamageType.BLUNT)
                .description("A basic punch infused with cursed energy.")
                .description("Low cost, spammable. Damage scales with CE Output.")
                .requirement("None — starting ability")
                .build());
        allAbilities.get(0).setUnlocked(true);

        allAbilities.add(new Ability.Builder("divergent_fist", "Divergent Fist", AbilityType.BASIC)
                .ceCost(12f).cooldown(2).damageType(DamageType.BLUNT)
                .description("Strike with cursed energy delayed by 0.5 seconds.")
                .description("Deals damage twice — once on impact, once delayed.")
                .requirement("Mastery Level 3")
                .build());
        allAbilities.get(1).setUnlocked(true);

        allAbilities.add(new Ability.Builder("black_flash", "Black Flash", AbilityType.BASIC)
                .ceCost(25f).cooldown(5).damageType(DamageType.CURSED)
                .description("A spatial distortion that multiplies damage by 2.5x.")
                .description("Requires precise timing. Can chain for higher damage.")
                .requirement("Mastery Level 10")
                .build());
        allAbilities.get(2).setUnlocked(true);

        allAbilities.add(new Ability.Builder("limitless_blue", "Limitless: Blue", AbilityType.ADVANCED)
                .ceCost(40f).cooldown(8).damageType(DamageType.SPATIAL)
                .description("Creates a singularity that violently pulls")
                .description("enemies toward a focal point.")
                .requirement("Innate: Limitless Technique")
                .requirement("Mastery Level 15")
                .build());
        allAbilities.get(3).setUnlocked(true);

        allAbilities.add(new Ability.Builder("limitless_red", "Limitless: Red", AbilityType.ADVANCED)
                .ceCost(50f).cooldown(12).damageType(DamageType.SPATIAL)
                .description("The reversal of Blue. Creates an explosive")
                .description("repulsive force that blasts enemies away.")
                .requirement("Innate: Limitless Technique")
                .requirement("Mastery Level 25")
                .requirement("Unlock Reverse Cursed Technique")
                .build());

        allAbilities.add(new Ability.Builder("hollow_purple", "Hollow Purple", AbilityType.SPECIAL_MOVE)
                .ceCost(200f).cooldown(300).damageType(DamageType.VOID)
                .description("The fusion of Blue and Red. Fires a massive")
                .description("sphere of annihilation that erases everything.")
                .description("Ultimate technique of the Limitless.")
                .requirement("Innate: Limitless Technique")
                .requirement("Mastery Level 100 (Special Grade)")
                .requirement("Six Eyes activated")
                .build());

        allAbilities.add(new Ability.Builder("infinite_void", "Infinite Void", AbilityType.DOMAIN)
                .ceCost(500f).cooldown(600).damageType(DamageType.TRUE)
                .description("Domain Expansion: traps the target in an")
                .description("infinite stream of information, paralyzing them.")
                .description("Guarantees the next hit lands.")
                .requirement("Innate: Limitless Technique")
                .requirement("Mastery Level 50")
                .requirement("Domain Stability > 60%")
                .build());

        // Assign some example abilities to hotbar
        hotbarAbilities[0] = allAbilities.get(0); // Cursed Punch
        hotbarAbilities[1] = allAbilities.get(1); // Divergent Fist
        hotbarAbilities[2] = allAbilities.get(3); // Blue
    }

    // ============================================================
    // MAIN CONTENT RENDER
    // ============================================================

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        abilityClickAreas.clear();
        hotbarSlotAreas.clear();

        // ---- LEFT: Ability List with 4 Sections ----
        renderAbilityList(graphics, mouseX, mouseY);

        // ---- RIGHT: Ability Detail Panel ----
        if (selectedAbility != null) {
            renderAbilityDetail(graphics, mouseX, mouseY);
        } else {
            renderEmptyDetail(graphics);
        }

        // ---- BOTTOM: 9 Hotbar Slots ----
        renderHotbar(graphics, mouseX, mouseY);
    }

    // ============================================================
    // ABILITY LIST — 4 Sections
    // ============================================================

    private void renderAbilityList(GuiGraphics graphics, int mouseX, int mouseY) {
        int listX = contentLeft();
        int listY = contentTop();
        int currentY = listY;

        AbilityType[] types = AbilityType.values();
        for (int i = 0; i < types.length; i++) {
            AbilityType type = types[i];

            boolean expanded = sectionExpanded[i];
            boolean headerHovered = mouseX >= listX && mouseX < listX + LIST_WIDTH
                    && mouseY >= currentY && mouseY < currentY + SECTION_HEADER_HEIGHT;

            renderSectionHeader(graphics, listX, currentY, LIST_WIDTH, type, expanded, headerHovered);

            if (headerHovered) {
                tooltipText = expanded ? "Click to collapse" : "Click to expand";
                tooltipX = mouseX;
                tooltipY = mouseY - 15;
            }

            currentY += SECTION_HEADER_HEIGHT;

            if (expanded) {
                List<Ability> sectionAbilities = getAbilitiesByType(type);
                if (!sectionAbilities.isEmpty()) {
                    int sectionHeight = renderAbilityGrid(graphics, listX, currentY,
                            LIST_WIDTH, sectionAbilities, mouseX, mouseY);
                    currentY += sectionHeight;
                } else {
                    graphics.drawString(this.font, "No abilities unlocked...",
                            listX + 5, currentY + 5, 0xFF666666, false);
                    currentY += 20;
                }
            }

            currentY += 3;
        }
    }

    private void renderSectionHeader(GuiGraphics graphics, int x, int y, int width,
                                     AbilityType type, boolean expanded, boolean hovered) {
        int bgColor = hovered ? 0xFF333333 : 0xFF222222;
        graphics.fill(x, y, x + width, y + SECTION_HEADER_HEIGHT, bgColor);

        graphics.fill(x, y, x + 3, y + SECTION_HEADER_HEIGHT, type.getColor());

        String arrow = expanded ? "v" : ">";
        graphics.drawString(this.font, arrow, x + 6, y + 4, 0xFFFFFFFF, false);

        String label = type.getDisplayName() + " Abilities";
        graphics.drawString(this.font, label, x + 18, y + 4, type.getColor(), false);

        int count = (int) allAbilities.stream().filter(a -> a.getType() == type).count();
        int unlocked = (int) allAbilities.stream()
                .filter(a -> a.getType() == type && a.isUnlocked()).count();
        String countStr = unlocked + "/" + count;
        int countWidth = this.font.width(countStr);
        graphics.drawString(this.font, countStr, x + width - countWidth - 4, y + 4,
                0xFFAAAAAA, false);

        graphics.hLine(x, x + width, y + SECTION_HEADER_HEIGHT - 1, type.getColor() & 0xFF444444);
    }

    private int renderAbilityGrid(GuiGraphics graphics, int x, int y, int width,
                                  List<Ability> abilities, int mouseX, int mouseY) {
        int startY = y;
        int iconX = x + 4;
        int iconY = y + 4;
        int count = 0;

        for (Ability ability : abilities) {
            if (count > 0 && count % ICONS_PER_ROW == 0) {
                iconX = x + 4;
                iconY += ICON_SIZE + ICON_GAP;
            }

            boolean hovered = mouseX >= iconX && mouseX < iconX + ICON_SIZE
                    && mouseY >= iconY && mouseY < iconY + ICON_SIZE;

            renderAbilityIcon(graphics, ability, iconX, iconY, hovered);

            abilityClickAreas.add(new AbilityClickArea(ability, iconX, iconY, ICON_SIZE, ICON_SIZE));

            if (hovered) {
                tooltipText = ability.getName() + " (" + ability.getCostDisplay() + ")";
                tooltipX = mouseX;
                tooltipY = mouseY - 15;
            }

            iconX += ICON_SIZE + ICON_GAP;
            count++;
        }

        int rows = (int) Math.ceil((double) count / ICONS_PER_ROW);
        return rows * (ICON_SIZE + ICON_GAP) + 8;
    }

    private void renderAbilityIcon(GuiGraphics graphics, Ability ability, int x, int y, boolean hovered) {
        boolean isSelected = selectedAbility == ability;

        int bgColor = isSelected ? 0xFF444444 : (hovered ? 0xFF333333 : 0xFF222222);
        graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, bgColor);

        int borderColor;
        if (isSelected) {
            borderColor = 0xFFFFFFFF;
        } else if (ability.isUnlocked()) {
            borderColor = ability.getType().getColor();
        } else {
            borderColor = 0xFF555555;
        }
        graphics.renderOutline(x, y, ICON_SIZE, ICON_SIZE, borderColor);

        int iconInnerX = x + 2;
        int iconInnerY = y + 2;
        int iconInnerSize = ICON_SIZE - 4;

        if (ability.isUnlocked()) {
            graphics.fill(iconInnerX, iconInnerY,
                    iconInnerX + iconInnerSize, iconInnerY + iconInnerSize,
                    ability.getDamageType().getColor() & 0xFF999999);

            int dotColor = ability.getType().getColor();
            graphics.fill(x + ICON_SIZE - 6, y + 1, x + ICON_SIZE - 1, y + 6, dotColor);
        } else {
            graphics.fill(iconInnerX, iconInnerY,
                    iconInnerX + iconInnerSize, iconInnerY + iconInnerSize,
                    0xFF333333);
            graphics.drawString(this.font, "X", x + 9, y + 8, 0xFFFF0000, false);
        }

        if (hovered && ability.isUnlocked()) {
            graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x20FFFFFF);
        }
    }

    // ============================================================
    // ABILITY DETAIL PANEL
    // ============================================================

    private void renderAbilityDetail(GuiGraphics graphics, int mouseX, int mouseY) {
        Ability a = selectedAbility;
        int detailX = contentLeft() + LIST_WIDTH + 8;
        int detailY = contentTop();
        int detailW = DETAIL_WIDTH;
        int maxDetailH = BG_HEIGHT - CONTENT_TOP - HOTBAR_HEIGHT - 15;

        graphics.fill(detailX, detailY, detailX + detailW, detailY + maxDetailH, 0xCC1A1A1A);
        graphics.renderOutline(detailX, detailY, detailW, maxDetailH, 0xFF555555);

        int bannerHeight = 22;
        graphics.fill(detailX + 1, detailY + 1,
                detailX + detailW - 1, detailY + bannerHeight, a.getType().getBannerColor());

        String typeName = a.getType().getDisplayName().toUpperCase();
        int typeNameWidth = this.font.width(typeName);
        graphics.drawString(this.font, typeName,
                detailX + (detailW - typeNameWidth) / 2,
                detailY + 7, 0xFFFFFFFF, true);

        int textY = detailY + bannerHeight + 6;
        graphics.drawString(this.font,
                Component.literal(a.getName()).withStyle(ChatFormatting.BOLD),
                detailX + 5, textY, 0xFFFFFFFF, false);
        textY += 14;

        graphics.hLine(detailX + 5, detailX + detailW - 5, textY, 0xFF444444);
        textY += 6;

        renderDetailStat(graphics, detailX + 5, textY, "CE Cost:", a.getCostDisplay(), 0xFF00E676);
        textY += 14;

        renderDetailStat(graphics, detailX + 5, textY, "Cooldown:", a.getCooldownDisplay(), 0xFFFFD700);
        textY += 14;

        renderDetailStat(graphics, detailX + 5, textY, "Damage:",
                a.getDamageType().getDisplayName(), a.getDamageType().getColor());
        textY += 18;

        graphics.hLine(detailX + 5, detailX + detailW - 5, textY, 0xFF444444);
        textY += 6;

        graphics.drawString(this.font, "Description:", detailX + 5, textY, 0xFFAAAAAA, false);
        textY += 12;

        for (String line : a.getDescription()) {
            List<String> wrapped = wrapText(line, detailW - 10);
            for (String wrappedLine : wrapped) {
                graphics.drawString(this.font, wrappedLine, detailX + 8, textY, 0xFFCCCCCC, false);
                textY += 11;
            }
        }

        textY += 4;

        if (!a.getRequirements().isEmpty()) {
            graphics.hLine(detailX + 5, detailX + detailW - 5, textY, 0xFF444444);
            textY += 6;
            graphics.drawString(this.font, "Requirements:", detailX + 5, textY, 0xFFAAAAAA, false);
            textY += 12;

            for (String req : a.getRequirements()) {
                boolean met = isRequirementMet(req);
                int color = met ? 0xFF4CAF50 : 0xFFFF5252;
                String prefix = met ? "+ " : "- ";

                List<String> wrapped = wrapText(prefix + req, detailW - 10);
                for (String wrappedLine : wrapped) {
                    graphics.drawString(this.font, wrappedLine, detailX + 8, textY, color, false);
                    textY += 11;
                }
            }
        }

        textY += 6;
        String previewHint = "Press [P] for Preview";
        int hintWidth = this.font.width(previewHint);
        int hintX = detailX + (detailW - hintWidth) / 2;

        float pulse = (float) (Math.sin(System.currentTimeMillis() / 300.0) * 0.3 + 0.7);
        int hintAlpha = (int) (pulse * 255) << 24;
        graphics.drawString(this.font, previewHint, hintX, textY,
                hintAlpha | 0xFFAAAA, false);
    }

    private void renderEmptyDetail(GuiGraphics graphics) {
        int detailX = contentLeft() + LIST_WIDTH + 8;
        int detailY = contentTop();
        int detailW = DETAIL_WIDTH;
        int detailH = BG_HEIGHT - CONTENT_TOP - HOTBAR_HEIGHT - 15;

        graphics.fill(detailX, detailY, detailX + detailW, detailY + detailH, 0xCC1A1A1A);
        graphics.renderOutline(detailX, detailY, detailW, detailH, 0xFF444444);

        String msg = "Select an ability";
        int msgWidth = this.font.width(msg);
        int msgX = detailX + (detailW - msgWidth) / 2;
        int msgY = detailY + detailH / 2;
        graphics.drawString(this.font, msg, msgX, msgY, 0xFF666666, false);

        String subMsg = "to view details";
        int subWidth = this.font.width(subMsg);
        graphics.drawString(this.font, subMsg,
                detailX + (detailW - subWidth) / 2, msgY + 12, 0xFF555555, false);
    }

    private void renderDetailStat(GuiGraphics graphics, int x, int y, String label, String value, int valueColor) {
        graphics.drawString(this.font, label, x, y, 0xFF999999, false);
        int valueWidth = this.font.width(value);
        graphics.drawString(this.font, value, x + 140 - valueWidth, y, valueColor, false);
    }

    // ============================================================
    // HOTBAR
    // ============================================================

    private void renderHotbar(GuiGraphics graphics, int mouseX, int mouseY) {
        int hotbarY = panelTop + BG_HEIGHT - HOTBAR_HEIGHT - 5;
        int totalWidth = 9 * SLOT_SIZE + 8 * 3;
        int hotbarX = panelLeft + (BG_WIDTH - totalWidth) / 2;

        graphics.drawString(this.font, "Ability Hotbar",
                hotbarX, hotbarY - 12, 0xFFAAAAAA, false);

        for (int i = 0; i < 9; i++) {
            int slotX = hotbarX + i * (SLOT_SIZE + 3);
            boolean hovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                    && mouseY >= hotbarY && mouseY < hotbarY + SLOT_SIZE;

            Ability ability = hotbarAbilities[i];

            int bgColor = hovered ? 0xFF3A3A3A : 0xFF222222;
            graphics.fill(slotX, hotbarY, slotX + SLOT_SIZE, hotbarY + SLOT_SIZE, bgColor);
            graphics.renderOutline(slotX, hotbarY, SLOT_SIZE, SLOT_SIZE,
                    hovered ? 0xFFFFFFFF : 0xFF555555);

            graphics.drawString(this.font, String.valueOf(i + 1),
                    slotX + 2, hotbarY + 1, 0xFF666666, false);

            if (ability != null) {
                int iconX = slotX + 4;
                int iconY = hotbarY + 4;
                int iconS = SLOT_SIZE - 8;

                graphics.fill(iconX, iconY, iconX + iconS, iconY + iconS,
                        ability.getDamageType().getColor() & 0xFF777777);

                String cost = ability.getCostDisplay();
                int costWidth = this.font.width(cost);
                graphics.drawString(this.font, cost,
                        slotX + (SLOT_SIZE - costWidth) / 2,
                        hotbarY + SLOT_SIZE - 9, 0xFF00E676, false);

                if (hovered) {
                    tooltipText = ability.getName() + " — " + ability.getCostDisplay();
                    tooltipX = mouseX;
                    tooltipY = mouseY - 15;
                }
            }

            hotbarSlotAreas.add(new HotbarSlotArea(i, slotX, hotbarY, SLOT_SIZE, SLOT_SIZE));
        }
    }

    // ============================================================
    // INPUT HANDLING
    // ============================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbilityClickArea area : abilityClickAreas) {
            if (area.contains((int) mouseX, (int) mouseY)) {
                if (area.ability.isUnlocked()) {
                    selectedAbility = area.ability;
                }
                return true;
            }
        }

        if (selectedAbility != null) {
            for (HotbarSlotArea area : hotbarSlotAreas) {
                if (area.contains((int) mouseX, (int) mouseY)) {
                    assignToHotbar(selectedAbility, area.slot);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_P && selectedAbility != null) {
            openVideoPreview(selectedAbility);
            return true;
        }

        if (selectedAbility != null && keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int slot = keyCode - GLFW.GLFW_KEY_1;
            assignToHotbar(selectedAbility, slot);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ============================================================
    // ACTIONS
    // ============================================================

    private void assignToHotbar(Ability ability, int slot) {
        for (int i = 0; i < 9; i++) {
            if (hotbarAbilities[i] == ability) {
                hotbarAbilities[i] = null;
            }
        }
        if (hotbarAbilities[slot] == ability) {
            hotbarAbilities[slot] = null;
            ability.setHotbarSlot(-1);
        } else {
            hotbarAbilities[slot] = ability;
            ability.setHotbarSlot(slot);
        }
    }

    private void openVideoPreview(Ability ability) {
        Minecraft mc = Minecraft.getInstance();
        mc.pushGuiLayer(new VideoPopupScreen(this, ability)); // Make sure this class exists
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    private List<Ability> getAbilitiesByType(AbilityType type) {
        List<Ability> result = new ArrayList<>();
        for (Ability a : allAbilities) {
            if (a.getType() == type) {
                result.add(a);
            }
        }
        return result;
    }

    private boolean isRequirementMet(String requirement) {
        if (requirement.contains("None")) return true;
        if (requirement.contains("Mastery Level")) {
            try {
                int required = Integer.parseInt(requirement.replaceAll("\\D+", ""));
                return com.anastas1s12.jjs.client.ClientCEData.getMasteryLevel() >= required;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split(" ")) {
            String test = currentLine.length() > 0 ? currentLine + " " + word : word;
            if (this.font.width(test) > maxWidth && currentLine.length() > 0) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(test);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    // ============================================================
    // INNER CLASSES
    // ============================================================

    private static class AbilityClickArea {
        final Ability ability;
        final int x, y, width, height;

        AbilityClickArea(Ability ability, int x, int y, int width, int height) {
            this.ability = ability;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        boolean contains(int mx, int my) {
            return mx >= x && mx < x + width && my >= y && my < y + height;
        }
    }

    private static class HotbarSlotArea {
        final int slot;
        final int x, y, width, height;

        HotbarSlotArea(int slot, int x, int y, int width, int height) {
            this.slot = slot;
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