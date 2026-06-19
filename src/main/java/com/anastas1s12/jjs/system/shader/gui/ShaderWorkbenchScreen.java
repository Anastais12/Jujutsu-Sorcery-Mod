package com.anastas1s12.jjs.system.shader.gui;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.data.project.ShaderProject;
import com.anastas1s12.jjs.system.shader.gui.components.CodeEditor;
import com.anastas1s12.jjs.system.shader.gui.components.ConsolePanel;
import com.anastas1s12.jjs.system.shader.gui.components.ProjectPanel;
import com.anastas1s12.jjs.system.shader.gui.components.PropertiesPanel;
import com.anastas1s12.jjs.system.shader.gui.timeline.TimelineEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Main Shader Workbench GUI.
 *
 * Layout:
 * +------------------+------------------------+------------------+
 * |  Left Panel      |   Center Panel         |  Right Panel     |
 * |  (Projects)      |   (Code Editor)        |  (Properties)    |
 * |                  |                        |                  |
 * |  - List          |   - Vertex Shader      |  - Name          |
 * |  - Create        |   - Fragment Shader    |  - Render Pass   |
 * |  - Delete        |   - JSON Definition    |  - Blend Mode    |
 * |  - Duplicate     |                        |  - Uniforms      |
 * |  - Import        |   Features:            |                  |
 * |  - Export        |   - Syntax Highlight   |                  |
 * |                  |   - Line Numbers       |                  |
 * |                  |   - Auto Indent        |                  |
 * |                  |   - Error Highlight    |                  |
 * |                  |   - Search/Replace     |                  |
 * +------------------+------------------------+------------------+
 * |  Bottom Panel: Console + Timeline                          |
 * +------------------------------------------------------------+
 */
public class ShaderWorkbenchScreen extends Screen {
    private static final int LEFT_PANEL_WIDTH = 160;
    private static final int RIGHT_PANEL_WIDTH = 200;
    private static final int BOTTOM_PANEL_HEIGHT = 120;

    private final Minecraft mc = Minecraft.getInstance();

    // Sub-components
    private ProjectPanel projectPanel;
    private CodeEditor codeEditor;
    private PropertiesPanel propertiesPanel;
    private ConsolePanel consolePanel;
    private TimelineEditor timelineEditor;

    // State
    private ShaderProject currentProject;
    private EditorMode currentMode = EditorMode.VERTEX_SHADER;

    private enum EditorMode {
        VERTEX_SHADER, FRAGMENT_SHADER, JSON_DEFINITION
    }

    public ShaderWorkbenchScreen() {
        super(Component.literal("Shader Workbench"));
    }

