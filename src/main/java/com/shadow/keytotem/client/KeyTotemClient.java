package com.shadow.keytotem.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class KeyTotemClient implements ClientModInitializer {

    private static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        // 1. Register the Keybind
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.keytotem.equip", // The translation key for the keybind name
                InputUtil.Type.KEYSYM, // The type of input (keyboard)
                GLFW.GLFW_KEY_R, // The default key code (R)
                "key.categories.keytotem" // The translation key for the keybind category
        ));

        // 2. Register a client tick event listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 3. Check if the key was pressed
            while (keyBinding.wasPressed()) {
                // Ensure player and world exist
                if (client.player == null || client.interactionManager == null) {
                    return;
                }

                // 4. Check if the offhand already has a totem
                if (client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                    return; // Offhand is already equipped, do nothing
                }

                // 5. Find a totem in the main inventory (slots 0-35)
                for (int i = 0; i <= 35; i++) {
                    ItemStack currentStack = client.player.getInventory().getStack(i);
                    if (currentStack.isOf(Items.TOTEM_OF_UNDYING)) {
                        // 6. Swap the totem to the offhand
                        // Slot 40 is the offhand slot
                        client.interactionManager.clickSlot(
                                client.player.playerScreenHandler.syncId,
                                i < 9 ? i + 36 : i, // Convert hotbar slot index to inventory slot index
                                40,
                                SlotActionType.SWAP,
                                client.player
                        );
                        // Stop searching once a totem is found and moved
                        break;
                    }
                }
            }
        });
    }
}