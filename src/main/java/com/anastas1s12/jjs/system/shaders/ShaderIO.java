package com.anastas1s12.jjs.system.shaders;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.client.gui.components.MultiLineEditBox;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShaderIO {
    // Save to the user's config folder
    private static final Path SHADER_DIR = FMLPaths.CONFIGDIR.get().resolve("custom_shaders");

    public static void saveCode(String filename, String code) {
        try {
            if (!Files.exists(SHADER_DIR)) {
                Files.createDirectories(SHADER_DIR);
            }
            Path filepath = SHADER_DIR.resolve(filename);
            Files.writeString(filepath, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCode(String filename, MultiLineEditBox editor) {
        try {
            Path filepath = SHADER_DIR.resolve(filename);
            if (Files.exists(filepath)) {
                String code = Files.readString(filepath);
                editor.setValue(code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compileAndApply(String filename) {
        // This is where the magic happens (see explanation below)
    }
}