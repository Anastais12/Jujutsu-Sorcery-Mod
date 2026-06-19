package com.anastas1s12.jjs.system.shader.data.project;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages shader project storage in .minecraft/shaderworkbench/
 *
 * Structure:
 * .minecraft/shaderworkbench/
 *   projects/
 *   presets/
 *   exports/
 *   logs/
 *   cache/
 */
public class ProjectManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path baseDir;
    private final Path projectsDir;
    private final Path presetsDir;
    private final Path exportsDir;
    private final Path logsDir;
    private final Path cacheDir;

    private final Map<String, Boolean> enabledStates = new HashMap<>();

    public ProjectManager() {
        String mcDir = System.getProperty("user.dir");
        this.baseDir = Paths.get(mcDir, "shaderworkbench");
        this.projectsDir = baseDir.resolve("projects");
        this.presetsDir = baseDir.resolve("presets");
        this.exportsDir = baseDir.resolve("exports");
        this.logsDir = baseDir.resolve("logs");
        this.cacheDir = baseDir.resolve("cache");
    }

    public void initializeStorage() {
        try {
            Files.createDirectories(projectsDir);
            Files.createDirectories(presetsDir);
            Files.createDirectories(exportsDir);
            Files.createDirectories(logsDir);
            Files.createDirectories(cacheDir);
            JujutsuSorcery.LOGGER.info("ShaderWorkbench storage initialized at: {}", baseDir);

            // Copy built-in presets if presets dir is empty
            if (Files.list(presetsDir).count() == 0) {
                extractBuiltInPresets();
            }
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to initialize storage", e);
        }
    }

    private void extractBuiltInPresets() {
        // Presets are loaded from mod resources
        JujutsuSorcery.LOGGER.info("Extracting built-in presets...");
    }

    public List<String> listProjects() {
        try {
            return Files.list(projectsDir)
                    .filter(Files::isDirectory)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public ShaderProject loadProject(String name) {
        Path projectDir = projectsDir.resolve(name);
        if (!Files.exists(projectDir)) return null;

        try {
            Path configFile = projectDir.resolve("project.json");
            if (!Files.exists(configFile)) {
                // Legacy or simple project
                return loadLegacyProject(name, projectDir);
            }

            String json = Files.readString(configFile);
            return GSON.fromJson(json, ShaderProject.class);
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to load project: {}", name, e);
            return null;
        }
    }

    private ShaderProject loadLegacyProject(String name, Path dir) throws IOException {
        ShaderProject project = new ShaderProject(name);

        Path vert = dir.resolve("shader.vsh");
        Path frag = dir.resolve("shader.fsh");
        Path json = dir.resolve("shader.json");

        if (Files.exists(vert)) {
            project.setVertexShader(Files.readString(vert));
        }
        if (Files.exists(frag)) {
            project.setFragmentShader(Files.readString(frag));
        }
        if (Files.exists(json)) {
            project.setJsonDefinition(Files.readString(json));
        }

        return project;
    }

    public void saveProject(ShaderProject project) {
        Path projectDir = projectsDir.resolve(project.getName());
        try {
            Files.createDirectories(projectDir);

            // Save as unified JSON
            String json = GSON.toJson(project);
            Files.writeString(projectDir.resolve("project.json"), json);

            // Also save individual files for external editing
            Files.writeString(projectDir.resolve("shader.vsh"), project.getVertexShader());
            Files.writeString(projectDir.resolve("shader.fsh"), project.getFragmentShader());
            Files.writeString(projectDir.resolve("shader.json"), project.getJsonDefinition());

            JujutsuSorcery.LOGGER.info("Saved project: {}", project.getName());
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to save project: {}", project.getName(), e);
        }
    }

    public void deleteProject(String name) {
        Path projectDir = projectsDir.resolve(name);
        try {
            deleteDirectory(projectDir);
            JujutsuSorcery.LOGGER.info("Deleted project: {}", name);
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to delete project: {}", name, e);
        }
    }

    public String exportProject(String name) {
        ShaderProject project = loadProject(name);
        if (project == null) return null;

        String exportName = name + "_" + System.currentTimeMillis() + ".swb";
        Path exportPath = exportsDir.resolve(exportName);

        try {
            String json = GSON.toJson(project);
            Files.writeString(exportPath, json);
            return exportPath.toString();
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to export project: {}", name, e);
            return null;
        }
    }

    public boolean importProject(String path) {
        Path importPath = Paths.get(path);
        if (!Files.exists(importPath)) {
            // Try in exports dir
            importPath = exportsDir.resolve(path);
        }

        try {
            String json = Files.readString(importPath);
            ShaderProject project = GSON.fromJson(json, ShaderProject.class);

            // Ensure unique name
            String name = project.getName();
            int suffix = 1;
            while (Files.exists(projectsDir.resolve(name))) {
                name = project.getName() + "_" + suffix++;
            }
            project.setName(name);

            saveProject(project);
            return true;
        } catch (IOException e) {
            JujutsuSorcery.LOGGER.error("Failed to import project: {}", path, e);
            return false;
        }
    }

    public boolean isProjectEnabled(String name) {
        return enabledStates.getOrDefault(name, false);
    }

    public void setProjectEnabled(String name, boolean enabled) {
        enabledStates.put(name, enabled);
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.isDirectory(dir)) {
            try (var stream = Files.list(dir)) {
                for (Path child : stream.toList()) {
                    deleteDirectory(child);
                }
            }
        }
        Files.deleteIfExists(dir);
    }
}