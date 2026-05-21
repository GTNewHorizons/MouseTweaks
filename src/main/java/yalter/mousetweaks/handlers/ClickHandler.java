package yalter.mousetweaks.handlers;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import yalter.mousetweaks.Constants;
import yalter.mousetweaks.DeobfuscationLayer;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.config.MTConfig;

public class ClickHandler extends DeobfuscationLayer {

    private Slot oldSelectedSlot = null;
    private Slot firstSlot = null;
    private ItemStack oldStackOnMouse = null;
    private boolean firstSlotClicked = false;
    private boolean shouldClick = true;

    public void reset() {
        oldSelectedSlot = null;
        firstSlot = null;
        oldStackOnMouse = null;
        firstSlotClicked = false;
        shouldClick = true;
    }

    public void resetDragStateOnRightButtonRelease() {
        if (!Mouse.isButtonDown(1)) {
            firstSlotClicked = false;
            firstSlot = null;
            shouldClick = true;
        }
    }

    public void updateShouldClickFromRmbState(ItemStack stackOnMouse) {
        // To correctly determine when and how the default RMB drag needs to be disabled,
        // we need to observe the state transition from empty mouse stack.
        if (Mouse.isButtonDown(1) && (oldStackOnMouse != stackOnMouse) && (oldStackOnMouse == null)) {
            shouldClick = false;
        }
    }

    public boolean handleSlotChangeAndButtons(GuiScreen currentScreen, Slot selectedSlot, ItemStack stackOnMouse,
            ItemStack targetStack, ContainerContext context) {
        if (oldSelectedSlot == selectedSlot) return true;

        if (Mouse.isButtonDown(1) && !firstSlotClicked && (firstSlot == null) && (oldSelectedSlot != null)) {
            if (!areStacksCompatible(stackOnMouse, getSlotStack(oldSelectedSlot))) {
                shouldClick = false;
            }

            firstSlot = oldSelectedSlot;
        }

        if (Mouse.isButtonDown(1) && (oldSelectedSlot == null) && !firstSlotClicked && (firstSlot == null)) {
            shouldClick = false;
        }

        if (selectedSlot == null) {
            oldSelectedSlot = null;

            if ((firstSlot != null) && !firstSlotClicked) {
                firstSlotClicked = true;
                context.disableRMBDragIfRequired(currentScreen, firstSlot, shouldClick);
                firstSlot = null;
            }

            return false;
        }

        if (Constants.DEV_ENV) {
            Constants.LOGGER.debug("You have selected a new slot, its slot number is " + getSlotNumber(selectedSlot));
        }

        boolean shiftIsDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (Mouse.isButtonDown(1)) { // Right mouse button
            handleRMBTweak(currentScreen, selectedSlot, stackOnMouse, targetStack, context);
        } else if (Mouse.isButtonDown(0)) { // Left mouse button
            handleLMBTweak(currentScreen, selectedSlot, stackOnMouse, targetStack, shiftIsDown, context);
        }

        oldSelectedSlot = selectedSlot;
        return true;
    }

    public void setOldStackOnMouse(ItemStack stackOnMouse) {
        oldStackOnMouse = stackOnMouse;
    }

    private void handleRMBTweak(GuiScreen currentScreen, Slot selectedSlot, ItemStack stackOnMouse,
            ItemStack targetStack, ContainerContext context) {
        if (!MTConfig.RMBTweak || Main.DisableRMBTweak) return;

        if ((stackOnMouse != null) && areStacksCompatible(stackOnMouse, targetStack)
                && !context.isCraftingOutputSlot(currentScreen, selectedSlot)) {
            if ((firstSlot != null) && !firstSlotClicked) {
                firstSlotClicked = true;
                context.disableRMBDragIfRequired(currentScreen, firstSlot, shouldClick);
                firstSlot = null;
            } else {
                shouldClick = false;
                Slot fallbackSlot = (firstSlot != null) ? firstSlot : selectedSlot;
                context.disableRMBDragIfRequired(currentScreen, fallbackSlot, shouldClick);
            }

            context.clickSlot(currentScreen, selectedSlot, 1, false);
        }
    }

    private void handleLMBTweak(GuiScreen currentScreen, Slot selectedSlot, ItemStack stackOnMouse,
            ItemStack targetStack, boolean shiftIsDown, ContainerContext context) {
        if (stackOnMouse != null) {
            if (MTConfig.LMBTweakWithItem && (targetStack != null) && areStacksCompatible(stackOnMouse, targetStack)) {
                boolean isCraftingOutput = context.isCraftingOutputSlot(currentScreen, selectedSlot);
                if (shiftIsDown) {
                    if (!isCraftingOutput) {
                        context.clickSlot(currentScreen, selectedSlot, 0, true);
                    }
                } else if (asGuiContainer(currentScreen).field_147007_t == false // dragSplitting
                        && (getItemStackSize(stackOnMouse) + getItemStackSize(targetStack))
                                <= getMaxItemStackSize(stackOnMouse)) {
                                    context.clickSlot(currentScreen, selectedSlot, 0, false);
                                    if (!isCraftingOutput) {
                                        context.clickSlot(currentScreen, selectedSlot, 0, false);
                                    }
                                }
            }
        } else if (MTConfig.LMBTweakWithoutItem && (targetStack != null) && shiftIsDown) {
            context.clickSlot(currentScreen, selectedSlot, 0, true);
        }
    }
}
