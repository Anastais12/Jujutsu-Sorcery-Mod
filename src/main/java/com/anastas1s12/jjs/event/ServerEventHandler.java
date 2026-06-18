package com.anastas1s12.jjs.event;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.anastas1s12.jjs.command.CECommand;
import com.anastas1s12.jjs.command.JJSCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Server-side event handler for registering commands.
 */
@Mod.EventBusSubscriber(modid = JujutsuSorcery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CECommand.register(event.getDispatcher());
        JJSCommand.register(event.getDispatcher());
    }
}
