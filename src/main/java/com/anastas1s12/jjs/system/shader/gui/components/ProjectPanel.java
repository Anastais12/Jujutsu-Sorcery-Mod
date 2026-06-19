package com.anastas1s12.jjs.system.shader.gui.components;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.data.project.ShaderProject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Left panel of the Shader Workbench GUI.
 * Lists shader projects and provides create/delete/import/export controls.
 */
public class ProjectPanel extends AbstractWidget {

    private static final int ITEM_HEIGHT = 18;
    private static final int BUTTON_HEIGHT = 16;

    private final List<String> projects = new ArrayList<>();
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    private final Consumer<ShaderProject> onSelect;
    private final Consumer<String> onCreate;
    private final Consumer<String> onDelete;
    private final Consumer<String> onDuplicate;
    private final Consumer<String> onImport;
    private final Consumer<String> onExport;

    private EditBox newProjectName;
    private Button createBtn;
    private Button deleteBtn;
    private Button duplicateBtn;
    private Button importBtn;
    private Button exportBtn;

    public ProjectPanel(int x, int y, int width, int height,
                        Consumer<ShaderProject> onSelect,
                        Consumer<String> onCreate,
                        Consumer<String> onDelete,
                        Consumer<String> onDuplicate,
                        Consumer<String> onImport,
                        Consumer<String> onExport) {
        super(x, y, width, height, Component.literal("Projects"));
        this.onSelect = onSelect;
        this.onCreate = onCreate;
        this.onDelete = onDelete;
        this.onDuplicate = onDuplicate;
        this.onImport = onImport;
        this.onExport = onExport;
    }

    public void initButtons() {
        int btnY = getY() + height - 100;
        int btnW = width - 10;

        newProjectName = new EditBox(Minecraft.getInstance().font,
                getX() + 5, btnY, btnW, 14, Component.literal("New project name"));
        newProjectName.setMaxLength(64);
        newProjectName.setValue("new_shader");

        createBtn = Button.builder(Component.literal("+ Create"), b -> {
            if (!newProjectName.getValue().isEmpty()) {
                onCreate.accept(newProjectName.getValue());
            }
        }).pos(getX() + 5, btnY + 18).size(btnW, BUTTON_HEIGHT).build();

        deleteBtn = Button.builder(Component.literal("- Delete"), b -> {
            if (selectedIndex >= 0 && selectedIndex < projects.size()) {
                onDelete.accept(projects.get(selectedIndex));
            }
        }).pos(getX() + 5, btnY + 36).size(btnW, BUTTON_HEIGHT).build();

        duplicateBtn = Button.builder(Component.literal("⎘ Duplicate"), b -> {
            if (selectedIndex >= 0 && selectedIndex < projects.size()) {
                onDuplicate.accept(projects.get(selectedIndex));
            }
        }).pos(getX() + 5, btnY + 54).size(btnW, BUTTON_HEIGHT).build();

        importBtn = Button.builder(Component.literal("↓ Import"), b -> {
            onImport.accept("");
        }).pos(getX() + 5, btnY + 72).size(btnW / 2 - 2, BUTTON_HEIGHT).build();

        exportBtn = Button.builder(Component.literal("↑ Export"), b -> {
            if (selectedIndex >= 0 && selectedIndex < projects.size()) {
                onExport.accept(projects.get(selectedIndex));
            }
        }).pos(getX() + 5 + btnW / 2, btnY + 72).size(btnW / 2 - 2, BUTTON_HEIGHT).build();
    }

    public void setProjects(List<String> projects) {
        this.projects.clear();
        this.projects.addAll(projects);
        if (selectedIndex >= this.projects.size()) {
            selectedIndex = this.projects.size() - 1;
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF1E1E1E);

        graphics.fill(getX(), getY(), getX() + width, getY() + 16, 0xFF2D2D30);
        graphics.drawCenteredString(Minecraft.getInstance().font,
                "§lProjects", getX() + width / 2, getY() + 4, 0xFFFFFF);

        int listY = getY() + 20;
        int listHeight = height - 120;
        int visibleItems = listHeight / ITEM_HEIGHT;

        for (int i = 0; i < visibleItems; i++) {
            int idx = scrollOffset + i;
            if (idx >= projects.size()) break;

            int itemY = listY + i * ITEM_HEIGHT;
            boolean hovered = mouseX >= getX() && mouseX <= getX() + width
                    && mouseY >= itemY && mouseY < itemY + ITEM_HEIGHT;
            boolean selected = idx == selectedIndex;

            int bgColor = selected ? 0xFF094771 : (hovered ? 0xFF2A2D2E : 0xFF1E1E1E);
            graphics.fill(getX() + 2, itemY, getX() + width - 2, itemY + ITEM_HEIGHT - 1, bgColor);

            String name = projects.get(idx);
            int textColor = selected ? 0xFFFFFF : 0xCCCCCC;
            graphics.drawString(Minecraft.getInstance().font, name, getX() + 8, itemY + 4, textColor);
        }

        if (createBtn != null) {
            newProjectName.render(graphics, mouseX, mouseY, partialTicks);
            createBtn.render(graphics, mouseX, mouseY, partialTicks);
            deleteBtn.render(graphics, mouseX, mouseY, partialTicks);
            duplicateBtn.render(graphics, mouseX, mouseY, partialTicks);
            importBtn.render(graphics, mouseX, mouseY, partialTicks);
            exportBtn.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (newProjectName != null && newProjectName.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (createBtn != null && createBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (deleteBtn != null && deleteBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (duplicateBtn != null && duplicateBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (importBtn != null && importBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (exportBtn != null && exportBtn.mouseClicked(mouseX, mouseY, button)) return true;

        int listY = getY() + 20;
        int listHeight = height - 120;
        if (mouseX >= getX() && mouseX <= getX() + width
                && mouseY >= listY && mouseY < listY + listHeight) {
            int idx = scrollOffset + (int) ((mouseY - listY) / ITEM_HEIGHT);
            if (idx >= 0 && idx < projects.size()) {
                selectedIndex = idx;
                ShaderProject project = JujutsuSorcery.getInstance().getProjectManager()
                        .loadProject(projects.get(idx));
                if (project != null) {
                    onSelect.accept(project);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int listHeight = height - 120;
        int visibleItems = listHeight / ITEM_HEIGHT;
        int maxScroll = Math.max(0, projects.size() - visibleItems);

        if (delta > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else {
            scrollOffset = Math.min(maxScroll, scrollOffset + 1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (newProjectName != null && newProjectName.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (newProjectName != null && newProjectName.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
