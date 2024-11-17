package yalter.mousetweaks;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.Loader;
import yalter.mousetweaks.config.MTConfig;

public class Main extends DeobfuscationLayer {

    public static boolean DisableRMBTweak = false;

    private static GuiScreen oldGuiScreen = null;
    private static Object container = null;
    private static Slot oldSelectedSlot = null;
    private static Slot firstSlot = null;
    private static ItemStack oldStackOnMouse = null;
    private static boolean firstSlotClicked = false;
    private static boolean shouldClick = true;
    private static boolean disableForThisContainer = false;
    private static boolean disableWheelForThisContainer = false;
    private static int guiContainerID = 0;

    public static void initialise() {
        mc = Minecraft.getMinecraft();

        ModCompatibility.initialize();
        Constants.LOGGER.info("Mouse Tweaks has been initialised.");
    }

    public static void onUpdateInGame() {
        GuiScreen currentScreen = getCurrentScreen();
        if (currentScreen == null) {
            // Reset stuff
            oldGuiScreen = null;
            container = null;
            oldSelectedSlot = null;
            firstSlot = null;
            oldStackOnMouse = null;
            firstSlotClicked = false;
            shouldClick = true;
            disableForThisContainer = false;
            disableWheelForThisContainer = false;
            guiContainerID = Constants.NOTASSIGNED;
        } else {
            if (guiContainerID == Constants.NOTASSIGNED) {
                guiContainerID = getGuiContainerID(currentScreen);
            }
            onUpdateInGui(currentScreen);
        }
    }

    public static void onUpdateInGui(GuiScreen currentScreen) {

        if (oldGuiScreen != currentScreen) {
            oldGuiScreen = currentScreen;

            // If we opened an inventory from another inventory (for example, NEI's options menu).
            guiContainerID = getGuiContainerID(currentScreen);
            if (guiContainerID == Constants.NOTGUICONTAINER) return;

            container = getContainerWithID(currentScreen);
            disableForThisContainer = isDisabledForThisContainer(currentScreen);

            if (Constants.DEV_ENV) {
                Constants.LOGGER.debug(
                        new StringBuilder().append("You have just opened a ")
                                .append(getGuiContainerNameFromID(currentScreen)).append(" container (")
                                .append(currentScreen.getClass().getSimpleName())
                                .append((container == null) ? "" : "; ")
                                .append((container == null) ? "" : container.getClass().getSimpleName())
                                .append("), which has ").append(getSlotCountWithID(currentScreen)).append(" slots!")
                                .toString());
            }

            disableWheelForThisContainer = isWheelDisabledForThisContainer(currentScreen);

            if (isMouseWheelTransferActive() && !disableWheelForThisContainer) {
                Mouse.getDWheel(); // reset the mouse wheel delta
            }
        }

        if (guiContainerID == Constants.NOTGUICONTAINER) return;

        if ((Main.DisableRMBTweak || (!MTConfig.RMBTweak)) && !MTConfig.LMBTweakWithoutItem
                && !MTConfig.LMBTweakWithItem
                && !isMouseWheelTransferActive()) {
            return;
        }

        if (disableForThisContainer) return;

        // It's better to have this here, because there
        // are some inventories that change slot
        // count during runtime (for example NEI's crafting recipe GUI).
        int slotCount = getSlotCountWithID(currentScreen);

        if (slotCount == 0) {
            // If there are no slots, then there is nothing to do.
            return;
        }

        int wheel = isMouseWheelTransferActive() && !disableWheelForThisContainer ? Mouse.getDWheel() : 0;

        if (!Mouse.isButtonDown(1)) {
            firstSlotClicked = false;
            firstSlot = null;
            shouldClick = true;
        }

        Slot selectedSlot = getSelectedSlotWithID(currentScreen, slotCount);

        // Copy the stacks, so that they don't change while we do our stuff.
        ItemStack stackOnMouse = copyItemStack(getStackOnMouse());
        ItemStack targetStack = copyItemStack(getSlotStack(selectedSlot));

        // To correctly determine, when and how the default RMB drag needs to be disabled, we need a bunch of
        // conditions...
        if (Mouse.isButtonDown(1) && (oldStackOnMouse != stackOnMouse) && (oldStackOnMouse == null)) {
            shouldClick = false;
        }

        if (oldSelectedSlot != selectedSlot) {
            // ...and some more conditions.
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
                oldSelectedSlot = selectedSlot;

                if ((firstSlot != null) && !firstSlotClicked) {
                    firstSlotClicked = true;
                    disableRMBDragWithID(currentScreen);
                    firstSlot = null;
                }

                return;
            }

            if (Constants.DEV_ENV) {
                Constants.LOGGER
                        .debug("You have selected a new slot, it's slot number is " + getSlotNumber(selectedSlot));
            }

            boolean shiftIsDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

            if (Mouse.isButtonDown(1)) { // Right mouse button
                if ((MTConfig.RMBTweak) && !Main.DisableRMBTweak) {

                    if ((stackOnMouse != null) && areStacksCompatible(stackOnMouse, targetStack)
                            && !isCraftingOutputSlot(currentScreen, selectedSlot)) {
                        if ((firstSlot != null) && !firstSlotClicked) {
                            firstSlotClicked = true;
                            disableRMBDragWithID(currentScreen);
                            firstSlot = null;
                        } else {
                            shouldClick = false;
                            disableRMBDragWithID(currentScreen);
                        }

                        clickSlot(currentScreen, selectedSlot, 1, false);
                    }
                }
            } else if (Mouse.isButtonDown(0)) { // Left mouse button
                if (stackOnMouse != null) {
                    if (MTConfig.LMBTweakWithItem) {
                        if ((targetStack != null) && areStacksCompatible(stackOnMouse, targetStack)) {

                            if (shiftIsDown) { // If shift is down, we just shift-click the slot and the item gets moved
                                // into another inventory.
                                clickSlot(currentScreen, selectedSlot, 0, true);
                            } else { // If shift is not down, we need to merge the item stack on the mouse with the one
                                // in the slot.
                                if ((getItemStackSize(stackOnMouse) + getItemStackSize(targetStack))
                                        <= getMaxItemStackSize(stackOnMouse)) {
                                    // We need to click on the slot so that our item stack gets merged with it,
                                    // and then click again to return the stack to the mouse.
                                    // However, if the slot is crafting output, then the item is added to the mouse
                                    // stack
                                    // on the first click and we don't need to click the second time.
                                    clickSlot(currentScreen, selectedSlot, 0, false);
                                    if (!isCraftingOutputSlot(currentScreen, selectedSlot))
                                        clickSlot(currentScreen, selectedSlot, 0, false);
                                }
                            }
                        }
                    }
                } else if (MTConfig.LMBTweakWithoutItem) {
                    if (targetStack != null) {
                        if (shiftIsDown) {
                            clickSlot(currentScreen, selectedSlot, 0, true);
                        }
                    }
                }
            }

            oldSelectedSlot = selectedSlot;
        }

