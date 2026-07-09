package yalter.mousetweaks.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import yalter.mousetweaks.DeobfuscationLayer;
import yalter.mousetweaks.config.MTConfig;

public class ClickCreativeHandler extends DeobfuscationLayer {

    public static void handler(GuiScreen currentScreen, ContainerContext context, Slot selectedSlot,
            ItemStack stackOnMouse, boolean shiftIsDown, ItemStack targetStack) {
        if (stackOnMouse == null && MTConfig.LMBTweakWithoutItem
                && (targetStack != null)
                && shiftIsDown
                && Mouse.isButtonDown(0)) {
            GuiContainerCreative gcc = (GuiContainerCreative) currentScreen;
            if (gcc.func_147056_g() == CreativeTabs.tabInventory.getTabIndex()) {
                windowClick(context.getWindowId(), selectedSlot.getSlotIndex(), 0, 1);
            } else {
                gcc.inventorySlots.slotClick(selectedSlot.slotNumber, 0, 1, getThePlayer());
                gcc.mc.playerController.sendSlotPacket(
                        gcc.inventorySlots.getSlot(selectedSlot.slotNumber).getStack(),
                        selectedSlot.slotNumber - gcc.inventorySlots.inventorySlots.size() + 45);
            }
        }
    }
}
