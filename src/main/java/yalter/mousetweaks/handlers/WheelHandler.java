package yalter.mousetweaks.handlers;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import yalter.mousetweaks.Constants;
import yalter.mousetweaks.DeobfuscationLayer;
import yalter.mousetweaks.ModCompatibility;
import yalter.mousetweaks.config.MTConfig;

public final class WheelHandler extends DeobfuscationLayer {

    private WheelHandler() {}

    public static void handleWheelTweak(GuiScreen currentScreen, Slot selectedSlot, ItemStack stackOnMouse,
            int slotCount, int wheel, ContainerContext context) {
        if ((wheel == 0) || (selectedSlot == null)) return;

        int numItemsToMove = getNumItemsToMoveFromWheel(wheel);
        if (Constants.DEV_ENV) {
            Constants.LOGGER.debug("numItemsToMove: " + numItemsToMove);
        }

        if (slotCount <= Constants.INVENTORY_SIZE) return;

        ItemStack originalStack = getSlotStack(selectedSlot);
        boolean isCraftingOutput = context.isCraftingOutputSlot(currentScreen, selectedSlot);
        if ((originalStack == null)
                || ((stackOnMouse != null) && (isCraftingOutput ? !areStacksCompatible(originalStack, stackOnMouse)
                        : areStacksCompatible(originalStack, stackOnMouse)))) {
            return;
        }

        do {
            Slot applicableSlot = null;
            boolean pushItems = shouldPushItems(wheel, selectedSlot, context);

            int slotCounter = 0;
            int countUntil = slotCount - Constants.INVENTORY_SIZE;
            if (getSlotNumber(selectedSlot) < countUntil) {
                slotCounter = countUntil;
                countUntil = slotCount;
            }

            if (pushItems || (MTConfig.WheelSearchOrder == 0)) {
                for (int i = slotCounter; i < countUntil; i++) {
                    Slot sl = context.getSlot(currentScreen, i);
                    ItemStack stackSl = getSlotStack(sl);

                    if (stackSl == null) {
                        if ((applicableSlot == null) && pushItems
                                && sl.isItemValid(originalStack)
                                && !context.isCraftingOutputSlot(currentScreen, sl)) {
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
                    Slot sl = context.getSlot(currentScreen, i);
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
                        context.clickSlot(currentScreen, selectedSlot, 0, false);
                    }

                    if ((applicableSlot != null) && mouseWasEmpty) {
                        context.clickSlot(currentScreen, applicableSlot, 0, false);
                    }
                }

                break;
            }

            if (applicableSlot == null) break;

            Slot slotTo = pushItems ? applicableSlot : selectedSlot;
            Slot slotFrom = pushItems ? selectedSlot : applicableSlot;
            ItemStack stackTo = (getSlotStack(slotTo) != null) ? copyItemStack(getSlotStack(slotTo)) : null;
            ItemStack stackFrom = copyItemStack(getSlotStack(slotFrom));

            if (pushItems) {
                numItemsToMove = Math.min(numItemsToMove, getItemStackSize(stackFrom));

                if ((stackTo != null)
                        && ((getMaxItemStackSize(stackTo) - getItemStackSize(stackTo)) <= numItemsToMove)) {
                    context.clickSlot(currentScreen, slotFrom, 0, false);
                    context.clickSlot(currentScreen, slotTo, 0, false);
                    context.clickSlot(currentScreen, slotFrom, 0, false);

                    numItemsToMove -= getMaxItemStackSize(stackTo) - getItemStackSize(stackTo);
                } else {
                    context.clickSlot(currentScreen, slotFrom, 0, false);

                    if (getItemStackSize(stackFrom) <= numItemsToMove) {
                        context.clickSlot(currentScreen, slotTo, 0, false);
                    } else {
                        for (int i = 0; i < numItemsToMove; i++) {
                            context.clickSlot(currentScreen, slotTo, 1, false);
                        }
                    }

                    context.clickSlot(currentScreen, slotFrom, 0, false);
                    numItemsToMove = 0;
                }
            } else {
                int availableInTarget = getMaxItemStackSize(stackTo) - getItemStackSize(stackTo);
                if (availableInTarget <= 0) {
                    break;
                }

                if (availableInTarget <= numItemsToMove) {
                    context.clickSlot(currentScreen, slotFrom, 0, false);
                    context.clickSlot(currentScreen, slotTo, 0, false);
                    context.clickSlot(currentScreen, slotFrom, 0, false);

                    numItemsToMove -= availableInTarget;
                } else {
                    context.clickSlot(currentScreen, slotFrom, 0, false);

                    if (getItemStackSize(stackFrom) <= numItemsToMove) {
                        context.clickSlot(currentScreen, slotTo, 0, false);
                        numItemsToMove -= getItemStackSize(stackFrom);
                    } else {
                        for (int i = 0; i < numItemsToMove; i++) {
                            context.clickSlot(currentScreen, slotTo, 1, false);
                        }

                        numItemsToMove = 0;
                    }

                    context.clickSlot(currentScreen, slotFrom, 0, false);
                }
            }
        } while (numItemsToMove != 0);
    }

    private static int getNumItemsToMoveFromWheel(int wheel) {
        if (!ModCompatibility.isLwjgl3Loaded() && MTConfig.ScrollItemScaling == 0) {
            return Math.abs(wheel) / 120;
        }
        return 1;
    }

    private static boolean shouldPushItems(int wheel, Slot selectedSlot, ContainerContext context) {
        boolean pushItems = (wheel < 0);
        if ((MTConfig.WheelScrollDirection == 2 || MTConfig.WheelScrollDirection == 3)
                && otherInventoryIsAbove(selectedSlot, context.getContainerSlots())) {
            pushItems = !pushItems;
        }
        if (MTConfig.WheelScrollDirection == 1 || MTConfig.WheelScrollDirection == 3) {
            pushItems = !pushItems;
        }
        return pushItems;
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
}
