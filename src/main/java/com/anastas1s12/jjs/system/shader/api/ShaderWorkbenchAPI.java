package com.anastas1s12.jjs.system.shader.api;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.system.shader.data.project.ShaderProject;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffect;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffectManager;
import com.anastas1s12.jjs.system.shader.render.postprocess.PostProcessPipeline;

import java.util.Map;

/**
 * Public API for other mods to integrate with ShaderWorkbench.
 *
 * Usage:
 *   ShaderWorkbenchAPI.registerShader("mymod:cool_effect", vertexCode, fragmentCode);
 *   ShaderWorkbenchAPI.registerImpactEffect("mymod:custom_hit", factory);
 *   ShaderWorkbenchAPI.triggerImpact("mymod:custom_hit");
 */
public final class ShaderWorkbenchAPI {

    private ShaderWorkbenchAPI() {}

    /**
     * Register a custom post-processing shader.
     *
     * @param id Unique identifier (format: "modid:shadername")
     * @param vertexShader GLSL vertex shader source
     * @param fragmentShader GLSL fragment shader source
     * @return true if registration succeeded
     */
    public static boolean registerShader(String id, String vertexShader, String fragmentShader) {
        try {
            ShaderProject project = new ShaderProject(id.replace(":", "_"));
            project.setVertexShader(vertexShader);
            project.setFragmentShader(fragmentShader);

            JujutsuSorcery.getInstance().getProjectManager().saveProject(project);
            return JujutsuSorcery.getInstance().getRenderManager().compileShader(project.getName());
        } catch (Exception e) {
            JujutsuSorcery.LOGGER.error("API: Failed to register shader: {}", id, e);
            return false;
        }
    }

    /**
     * Register a custom impact effect.
     *
     * @param id Unique identifier
     * @param factory Factory for creating the effect
     */
    public static void registerImpactEffect(String id, ImpactEffectFactory factory) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            manager.register(id, (params) -> factory.create(params));
        }
    }

    /**
     * Trigger an impact effect by ID.
     *
     * @param id Effect identifier
     * @return true if effect was triggered
     */
    public static boolean triggerImpact(String id) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            return manager.triggerEffect(id);
        }
        return false;
    }

    /**
     * Trigger an impact effect with parameters.
     */
    public static boolean triggerImpact(String id, Map<String, Object> params) {
        ImpactEffectManager manager = JujutsuSorcery.getInstance().getImpactEffectManager();
        if (manager != null) {
            return manager.triggerEffect(id, params);
        }
        return false;
    }

    /**
     * Enable a post-processing shader.
     */
    public static boolean enableShader(String name) {
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            pipeline.enableShader(name);
            return true;
        }
        return false;
    }

    /**
     * Disable a post-processing shader.
     */
    public static boolean disableShader(String name) {
        PostProcessPipeline pipeline = JujutsuSorcery.getInstance().getPostProcessPipeline();
        if (pipeline != null) {
            pipeline.disableShader(name);
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public interface ImpactEffectFactory {
        ImpactEffect create(Map<String, Object> params);
    }
}