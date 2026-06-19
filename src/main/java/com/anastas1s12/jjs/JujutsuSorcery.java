package com.anastas1s12.jjs;

import com.anastas1s12.jjs.config.SWBConfig;
import com.anastas1s12.jjs.system.ability.AbilityRegistry;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.event.CursedEnergyEventHandler;
import com.anastas1s12.jjs.event.ServerEventHandler;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.system.shader.data.project.ProjectManager;
import com.anastas1s12.jjs.system.shader.render.ShaderRenderManager;
import com.anastas1s12.jjs.system.shader.render.impact.ImpactEffectManager;
import com.anastas1s12.jjs.system.shader.render.postprocess.PostProcessPipeline;
import com.anastas1s12.jjs.system.technique.TechniqueRegistry;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JujutsuSorcery.MOD_ID)
public class JujutsuSorcery {

    public static final String MOD_ID = "jjs";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static JujutsuSorcery INSTANCE;

    private ProjectManager projectManager;
    private ShaderRenderManager renderManager;
    private PostProcessPipeline postProcessPipeline;
    private ImpactEffectManager impactEffectManager;


    public JujutsuSorcery(FMLJavaModLoadingContext context) {
        INSTANCE = this;

        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        context.registerConfig(ModConfig.Type.CLIENT, SWBConfig.SPEC);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        forgeEventBus.register(CursedEnergyEventHandler.class);
        forgeEventBus.register(ServerEventHandler.class);

        forgeEventBus.register(CursedEnergyCapability.class);

        LOGGER.info("JujutsuSorcery (JJS) mod initialized!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetworking.register();

            AbilityRegistry.init();
            TechniqueRegistry.init();

            LOGGER.info("JJS Networking and AbilityRegistry initialized.");
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        this.projectManager = new ProjectManager();
        this.renderManager = new ShaderRenderManager();
        this.impactEffectManager = new ImpactEffectManager();

        projectManager.initializeStorage();

        event.enqueueWork(() -> {
            try {
                this.postProcessPipeline = new PostProcessPipeline();
                LOGGER.info("JJS PostProcessPipeline successfully allocated on the Render Thread.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize PostProcessPipeline: ", e);
            }
        });

        LOGGER.info("JJS Client setup managers registered.");
    }


    public static JujutsuSorcery getInstance() {
        return INSTANCE;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public ShaderRenderManager getRenderManager() {
        return renderManager;
    }

    public PostProcessPipeline getPostProcessPipeline() {
        return postProcessPipeline;
    }

    public ImpactEffectManager getImpactEffectManager() {
        return impactEffectManager;
    }
}
