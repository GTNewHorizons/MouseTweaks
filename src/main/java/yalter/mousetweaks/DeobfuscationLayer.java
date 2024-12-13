package yalter.mousetweaks;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

public class DeobfuscationLayer {

    protected static Minecraft mc;

    protected static GuiScreen getCurrentScreen() {
        return mc.currentScreen;
    }

    protected static boolean isGuiContainer(GuiScreen guiScreen) {
        return guiScreen instanceof GuiContainer;
    }

    protected static boolean isValidGuiContainer(GuiScreen guiScreen) {
        return (guiScreen != null) && !(guiScreen.getClass().getSimpleName().contains("CJB_GuiCrafting"))
                && !(guiScreen.getClass().equals(GuiContainerCreative.class));
    }

    protected static boolean isVanillaCraftingOutputSlot(Container container, Slot slot) {
        return ((container instanceof ContainerWorkbench) && (getSlotNumber(slot) == 0))
                || ((container instanceof ContainerPlayer) && (getSlotNumber(slot) == 0))
                || ((container instanceof ContainerFurnace) && (getSlotNumber(slot) == 2))
                || ((container instanceof ContainerRepair) && (getSlotNumber(slot) == 2));
    }

    protected static GuiContainer asGuiContainer(GuiScreen guiScreen) {
        return (GuiContainer) guiScreen;
    }

    protected static Container asContainer(Object obj) {
        return (Container) obj;
    }

    protected static Slot asSlot(Object obj) {
        return (Slot) obj;
    }

    protected static Container getContainer(GuiContainer guiContainer) {
        return guiContainer.inventorySlots;
    }

    protected static List<Slot> getSlots(Container container) {
        return container.inventorySlots;
    }

    protected static Slot getSlot(Container container, int index) {
        return getSlots(container).get(index);
    }

    protected static ItemStack getSlotStack(Slot slot) {
        return (slot == null) ? null : slot.getStack();
    }

    protected static int getWindowId(Container container) {
        return container.windowId;
    }

    protected static void windowClick(int windowId, int slotNumber, int mouseButton, int shiftPressed) {
        // if (slotNumber != -1) {
        getPlayerController().windowClick(windowId, slotNumber, mouseButton, shiftPressed, getThePlayer());
        // }
    }

    protected static EntityClientPlayerMP getThePlayer() {
        return mc.thePlayer;
    }

    protected static InventoryPlayer getInventoryPlayer() {
        return getThePlayer().inventory;
    }

    protected static int getDisplayWidth() {
        return mc.displayWidth;
    }

    protected static int getDisplayHeight() {
        return mc.displayHeight;
    }

    protected static ItemStack getStackOnMouse() {
        return getInventoryPlayer().getItemStack();
    }

    protected static PlayerControllerMP getPlayerController() {
        return mc.playerController;
    }

    protected static IInventory getSlotInventory(Slot slot) {
        return slot.inventory;
    }

    protected static int getSlotNumber(Slot slot) {
        return slot.slotNumber;
    }

    protected static int getSlotYPosition(Slot slot) {
        return slot.yDisplayPosition;
    }

    protected static int getItemStackSize(ItemStack itemStack) {
        return itemStack.stackSize;
    }

    protected static int getMaxItemStackSize(ItemStack itemStack) {
        return itemStack.getMaxStackSize();
    }

    protected static ItemStack copyItemStack(ItemStack itemStack) {
        return (itemStack == null) ? null : itemStack.copy();
    }

    protected static boolean areStacksCompatible(ItemStack itemStack1, ItemStack itemStack2) {
        return ((itemStack1 == null) || (itemStack2 == null))
                || (itemStack1.isItemEqual(itemStack2) && ItemStack.areItemStackTagsEqual(itemStack1, itemStack2));
    }

    protected static Slot getSelectedSlot(GuiContainer guiContainer, Container container, int slotCount) {
        for (int i = 0; i < slotCount; i++) {
            Slot slot = getSlot(container, i);
            if (guiContainer.isMouseOverSlot(slot, getRequiredMouseX(), getRequiredMouseY())) return slot;
        }

        return null;
    }

    /**
     * Disables the vanilla RMB drag mechanic in the given GuiContainer. If your guiContainer is based on the vanilla
     * GuiContainer, you can use this method to disable the RMB drag.
     *
     * @param guiContainer The guiContainer to disable RMB drag in.
     */
    public static void disableVanillaRMBDrag(GuiContainer guiContainer) {
        guiContainer.field_146995_H = true;
        guiContainer.field_147007_t = false;
    }

    protected static int getRequiredMouseX() {
        ScaledResolution var8 = new ScaledResolution(mc, getDisplayWidth(), getDisplayHeight());
        int var9 = var8.getScaledWidth();
        return (Mouse.getX() * var9) / getDisplayWidth();
    }

    protected static int getRequiredMouseY() {
        ScaledResolution var8 = new ScaledResolution(mc, getDisplayWidth(), getDisplayHeight());
        int var10 = var8.getScaledHeight();
        return var10 - ((Mouse.getY() * var10) / getDisplayHeight()) - 1;
    }
}
