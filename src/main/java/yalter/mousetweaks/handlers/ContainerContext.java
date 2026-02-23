package yalter.mousetweaks.handlers;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;

import org.lwjgl.input.Mouse;

import yalter.mousetweaks.Constants;
import yalter.mousetweaks.DeobfuscationLayer;
import yalter.mousetweaks.ModCompatibility;

public class ContainerContext extends DeobfuscationLayer {

    private GuiScreen oldGuiScreen = null;
    private Object container = null;
    private boolean disableForThisContainer = false;
    private boolean disableWheelForThisContainer = false;
    private int guiContainerID = Constants.NOTASSIGNED;

    public void reset() {
        oldGuiScreen = null;
        container = null;
        disableForThisContainer = false;
        disableWheelForThisContainer = false;
        guiContainerID = Constants.NOTASSIGNED;
    }

    public boolean refresh(GuiScreen currentScreen, boolean wheelTransferActive) {
        if (oldGuiScreen == currentScreen) return true;
        oldGuiScreen = currentScreen;

        // If we opened an inventory from another inventory (for example, NEI's options menu).
        guiContainerID = resolveGuiContainerID(currentScreen);
        if (guiContainerID == Constants.NOTGUICONTAINER) return false;

        container = getContainerWithID(currentScreen);
        disableForThisContainer = isDisabledForThisContainer(currentScreen);

        if (Constants.DEV_ENV) {
            Constants.LOGGER.debug(
                    new StringBuilder().append("You have just opened a ")
                            .append(getGuiContainerNameFromID(currentScreen)).append(" container (")
                            .append(currentScreen.getClass().getSimpleName()).append((container == null) ? "" : "; ")
                            .append((container == null) ? "" : container.getClass().getSimpleName())
                            .append("), which has ").append(getSlotCount(currentScreen)).append(" slots!").toString());
        }

        disableWheelForThisContainer = isWheelDisabledForThisContainer(currentScreen);
        if (wheelTransferActive && !disableWheelForThisContainer) {
            Mouse.getDWheel(); // reset the mouse wheel delta
        }

        return true;
    }

    public int getCurrentGuiContainerID() {
        return guiContainerID;
    }

    public boolean isDisabledForCurrentContainer() {
        return disableForThisContainer;
    }

    public boolean isWheelDisabledForCurrentContainer() {
        return disableWheelForThisContainer;
    }

    public int getSlotCount(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return getSlots(asContainer(container)).size();
        return ModCompatibility.getModSlotCount(guiContainerID, currentScreen, container);
    }

    public Slot getSelectedSlot(GuiScreen currentScreen, int slotCount) {
        if (guiContainerID == Constants.MINECRAFT) {
            return getSelectedSlot(asGuiContainer(currentScreen), asContainer(container), slotCount);
        }
        return ModCompatibility.getModSelectedSlot(guiContainerID, currentScreen, container, slotCount);
    }

    public Slot getSlot(GuiScreen currentScreen, int slotNumber) {
        if (guiContainerID == Constants.MINECRAFT) return getSlot(asContainer(container), slotNumber);
        return ModCompatibility.modGetSlot(guiContainerID, currentScreen, container, slotNumber);
    }

    public void clickSlot(GuiScreen currentScreen, Slot targetSlot, int mouseButton, boolean shiftPressed) {
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

    public boolean isCraftingOutputSlot(GuiScreen currentScreen, Slot targetSlot) {
        if (guiContainerID == Constants.MINECRAFT) {
            return isVanillaCraftingOutputSlot(asContainer(container), targetSlot);
        }
        return ModCompatibility.modIsCraftingOutputSlot(guiContainerID, currentScreen, container, targetSlot);
    }

    public void disableRMBDragIfRequired(GuiScreen currentScreen, Slot firstSlot, boolean shouldClick) {
        if (guiContainerID == Constants.MINECRAFT) {
            disableVanillaRMBDrag(asGuiContainer(currentScreen));

            if (shouldClick) {
                clickSlot(currentScreen, firstSlot, 1, false);
            }
        } else {
            ModCompatibility.disableRMBDragIfRequired(guiContainerID, currentScreen, container, firstSlot, shouldClick);
        }
    }

    public List<Slot> getContainerSlots() {
        return getSlots(asContainer(container));
    }

    private int resolveGuiContainerID(GuiScreen currentScreen) {
        // This first because a lot of mods extend the vanilla Minecraft one.
        int containerID = ModCompatibility.getModGuiContainerID(currentScreen);
        if (containerID == Constants.NOTGUICONTAINER) {
            return (isGuiContainer(currentScreen) && isValidGuiContainer(currentScreen)) ? Constants.MINECRAFT
                    : Constants.NOTGUICONTAINER;
        }
        return containerID;
    }

    private Object getContainerWithID(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return getContainer(asGuiContainer(currentScreen));
        return ModCompatibility.getModContainer(guiContainerID, currentScreen);
    }

    private String getGuiContainerNameFromID(GuiScreen currentScreen) {
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

    private boolean isDisabledForThisContainer(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return false;
        return ModCompatibility.isDisabledForThisModContainer(guiContainerID, currentScreen, container);
    }

    private boolean isWheelDisabledForThisContainer(GuiScreen currentScreen) {
        if (guiContainerID == Constants.MINECRAFT) return false;
        return ModCompatibility.isWheelDisabledForThisModContainer(guiContainerID, currentScreen);
    }
}
