package com.anastas1s12.jjs;

import com.anastas1s12.jjs.ability.AbilityRegistry;
import com.anastas1s12.jjs.ability.TechniqueRegistry;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.event.CursedEnergyEventHandler;
import com.anastas1s12.jjs.event.ServerEventHandler;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JujutsuSorcery.MOD_ID)
public class JujutsuSorcery {

    public static final String MOD_ID = "jjs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JujutsuSorcery(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

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
        LOGGER.info("JJS Client setup complete.");
    }
}