        if ((wheel != 0) && (selectedSlot != null)) {
            int numItemsToMove;
            if (!ModCompatibility.isLwjgl3Loaded() && MTConfig.ScrollItemScaling == 0) {
                numItemsToMove = Math.abs(wheel) / 120;
            } else {
                numItemsToMove = 1;
            }

            if (Constants.DEV_ENV) {
                Constants.LOGGER.debug("numItemsToMove: " + numItemsToMove);
            }

            if (slotCount > Constants.INVENTORY_SIZE) {
                ItemStack originalStack = getSlotStack(selectedSlot);
                boolean isCraftingOutput = isCraftingOutputSlot(currentScreen, selectedSlot);

                if ((originalStack != null) && ((stackOnMouse == null)
                        || (isCraftingOutput ? areStacksCompatible(originalStack, stackOnMouse)
                                : !areStacksCompatible(originalStack, stackOnMouse)))) {
                    do {
                        Slot applicableSlot = null;

                        boolean pushItems = (wheel < 0);
                        if ((MTConfig.WheelScrollDirection == 2 || MTConfig.WheelScrollDirection == 3)
                                && otherInventoryIsAbove(selectedSlot, getSlots(asContainer(container)))) {
                            pushItems = !pushItems;
                        }
                        if (MTConfig.WheelScrollDirection == 1 || MTConfig.WheelScrollDirection == 3) {
                            pushItems = !pushItems;
                        }

                        int slotCounter = 0;
                        int countUntil = slotCount - Constants.INVENTORY_SIZE;
                        if (getSlotNumber(selectedSlot) < countUntil) {
                            slotCounter = countUntil;
                            countUntil = slotCount;
                        }

                        if (pushItems || (MTConfig.WheelSearchOrder == 0)) {
                            for (int i = slotCounter; i < countUntil; i++) {
                                Slot sl = getSlotWithID(currentScreen, i);
                                ItemStack stackSl = getSlotStack(sl);

                                if (stackSl == null) {
                                    if ((applicableSlot == null) && pushItems
                                            && sl.isItemValid(originalStack)
                                            && !isCraftingOutputSlot(currentScreen, sl)) {
                                        applicableSlot = sl;
                                    }
                                } else if (areStacksCompatible(originalStack, stackSl)) {
                                    if (pushItems && (stackSl.stackSize < stackSl.getMaxStackSize())) {
                                        applicableSlot = sl;
                                        break;
                                    } else if (!pushItems) {
                                        applicableSlot = sl;
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (int i = countUntil - 1; i >= slotCounter; i--) {
                                Slot sl = getSlotWithID(currentScreen, i);
                                ItemStack stackSl = getSlotStack(sl);

                                if (stackSl == null) {
                                    if ((applicableSlot == null) && pushItems && sl.isItemValid(originalStack)) {
                                        applicableSlot = sl;
                                    }
                                } else if (areStacksCompatible(originalStack, stackSl)) {
                                    if (pushItems && (stackSl.stackSize < stackSl.getMaxStackSize())) {
                                        applicableSlot = sl;
                                        break;
                                    } else if (!pushItems) {
                                        applicableSlot = sl;
                                        break;
                                    }
                                }
                            }
                        }

                        if (isCraftingOutput) {
                            if (pushItems) {
                                boolean mouseWasEmpty = stackOnMouse == null;

                                for (int i = 0; i < numItemsToMove; i++) {
                                    clickSlot(currentScreen, selectedSlot, 0, false);
                                }

                                if ((applicableSlot != null) && mouseWasEmpty) {
                                    clickSlot(currentScreen, applicableSlot, 0, false);
                                }
                            }

                            break;
                        }

                        if (applicableSlot != null) {
                            Slot slotTo = pushItems ? applicableSlot : selectedSlot;
                            Slot slotFrom = pushItems ? selectedSlot : applicableSlot;
                            ItemStack stackTo = (getSlotStack(slotTo) != null) ? copyItemStack(getSlotStack(slotTo))
                                    : null;
                            ItemStack stackFrom = copyItemStack(getSlotStack(slotFrom));

                            if (pushItems) {
                                numItemsToMove = Math.min(numItemsToMove, getItemStackSize(stackFrom));

                                if ((stackTo != null) && ((getMaxItemStackSize(stackTo) - getItemStackSize(stackTo))
                                        <= numItemsToMove)) {
                                    clickSlot(currentScreen, slotFrom, 0, false);
                                    clickSlot(currentScreen, slotTo, 0, false);
                                    clickSlot(currentScreen, slotFrom, 0, false);

                                    numItemsToMove -= getMaxItemStackSize(stackTo) - getItemStackSize(stackTo);
                                } else {
                                    clickSlot(currentScreen, slotFrom, 0, false);

                                    if (getItemStackSize(stackFrom) <= numItemsToMove) {
                                        clickSlot(currentScreen, slotTo, 0, false);
                                    } else {
                                        for (int i = 0; i < numItemsToMove; i++) {
                                            clickSlot(currentScreen, slotTo, 1, false);
                                        }
                                    }

                                    clickSlot(currentScreen, slotFrom, 0, false);

                                    numItemsToMove = 0;
                                }
                            } else {
                                if ((getMaxItemStackSize(stackTo) - getItemStackSize(stackTo)) <= numItemsToMove) {
                                    clickSlot(currentScreen, slotFrom, 0, false);
                                    clickSlot(currentScreen, slotTo, 0, false);
                                    clickSlot(currentScreen, slotFrom, 0, false);
                                } else {
                                    clickSlot(currentScreen, slotFrom, 0, false);

                                    if (getItemStackSize(stackFrom) <= numItemsToMove) {
                                        clickSlot(currentScreen, slotTo, 0, false);
                                        numItemsToMove -= getMaxItemStackSize(stackFrom);
                                    } else {
                                        for (int i = 0; i < numItemsToMove; i++) {
                                            clickSlot(currentScreen, slotTo, 1, false);
                                        }

                                        numItemsToMove = 0;
                                    }

                                    clickSlot(currentScreen, slotFrom, 0, false);
                                }

                                if (getMaxItemStackSize(stackTo) == getMaxItemStackSize(stackTo)) {
                                    numItemsToMove = 0;
                                }
                            }
                        } else {
                            break;
                        }
                    } while (numItemsToMove != 0);
                }
            }
        }

        oldStackOnMouse = stackOnMouse;
    }

    private static final boolean isNEIPresent = Loader.isModLoaded("NotEnoughItems");

    private static boolean isMouseWheelTransferActive() {
        if (isNEIPresent) {
            return MTConfig.WheelTweak && !codechicken.nei.NEIClientConfig.isMouseScrollTransferEnabled();
        }
        return MTConfig.WheelTweak;
    }

    // Returns true if the other inventory is above the selected slot inventory.
    //
    // This is used for the inventory position aware scroll direction. To prevent any surprises, this should have the
    // same logic for what constitutes the "other" inventory as elsewhere.
    private static boolean otherInventoryIsAbove(Slot selectedSlot, List<Slot> slots) {
        boolean selectedIsInPlayerInventory = (getSlotInventory(selectedSlot) == getInventoryPlayer());

        // Count the number of "other inventory" slots below and above the selected slot.
        int otherInventorySlotsBelow = 0, otherInventorySlotsAbove = 0;
        for (Slot slot : slots) {
            if ((getSlotInventory(slot) == getInventoryPlayer()) != selectedIsInPlayerInventory) {
                if (getSlotYPosition(slot) < getSlotYPosition(selectedSlot)) otherInventorySlotsAbove++;
                else otherInventorySlotsBelow++;
            }
        }

        // If there are more "other inventory" slots above the selected slot, consider the other inventory above.
        return (otherInventorySlotsAbove > otherInventorySlotsBelow);
    }

    public static int getGuiContainerID(GuiScreen currentScreen) {
        // This first because a lot of mod extend the vanilla Minecraft one.
        int containerID = ModCompatibility.getModGuiContainerID(currentScreen);
        if (containerID == Constants.NOTGUICONTAINER)
            return (isGuiContainer(currentScreen) && isValidGuiContainer(currentScreen)) ? Constants.MINECRAFT
                    : Constants.NOTGUICONTAINER;
        else return containerID;
    }

    public static Object getContainerWithID(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return getContainer(asGuiContainer(currentScreen));
        else return ModCompatibility.getModContainer(guiContainerID, currentScreen);
    }

    public static int getSlotCountWithID(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return getSlots(asContainer(container)).size();
        else return ModCompatibility.getModSlotCount(guiContainerID, currentScreen, container);
    }

    public static String getGuiContainerNameFromID(GuiScreen currentScreen) {
        switch (guiContainerID) {
            case Constants.NOTASSIGNED:
                return "Unknown";
            case Constants.NOTGUICONTAINER:
                return "Wrong";
            case Constants.MINECRAFT:
                return "Vanilla Minecraft";

            default:
                return ModCompatibility.getModNameFromModGuiContainerID(guiContainerID, currentScreen);
        }
    }

    public static boolean isDisabledForThisContainer(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return false;
        else return ModCompatibility.isDisabledForThisModContainer(guiContainerID, currentScreen, container);
    }

    public static boolean isWheelDisabledForThisContainer(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return false;
        else return ModCompatibility.isWheelDisabledForThisModContainer(guiContainerID, currentScreen);
    }

    public static Slot getSelectedSlotWithID(GuiScreen currentScreen, int slotCount) {
        if (guiContainerID == Constants.MINECRAFT)
            return getSelectedSlot(asGuiContainer(currentScreen), asContainer(container), slotCount);
        else return ModCompatibility.getModSelectedSlot(guiContainerID, currentScreen, container, slotCount);
    }

    public static void clickSlot(GuiScreen currentScreen, Slot targetSlot, int mouseButton, boolean shiftPressed) {
        if (guiContainerID == Constants.MINECRAFT) {
            windowClick(
                    getWindowId(asContainer(container)),
                    getSlotNumber(targetSlot),
                    mouseButton,
                    shiftPressed ? 1 : 0);
        } else {
            ModCompatibility
                    .modClickSlot(guiContainerID, currentScreen, container, targetSlot, mouseButton, shiftPressed);
        }
    }

    public static boolean isCraftingOutputSlot(GuiScreen currentScreen, Slot targetSlot) {
        if (guiContainerID == Constants.MINECRAFT)
            return isVanillaCraftingOutputSlot(asContainer(container), targetSlot);
        else return ModCompatibility.modIsCraftingOutputSlot(guiContainerID, currentScreen, container, targetSlot);
    }

    public static Slot getSlotWithID(GuiScreen currentScreen, int slotNumber) {
        if (guiContainerID == Constants.MINECRAFT) return getSlot(asContainer(container), slotNumber);
        else return ModCompatibility.modGetSlot(guiContainerID, currentScreen, container, slotNumber);
    }

    public static void disableRMBDragWithID(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) {
            disableVanillaRMBDrag(asGuiContainer(currentScreen));

            if (shouldClick) {
                clickSlot(currentScreen, firstSlot, 1, false);
            }
        } else {
            ModCompatibility.disableRMBDragIfRequired(guiContainerID, currentScreen, container, firstSlot, shouldClick);
        }
    }
}
