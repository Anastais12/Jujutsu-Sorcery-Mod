package com.anastas1s12.jjs.system.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import java.lang.reflect.Field;
import java.util.List;

public class ShaderManager {
    private static PostChain activeShader;
    private static Field passesField;

    static {
        try {
            // "f_110009_" is the unique SRG name for the private "passes" field in Forge 1.20.1
            passesField = PostChain.class.getDeclaredField("f_110009_");
            passesField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                // Fallback for development environments using Mojang mappings
                passesField = PostChain.class.getDeclaredField("passes");
                passesField.setAccessible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImpactShader() {
        Minecraft mc = Minecraft.getInstance();
        try {
            // Use 1.20.1 compatible ResourceLocation constructor
            mc.gameRenderer.loadEffect(ResourceLocation.fromNamespaceAndPath("jjs", "shaders/post/impact.json"));
            activeShader = mc.gameRenderer.currentEffect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void setUniform(String uniformName, float value) {
        if (activeShader != null && passesField != null) {
            try {
                // Safely grab the private passes list via reflection
                List<PostPass> passes = (List<PostPass>) passesField.get(activeShader);

                passes.forEach(pass -> {
                    if (pass.getEffect().getUniform(uniformName) != null) {
                        pass.getEffect().safeGetUniform(uniformName).set(value);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