    @Override
    protected void init() {
        super.init();

        int centerWidth = this.width - LEFT_PANEL_WIDTH - RIGHT_PANEL_WIDTH;
        int centerHeight = this.height - BOTTOM_PANEL_HEIGHT;

        // Left panel - Project list
        projectPanel = new ProjectPanel(
                0, 0,
                LEFT_PANEL_WIDTH, centerHeight,
                this::onProjectSelected,
                this::onProjectCreated,
                this::onProjectDeleted,
                this::onProjectDuplicated,
                this::onProjectImported,
                this::onProjectExported
        );
        this.addRenderableWidget(projectPanel);
        projectPanel.initButtons(); // Initialize the buttons inside the panel

        // Center panel - Code editor
        codeEditor = new CodeEditor(
                LEFT_PANEL_WIDTH, 30,
                centerWidth, centerHeight - 30,
                this::onCodeChanged,
                this::onCompileRequested
        );
        this.addRenderableWidget(codeEditor);

        // Right panel - Properties
        propertiesPanel = new PropertiesPanel(
                this.width - RIGHT_PANEL_WIDTH, 0,
                RIGHT_PANEL_WIDTH, centerHeight,
                this::onPropertyChanged
        );
        this.addRenderableWidget(propertiesPanel);

        // Bottom panel - Console
        consolePanel = new ConsolePanel(
                0, this.height - BOTTOM_PANEL_HEIGHT,
                this.width, 80
        );
        this.addRenderableWidget(consolePanel);

        // Bottom panel - Timeline
        timelineEditor = new TimelineEditor(
                0, this.height - BOTTOM_PANEL_HEIGHT + 80,
                this.width, 40
        );
        this.addRenderableWidget(timelineEditor);

        // Mode switch buttons
        this.addRenderableWidget(Button.builder(
                Component.literal("Vertex"),
                b -> switchMode(EditorMode.VERTEX_SHADER)
        ).pos(LEFT_PANEL_WIDTH + 5, 5).size(60, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Fragment"),
                b -> switchMode(EditorMode.FRAGMENT_SHADER)
        ).pos(LEFT_PANEL_WIDTH + 70, 5).size(60, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("JSON"),
                b -> switchMode(EditorMode.JSON_DEFINITION)
        ).pos(LEFT_PANEL_WIDTH + 135, 5).size(50, 20).build());

        // Compile button
        this.addRenderableWidget(Button.builder(
                Component.literal("▶ Compile"),
                b -> compileCurrentShader()
        ).pos(this.width - RIGHT_PANEL_WIDTH - 90, 5).size(80, 20).build());

        // Load initial projects
        loadProjects();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Dark background
        this.renderBackground(graphics);

        // Draw panel backgrounds
        graphics.fill(0, 0, LEFT_PANEL_WIDTH, this.height - BOTTOM_PANEL_HEIGHT, 0xFF1E1E1E);
        graphics.fill(LEFT_PANEL_WIDTH, 0, this.width - RIGHT_PANEL_WIDTH, 30, 0xFF252526);
        graphics.fill(this.width - RIGHT_PANEL_WIDTH, 0, this.width, this.height - BOTTOM_PANEL_HEIGHT, 0xFF1E1E1E);
        graphics.fill(0, this.height - BOTTOM_PANEL_HEIGHT, this.width, this.height, 0xFF1A1A1A);

        // Draw separators
        graphics.fill(LEFT_PANEL_WIDTH, 0, LEFT_PANEL_WIDTH + 1, this.height - BOTTOM_PANEL_HEIGHT, 0xFF3E3E42);
        graphics.fill(this.width - RIGHT_PANEL_WIDTH - 1, 0, this.width - RIGHT_PANEL_WIDTH, this.height - BOTTOM_PANEL_HEIGHT, 0xFF3E3E42);
        graphics.fill(0, this.height - BOTTOM_PANEL_HEIGHT - 1, this.width, this.height - BOTTOM_PANEL_HEIGHT, 0xFF3E3E42);

        // Render components
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Ctrl+S to save
        if (keyCode == 83 && (modifiers & 0x02) != 0) { // Ctrl+S
            saveCurrentProject();
            return true;
        }
        // F5 to compile
        if (keyCode == 63) { // F5
            compileCurrentShader();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        init(); // Re-init on resize
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    private void switchMode(EditorMode mode) {
        this.currentMode = mode;
        if (currentProject != null) {
            switch (mode) {
                case VERTEX_SHADER -> codeEditor.setText(currentProject.getVertexShader());
                case FRAGMENT_SHADER -> codeEditor.setText(currentProject.getFragmentShader());
                case JSON_DEFINITION -> codeEditor.setText(currentProject.getJsonDefinition());
            }
        }
    }

    private void onProjectSelected(ShaderProject project) {
        this.currentProject = project;
        propertiesPanel.setProject(project);
        switchMode(currentMode);
    }

    private void onProjectCreated(String name) {
        ShaderProject project = new ShaderProject(name);
        JujutsuSorcery.getInstance().getProjectManager().saveProject(project);
        loadProjects();
    }

    private void onProjectDeleted(String name) {
        JujutsuSorcery.getInstance().getProjectManager().deleteProject(name);
        loadProjects();
    }

    private void onProjectDuplicated(String name) {
        ShaderProject original = JujutsuSorcery.getInstance().getProjectManager().loadProject(name);
        if (original != null) {
            ShaderProject copy = original.duplicate(name + "_copy");
            JujutsuSorcery.getInstance().getProjectManager().saveProject(copy);
            loadProjects();
        }
    }

    private void onProjectImported(String path) {
        boolean success = JujutsuSorcery.getInstance().getProjectManager().importProject(path);
        consolePanel.log(success ? "Imported: " + path : "Failed to import: " + path,
                success ? ConsolePanel.LogLevel.INFO : ConsolePanel.LogLevel.ERROR);
        loadProjects();
    }

    private void onProjectExported(String name) {
        String path = JujutsuSorcery.getInstance().getProjectManager().exportProject(name);
        consolePanel.log("Exported to: " + path, ConsolePanel.LogLevel.INFO);
    }

    private void onCodeChanged(String code) {
        if (currentProject == null) return;
        switch (currentMode) {
            case VERTEX_SHADER -> currentProject.setVertexShader(code);
            case FRAGMENT_SHADER -> currentProject.setFragmentShader(code);
            case JSON_DEFINITION -> currentProject.setJsonDefinition(code);
        }
    }

    private void onCompileRequested() {
        compileCurrentShader();
    }

    private void onPropertyChanged(String key, Object value) {
        if (currentProject == null) return;
        currentProject.setProperty(key, value);
    }

    private void compileCurrentShader() {
        if (currentProject == null) {
            consolePanel.log("No project selected", ConsolePanel.LogLevel.WARNING);
            return;
        }

        // Save first
        saveCurrentProject();

        // Compile
        String name = currentProject.getName();
        boolean success = JujutsuSorcery.getInstance().getRenderManager().compileShader(name);

        if (success) {
            consolePanel.log("Compilation successful: " + name, ConsolePanel.LogLevel.SUCCESS);
            // Auto-enable in pipeline
            JujutsuSorcery.getInstance().getPostProcessPipeline().enableShader(name);
        } else {
            var result = JujutsuSorcery.getInstance().getRenderManager().getCompileResult(name);
            consolePanel.log("Compilation failed: " + result.getErrorMessage(), ConsolePanel.LogLevel.ERROR);
            codeEditor.highlightError(result.getErrorLine());
        }
    }

    private void saveCurrentProject() {
        if (currentProject != null) {
            JujutsuSorcery.getInstance().getProjectManager().saveProject(currentProject);
            consolePanel.log("Saved: " + currentProject.getName(), ConsolePanel.LogLevel.INFO);
        }
    }

    private void loadProjects() {
        var projects = JujutsuSorcery.getInstance().getProjectManager().listProjects();
        projectPanel.setProjects(projects);
    }
}
