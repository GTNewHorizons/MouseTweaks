package yalter.mousetweaks.compat;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import com.cleanroommc.modularui.api.widget.IVanillaSlot;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularContainer;

import cpw.mods.fml.common.Optional;

public class ModularUICompat {

    @Optional.Method(modid = "modularui2")
    public static boolean isModularContainer(Container container) {
        return container instanceof ModularContainer;
    }

    @Optional.Method(modid = "modularui2")
    public static Slot getSelectedSlot(Container container) {
        IWidget hovered = ((ModularContainer) container).getScreen().getContext().getTopHovered();
        if (hovered instanceof IVanillaSlot) {
            IVanillaSlot vanillaSlot = (IVanillaSlot) hovered;
            if (vanillaSlot.handleAsVanillaSlot()) return vanillaSlot.getVanillaSlot();
        }
        return null;
    }
}
