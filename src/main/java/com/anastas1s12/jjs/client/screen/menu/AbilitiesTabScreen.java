package com.anastas1s12.jjs.client.screen.menu;

import com.anastas1s12.jjs.system.ability.Ability;
import com.anastas1s12.jjs.system.ability.AbilityType;
import com.anastas1s12.jjs.client.ClientAbilityData;
import com.anastas1s12.jjs.client.ClientCEData;
import com.anastas1s12.jjs.client.ClientTechniqueData;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.c2s.AssignAbilityToHotbarC2SPacket;
import com.anastas1s12.jjs.system.technique.Technique;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AbilitiesTabScreen extends AbstractMenuScreen {

    private static final int TAB_INDEX = 0;

    // ── Texture-space row coordinates ─────────────────────────────────────────
    private static final int TX_ROW_X   =  94;
    private static final int TY_ROW_0   =  52;
    private static final int TX_ROW_W   = 324;
    private static final int TY_ROW_H   =  36;
    private static final int TY_ROW_GAP =   6;

    // ── Row → AbilityType mapping (order must match the texture rows) ─────────
    private static final AbilityType[] ROW_TYPES = {
        AbilityType.BASIC,
        AbilityType.ADVANCED,
        AbilityType.SPECIAL_MOVE,
        AbilityType.DOMAIN,
    };

    // Per-row accent colours
    private static final int[] ROW_GLOW = { 0xFF2979FF, 0xFFAA00FF, 0xFFFFAB00, 0xFFDD2222 };

    // ── Ability card dimensions (screen pixels; derived in init) ──────────────
    // Each row shows cards side-by-side; all cards in a row have the same height
    // (= row height - 4px padding) and a fixed width.
    private static final int CARD_W_TEX = 54; // ← card width in texture pixels  (adjust freely)
    private static final int CARD_GAP   =  3; // ← gap between cards in texture pixels

    // ── Card colors ───────────────────────────────────────────────────────────
    private static final int CARD_BG          = 0xCC050510;
    private static final int CARD_BG_HOVER    = 0xCC0A0A22;
    private static final int CARD_BG_SELECTED = 0xCC0D1A33;
    private static final int CARD_ASSIGNED_TINT = 0x4400AA44; // green tint if in hotbar

    // ── Text colors ───────────────────────────────────────────────────────────
    private static final int COLOR_DETAIL_TEXT  = 0xFFCCCCCC;
    private static final int COLOR_DETAIL_LABEL = 0xFF888899;
    private static final int COLOR_REQ_MET      = 0xFF44BB55;
    private static final int COLOR_REQ_UNMET    = 0xFFBB3333;
    private static final int COLOR_NO_TECHNIQUE = 0xFF666677;
    private static final int COLOR_HOTBAR_HINT  = 0xFF556688;

    // ── State ─────────────────────────────────────────────────────────────────
    /** Abilities from the player's technique, grouped by type. */
    private final Map<AbilityType, List<Ability>> abilityMap = new EnumMap<>(AbilityType.class);

    /** The currently selected ability, or null. */
    private Ability selectedAbility = null;

    // ── Computed screen-space row bounds ──────────────────────────────────────
    private final int[] rowScreenX = new int[ROW_TYPES.length];
    private final int[] rowScreenY = new int[ROW_TYPES.length];
    private int rowScreenW, rowScreenH;
    // Card dimensions in screen pixels (computed in init)
    private int cardScreenW, cardScreenH, cardScreenGap;

    // Ability icon source size
    private static final int SOURCE_ICON_W = 16;
    private static final int SOURCE_ICON_H = 16;

    // =========================================================================
    // Constructor
    // =========================================================================

    public AbilitiesTabScreen() {
        super(Component.literal("Abilities"), TAB_INDEX, TEX_ABILITIES);
        for (AbilityType t : ROW_TYPES) abilityMap.put(t, new ArrayList<>());
        loadAbilitiesFromTechnique();
    }

    // =========================================================================
    // Init
    // =========================================================================

    @Override
    protected void init() {
        super.init();

        rowScreenW = toScreenW(TX_ROW_W);
        rowScreenH = toScreenH(TY_ROW_H);
        for (int i = 0; i < ROW_TYPES.length; i++) {
            rowScreenX[i] = toScreenX(TX_ROW_X);
            rowScreenY[i] = toScreenY(TY_ROW_0 + i * (TY_ROW_H + TY_ROW_GAP));
        }

        cardScreenW   = toScreenW(CARD_W_TEX);
        cardScreenH   = rowScreenH - toScreenH(4); // 2px padding top + bottom
        cardScreenGap = toScreenW(CARD_GAP);
    }

    // =========================================================================
    // Load abilities from the current technique
    // =========================================================================

    private void loadAbilitiesFromTechnique() {
        for (AbilityType t : ROW_TYPES) abilityMap.get(t).clear();

        for (Ability ability : ClientTechniqueData.getTechniqueAbilities()) {
            // Work with mutable copies so we can set hotbarSlot without
            // mutating the shared registry instance.
            Ability copy = new Ability(
                    ability.getId(), ability.getName(), ability.getType(),
                    ability.getIcon(), ability.getPreviewVideo(),
                    ability.getCeCost(), ability.getCooldownSeconds(),
                    ability.getDamageType(),
                    ability.getDescription(), ability.getRequirements());
            copy.setUnlocked(true);
            copy.setHotbarSlot(findHotbarSlot(ability.getId()));

            List<Ability> bucket = abilityMap.get(ability.getType());
            if (bucket != null) bucket.add(copy);
        }
    }

    /** Returns the hotbar slot that contains {@code abilityId}, or -1. */
    private int findHotbarSlot(String abilityId) {
        for (int i = 0; i < ClientAbilityData.HOTBAR_SLOTS; i++) {
            if (abilityId.equals(ClientAbilityData.getSlotId(i))) return i;
        }
        return -1;
    }

    /**
     * Refreshes the hotbarSlot field on every card copy after a slot assignment
     * changes so the UI stays in sync without re-opening the screen.
     */
    private void refreshHotbarSlots() {
        for (List<Ability> list : abilityMap.values()) {
            for (Ability a : list) {
                a.setHotbarSlot(findHotbarSlot(a.getId()));
            }
        }
        // Also refresh the selected ability reference if it is one of the copies
        if (selectedAbility != null) {
            selectedAbility.setHotbarSlot(findHotbarSlot(selectedAbility.getId()));
        }
    }

    // =========================================================================
    // Content rendering
    // =========================================================================

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTechniqueHeader(graphics);
        renderAllRows(graphics, mouseX, mouseY);
        renderLeftSidebar(graphics);
        renderRightSidebar(graphics);
        renderHotbarSlots(graphics, mouseX, mouseY);
    }

    // ── Technique header ──────────────────────────────────────────────────────

    private void renderTechniqueHeader(GuiGraphics graphics) {
        int hy = centerY + 4;
        if (!ClientTechniqueData.hasTechnique()) {
            String msg = "No technique assigned — use /jjs technique assign";
            graphics.drawString(font, msg,
                    centerX + (centerW - font.width(msg)) / 2, hy, COLOR_NO_TECHNIQUE, false);
        } else {
            Technique t = ClientTechniqueData.getTechnique();
            String name = t != null ? t.getName() : "Unknown";
            String header = "Technique: " + name;
            Integer rawColor = (t != null && t.getColor().getColor() != null)
                    ? t.getColor().getColor() | 0xFF000000 : 0xFFFFFFFF;
            graphics.drawString(font, header,
                    centerX + (centerW - font.width(header)) / 2, hy, rawColor, false);
        }
    }

    // ── Rows + cards ──────────────────────────────────────────────────────────

    private void renderAllRows(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int rowIdx = 0; rowIdx < ROW_TYPES.length; rowIdx++) {
            List<Ability> list = abilityMap.get(ROW_TYPES[rowIdx]);
            int rx = rowScreenX[rowIdx];
            int ry = rowScreenY[rowIdx];
            int rw = rowScreenW;
            int rh = rowScreenH;
            int glow = ROW_GLOW[rowIdx];

            // Row type label on the far left
            int labelY = ry + (rh - font.lineHeight) / 2;
            graphics.drawString(font, ROW_TYPES[rowIdx].getDisplayName().toUpperCase(),
                    rx + 4, labelY, glow, false);

            // Calculate where cards start (after the type label)
            int labelW  = font.width(ROW_TYPES[rowIdx].getDisplayName().toUpperCase()) + 8;
            int cardsStartX = rx + labelW + 4;
            int cardTop     = ry + toScreenH(2); // 2px top padding

            // Render each card
            for (int ci = 0; ci < list.size(); ci++) {
                Ability ability = list.get(ci);
                int cx = cardsStartX + ci * (cardScreenW + cardScreenGap);

                // Stop rendering if card would overflow the row width
                if (cx + cardScreenW > rx + rw - 4) break;

                renderAbilityCard(graphics, ability, cx, cardTop, mouseX, mouseY, glow);
            }
        }
    }

    private void renderAbilityCard(GuiGraphics graphics, Ability ability,
                                   int cx, int cy, int mouseX, int mouseY, int glow) {
        boolean isSelected = (ability == selectedAbility);
        boolean isHovered  = !isSelected && mouseX >= cx && mouseX < cx + cardScreenW
                && mouseY >= cy && mouseY < cy + cardScreenH;
        boolean isAssigned = ability.getHotbarSlot() >= 0;

        // Card background
        int bg = isSelected ? CARD_BG_SELECTED : (isHovered ? CARD_BG_HOVER : CARD_BG);
        // 1px glow border
        graphics.fill(cx - 1, cy - 1, cx + cardScreenW + 1, cy + cardScreenH + 1,
                isSelected ? glow : (isHovered ? (glow & 0x00FFFFFF | 0x66000000) : 0xFF222233));
        graphics.fill(cx, cy, cx + cardScreenW, cy + cardScreenH, bg);

        // Green tint overlay if ability is already in a hotbar slot
        if (isAssigned) {
            graphics.fill(cx, cy, cx + cardScreenW, cy + cardScreenH, CARD_ASSIGNED_TINT);
        }

        // Ability name — truncated to fit
        String name = ability.getName();
        int maxNameW = cardScreenW - 4;
        while (font.width(name) > maxNameW && name.length() > 3)
            name = name.substring(0, name.length() - 3) + "..";

        int nameColor = isSelected ? 0xFFFFFFFF : (glow | 0xFF000000);
        graphics.drawString(font, name,
                cx + (cardScreenW - font.width(name)) / 2,
                cy + (cardScreenH - font.lineHeight) / 2,
                nameColor, false);

        // Small slot badge in top-right corner of the card
        if (isAssigned) {
            String badge = "" + (ability.getHotbarSlot() + 1);
            graphics.drawString(font, badge,
                    cx + cardScreenW - font.width(badge) - 1, cy + 1,
                    0xFF88CCFF, false);
        }
    }

    // ── Left sidebar ──────────────────────────────────────────────────────────

    private void renderLeftSidebar(GuiGraphics graphics) {
        int cx = leftSidebarX + leftSidebarW / 2;

        if (selectedAbility == null) {
            String l1 = ClientTechniqueData.hasTechnique() ? "Click a" : "No";
            String l2 = ClientTechniqueData.hasTechnique() ? "technique card" : "technique";
            int midY = leftSidebarY + leftSidebarH / 2;
            graphics.drawString(font, l1, cx - font.width(l1) / 2, midY - font.lineHeight - 1, COLOR_DETAIL_LABEL, false);
            graphics.drawString(font, l2, cx - font.width(l2) / 2, midY + 2,                   COLOR_DETAIL_LABEL, false);
            return;
        }

        Ability a    = selectedAbility;
        int rowIdx   = getRowIndex(a.getType());
        int glow     = ROW_GLOW[rowIdx];
        int y        = leftSidebarY + 6;

        // Icon placeholder
        int iconSize = Math.min(leftSidebarW - 8, toScreenW(28));
        int iconX    = leftSidebarX + (leftSidebarW - iconSize) / 2;
        graphics.fill(iconX - 1, y - 1, iconX + iconSize + 1, y + iconSize + 1, glow);
        graphics.fill(iconX, y, iconX + iconSize, y + iconSize, 0xFF050510);
        // TODO: blit ability.getIcon() when textures exist:
        // graphics.blit(a.getIcon(), iconX, y, iconSize, iconSize,
        //               0, 0, SOURCE_ICON_W, SOURCE_ICON_H, SOURCE_ICON_W, SOURCE_ICON_H);
        y += iconSize + 5;

        // Name
        String name = a.getName();
        while (font.width(name) > leftSidebarW - 4 && name.length() > 4)
            name = name.substring(0, name.length() - 4) + "...";
        graphics.drawString(font, name, cx - font.width(name) / 2, y, glow, false);
        y += font.lineHeight + 4;

        // CE cost
        String costStr = a.getCostDisplay();
        graphics.drawString(font, costStr, cx - font.width(costStr) / 2, y, 0xFFAAAAAA, false);
        y += font.lineHeight + 4;

        // Current slot or "not assigned"
        if (a.getHotbarSlot() >= 0) {
            String s = "In slot " + (a.getHotbarSlot() + 1);
            graphics.drawString(font, s, cx - font.width(s) / 2, y, 0xFF66BBFF, false);
        } else {
            String s = "Not assigned";
            graphics.drawString(font, s, cx - font.width(s) / 2, y, COLOR_DETAIL_LABEL, false);
        }
        y += font.lineHeight + 8;

        // Hotbar assignment hint
        if (a.getHotbarSlot() < 0) {
            String hint1 = "Click a hotbar";
            String hint2 = "slot to assign";
            graphics.drawString(font, hint1, cx - font.width(hint1) / 2, y,      COLOR_HOTBAR_HINT, false);
            graphics.drawString(font, hint2, cx - font.width(hint2) / 2, y + 10, COLOR_HOTBAR_HINT, false);
        } else {
            String hint = "Right-click slot";
            String hint2 = "to unassign";
            graphics.drawString(font, hint,  cx - font.width(hint)  / 2, y,      COLOR_HOTBAR_HINT, false);
            graphics.drawString(font, hint2, cx - font.width(hint2) / 2, y + 10, COLOR_HOTBAR_HINT, false);
        }
    }

    // ── Right sidebar ─────────────────────────────────────────────────────────

    private void renderRightSidebar(GuiGraphics graphics) {
        int cx = rightSidebarX + rightSidebarW / 2;

        if (selectedAbility == null) {
            String l1 = "No ability", l2 = "selected";
            int midY = rightSidebarY + rightSidebarH / 2;
            graphics.drawString(font, l1, cx - font.width(l1) / 2, midY - font.lineHeight - 1, COLOR_DETAIL_LABEL, false);
            graphics.drawString(font, l2, cx - font.width(l2) / 2, midY + 2,                   COLOR_DETAIL_LABEL, false);
            return;
        }

        Ability a = selectedAbility;
        int x = rightSidebarX + 4;
        int y = rightSidebarY + 6;
        int mw = rightSidebarW - 8;

        drawStat(graphics, x, y, mw, "Cost",     a.getCostDisplay());                  y += 12;
        drawStat(graphics, x, y, mw, "Cooldown", a.getCooldownDisplay());               y += 12;
        drawStat(graphics, x, y, mw, "Damage",   a.getDamageType().getDisplayName());   y += 14;

        graphics.fill(x, y, x + mw, y + 1, 0xFF222233);
        y += 5;

        for (String line : a.getDescription()) {
            if (y + font.lineHeight > rightSidebarY + rightSidebarH - 8) break;
            graphics.drawString(font, line, x, y, COLOR_DETAIL_TEXT, false);
            y += font.lineHeight + 1;
        }
        y += 3;

        if (!a.getRequirements().isEmpty()) {
            graphics.fill(x, y, x + mw, y + 1, 0xFF222233);
            y += 4;
            for (String req : a.getRequirements()) {
                if (y + font.lineHeight > rightSidebarY + rightSidebarH - 4) break;
                boolean met   = isRequirementMet(req);
                String prefix = met ? "\u2714 " : "\u2718 ";
                graphics.drawString(font, prefix + req, x, y, met ? COLOR_REQ_MET : COLOR_REQ_UNMET, false);
                y += font.lineHeight + 2;
            }
        }
    }

    private void drawStat(GuiGraphics g, int x, int y, int mw, String label, String value) {
        g.drawString(font, label, x,                           y, COLOR_DETAIL_LABEL, false);
        g.drawString(font, value, x + mw - font.width(value), y, COLOR_DETAIL_TEXT,  false);
    }

    // ── Hotbar slots (rendered at the bottom of the screen) ──────────────────

    private void renderHotbarSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            int sx = hotbarSlotScreenX[i];
            int sy = hotbarY;

            // Highlight the slot on hover
            boolean hovered = isMouseOverHotbarSlot(i, mouseX, mouseY);
            if (hovered) {
                int hlColor = selectedAbility != null ? 0x55FFFFFF : 0x22FFFFFF;
                graphics.fill(sx, sy, sx + hotbarSlotW, sy + hotbarSlotH, hlColor);
            }

            // Render the ability icon / placeholder for whatever is in this slot
            Ability assigned = ClientAbilityData.getSlotAbility(i);
            if (assigned == null) continue;

            int inset    = 2;
            int iconX    = sx + inset;
            int iconY    = sy + inset;
            int iconSize = hotbarSlotW - inset * 2;

            // TODO: blit real icon when textures exist:
            // graphics.blit(assigned.getIcon(), iconX, iconY, iconSize, iconSize,
            //               0, 0, SOURCE_ICON_W, SOURCE_ICON_H, SOURCE_ICON_W, SOURCE_ICON_H);

            // Coloured placeholder
            int glow = ROW_GLOW[getRowIndex(assigned.getType())];
            graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize,
                    (glow & 0x00FFFFFF) | 0x88000000);

            // Slot number
            String slotNum = String.valueOf(i + 1);
            graphics.drawString(font, slotNum, iconX, iconY, 0xAAFFFFFF, false);
        }
    }

    // =========================================================================
    // Mouse Handling
    // =========================================================================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // ── Left click ───────────────────────────────────────────────────────
        if (button == 0) {
            // Check all ability cards in every row
            Ability clicked = getCardAt((int) mouseX, (int) mouseY);
            if (clicked != null) {
                selectedAbility = (selectedAbility == clicked) ? null : clicked;
                return true;
            }

            // Click on a hotbar slot → assign the selected ability
            for (int i = 0; i < HOTBAR_SLOTS; i++) {
                if (isMouseOverHotbarSlot(i, (int) mouseX, (int) mouseY)) {
                    if (selectedAbility != null) {
                        assignToSlot(selectedAbility.getId(), i);
                    }
                    return true;
                }
            }
        }

        // ── Right click ──────────────────────────────────────────────────────
        if (button == 1) {
            // Right-click hotbar slot → clear it
            for (int i = 0; i < HOTBAR_SLOTS; i++) {
                if (isMouseOverHotbarSlot(i, (int) mouseX, (int) mouseY)) {
                    assignToSlot("", i); // empty string = clear
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // =========================================================================
    // Assignment logic
    // =========================================================================

    /**
     * Assigns {@code abilityId} to hotbar slot {@code slot} (or clears it if
     * {@code abilityId} is empty). Updates client-side state immediately for a
     * responsive UI, then sends the packet to the server for persistence.
     */
    private void assignToSlot(String abilityId, int slot) {
        // Optimistic local update
        ClientAbilityData.setSlotOptimistic(slot, abilityId);
        // Refresh the per-card hotbar badge
        refreshHotbarSlots();
        // Send to server for validation + persistence
        ModNetworking.INSTANCE.sendToServer(
                new AssignAbilityToHotbarC2SPacket(abilityId, slot));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Returns the ability card at screen position (mx, my), or null if none.
     * Mirrors the layout logic from renderAllRows().
     */
    private Ability getCardAt(int mx, int my) {
        for (int rowIdx = 0; rowIdx < ROW_TYPES.length; rowIdx++) {
            List<Ability> list = abilityMap.get(ROW_TYPES[rowIdx]);
            int rx = rowScreenX[rowIdx];
            int ry = rowScreenY[rowIdx];
            int rw = rowScreenW;

            int labelW      = font.width(ROW_TYPES[rowIdx].getDisplayName().toUpperCase()) + 8;
            int cardsStartX = rx + labelW + 4;
            int cardTop     = ry + toScreenH(2);

            for (int ci = 0; ci < list.size(); ci++) {
                int cx = cardsStartX + ci * (cardScreenW + cardScreenGap);
                if (cx + cardScreenW > rx + rw - 4) break;

                if (mx >= cx && mx < cx + cardScreenW
                        && my >= cardTop && my < cardTop + cardScreenH) {
                    return list.get(ci);
                }
            }
        }
        return null;
    }

    private int getRowIndex(AbilityType type) {
        for (int i = 0; i < ROW_TYPES.length; i++)
            if (ROW_TYPES[i] == type) return i;
        return 0;
    }

    private boolean isRequirementMet(String req) {
        if (req.startsWith("Mastery Level ")) {
            try {
                int needed = Integer.parseInt(req.substring("Mastery Level ".length()).trim());
                return ClientCEData.getMasteryLevel() >= needed;
            } catch (NumberFormatException ignored) {}
        }
        return false;
    }
}
