package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.capability.CursedEnergyCapability;
import com.anastas1s12.jjs.networking.ModNetworking;
import com.anastas1s12.jjs.networking.c2s.NearbySorcererPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID)
public class NearbySorcererDetector {

    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 10; // every 0.5s
    private static final double RADIUS = 25.0D;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;

        boolean found = !serverPlayer.level().getEntitiesOfClass(
                Player.class,
                serverPlayer.getBoundingBox().inflate(RADIUS),
                p -> p != serverPlayer && hasCursedEnergy(p)
        ).isEmpty();

        ModNetworking.sendTo(serverPlayer, new NearbySorcererPacket(found));
    }

    private static boolean hasCursedEnergy(Player player) {
        return CursedEnergyCapability.get(player)
                .map(cap -> cap.getCurrentCE() > 0)
                .orElse(false);
    }

}