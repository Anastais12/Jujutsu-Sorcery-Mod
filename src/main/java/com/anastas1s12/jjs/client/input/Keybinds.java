package com.anastas1s12.jjs.client.input;

import com.anastas1s12.jjs.JujutsuSorcery;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * =============================================================================
 * KEYBINDS — All mod key mappings
 * =============================================================================
 *
 * Register all keybinds here. They are registered to the Forge
 * key registry during FMLClientSetupEvent.
 *
 * Current keybinds:
 *   - OPEN_MENU: Opens the main JJS sorcerer menu (default: J)
 *   - TOGGLE_RCT: Toggles Reverse Cursed Technique (default: R)
 *   - USE_ABILITY: Uses the currently selected hotbar ability (default: Middle mouse)
 *
 * To add a new keybind:
 *   1. Add a new KeyMapping field below
 *   2. Register it in the register() method
 *   3. Handle it in ClientInputHandler
 *
 * =============================================================================
 */
public class Keybinds {

    public static final String CATEGORY = "key.categories." + JujutsuSorcery.MOD_ID;

    private static boolean isWorkbenchOpen = false;
    private static boolean isDebugOverlayOpen = false;


    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".open_menu",     // Translation key
            KeyConflictContext.IN_GAME,                         // Only works in-game
            InputConstants.Type.KEYSYM,                         // Keyboard key
            GLFW.GLFW_KEY_J,                                    // Default: J
            CATEGORY                                            // Category in controls menu
    );

    public static final KeyMapping TOGGLE_SORCERER_MODE = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".toggle_sorcerer_mode",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    public static final KeyMapping OPEN_WORKBENCH = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".open_workbench",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            CATEGORY
    );

    public static final KeyMapping TOGGLE_DEBUG_OVERLAY = new KeyMapping(
            "key." + JujutsuSorcery.MOD_ID + ".toggle_debug_overlay",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            CATEGORY
    );
}
