package yalter.mousetweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.Loader;
import yalter.mousetweaks.config.MTConfig;
import yalter.mousetweaks.handlers.ClickHandler;
import yalter.mousetweaks.handlers.ContainerContext;
import yalter.mousetweaks.handlers.WheelHandler;

public class Runtime extends DeobfuscationLayer {

    private static final ContainerContext context = new ContainerContext();
    private static final ClickHandler clickHandler = new ClickHandler();

    private static final boolean isNEIPresent = Loader.isModLoaded("NotEnoughItems");

    public static void initialise() {
        mc = Minecraft.getMinecraft();

        ModCompatibility.initialize();
        Constants.LOGGER.info("Mouse Tweaks has been initialised.");
    }

    public static void onUpdateInGame() {
        GuiScreen currentScreen = getCurrentScreen();
        if (currentScreen == null) {
            context.reset();
            clickHandler.reset();
            return;
        }

        onUpdateInGui(currentScreen);
    }

    public static void onUpdateInGui(GuiScreen currentScreen) {
        boolean wheelTransferActive = isMouseWheelTransferActive();

        if (!context.refresh(currentScreen, wheelTransferActive)) return;
        if (context.getCurrentGuiContainerID() == Constants.NOTGUICONTAINER) return;
        if (!anyTweaksEnabled()) return;
        if (context.isDisabledForCurrentContainer()) return;

        // It's better to have this here, because there
        // are some inventories that change slot
        // count during runtime (for example NEI's crafting recipe GUI).
        int slotCount = context.getSlotCount(currentScreen);

        if (slotCount == 0) {
            // If there are no slots, then there is nothing to do.
            return;
        }

        int wheel = wheelTransferActive && !context.isWheelDisabledForCurrentContainer() ? Mouse.getDWheel() : 0;
        clickHandler.resetDragStateOnRightButtonRelease();

        Slot selectedSlot = context.getSelectedSlot(currentScreen, slotCount);

        // Copy the stacks, so that they don't change while we do our stuff.
        ItemStack stackOnMouse = copyItemStack(getStackOnMouse());
        ItemStack targetStack = copyItemStack(getSlotStack(selectedSlot));

        clickHandler.updateShouldClickFromRmbState(stackOnMouse);

        if (!clickHandler.handleSlotChangeAndButtons(currentScreen, selectedSlot, stackOnMouse, targetStack, context)) {
            return;
        }

        WheelHandler.handleWheelTweak(currentScreen, selectedSlot, stackOnMouse, slotCount, wheel, context);
        clickHandler.setOldStackOnMouse(stackOnMouse);
    }

    private static boolean anyTweaksEnabled() {
        return (MTConfig.RMBTweak && !Main.DisableRMBTweak) || MTConfig.LMBTweakWithoutItem
                || MTConfig.LMBTweakWithItem
                || isMouseWheelTransferActive();
    }

    private static boolean isMouseWheelTransferActive() {
        if (isNEIPresent) {
            return MTConfig.WheelTweak && !codechicken.nei.NEIClientConfig.isMouseScrollTransferEnabled();
        }
        return MTConfig.WheelTweak;
    }
}
