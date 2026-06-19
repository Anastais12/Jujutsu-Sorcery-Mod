package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.client.ClientSorcererState;
import com.anastas1s12.jjs.client.DistortionClientState;
import com.anastas1s12.jjs.client.gui.CursedEnergyHudOverlay;
import com.anastas1s12.jjs.client.gui.SorcererHotbarOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

/**
 * Client-side event handler.
 *
 * Mod bus  — overlay registration.
 * Forge bus — vanilla hotbar suppression while sorcerer mode is active.
 */
public class ClientEventHandler {

    private static PostChain DISTORTION_CHAIN;
    private static final ResourceLocation DISTORTION_SHADER = ResourceLocation.fromNamespaceAndPath("jjs", "shaders/post/distortion.json");

    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            // CE energy bar — rendered above everything
            event.registerAboveAll("cursed_energy_hud", CursedEnergyHudOverlay.INSTANCE);

            // Sorcerer ability hotbar — rendered above the vanilla hotbar area
            // Only actually draws when ClientSorcererState.isSorcererModeActive() == true
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),
                    "sorcerer_hotbar", SorcererHotbarOverlay.INSTANCE);
        }
    }

    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onPreRenderHotbar(RenderGuiOverlayEvent.Pre event) {
            if (!ClientSorcererState.isSorcererModeActive()) return;

            var id = event.getOverlay().id();

            // Cancel vanilla hotbar background render
            if (id.equals(VanillaGuiOverlay.HOTBAR.id())) {
                event.setCanceled(true);
            }
        }
    }

    // Shader registration (MOD bus)
    @Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusClientEvents {

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            Minecraft mc = Minecraft.getInstance();
            PostChain chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), DISTORTION_SHADER);
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            DISTORTION_CHAIN = chain;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        DistortionClientState.clientTick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        if (DistortionClientState.shouldRenderDistortion() && DISTORTION_CHAIN != null) {
            Minecraft mc = Minecraft.getInstance();
            float gameTime = (mc.level != null ? mc.level.getGameTime() : 0) + event.getPartialTick();

            try {
                java.lang.reflect.Field passesField = net.minecraft.client.renderer.PostChain.class.getDeclaredField("passes");
                passesField.setAccessible(true);

                @SuppressWarnings("unchecked")
                java.util.List<net.minecraft.client.renderer.PostPass> passes =
                        (java.util.List<net.minecraft.client.renderer.PostPass>) passesField.get(DISTORTION_CHAIN);

                // Inject the updated time variable into all active passes
                for (net.minecraft.client.renderer.PostPass pass : passes) {
                    var effectUniforms = pass.getEffect().getUniform("Time");
                    if (effectUniforms != null) {
                        effectUniforms.set(gameTime);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            DISTORTION_CHAIN.process(event.getPartialTick());
            mc.getMainRenderTarget().bindWrite(false);
        }
    }


}
