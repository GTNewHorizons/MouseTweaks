package yalter.mousetweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.inventory.Slot;

public class Reflection {

    public static ReflectionCache forestry;
    public static ReflectionCache codechickencore;
    public static ReflectionCache NEI;

    public static boolean reflectForestry() {
        forestry = new ReflectionCache();

        Class<?> guiForestryClass = getClass("forestry.core.gui.GuiForestry");
        if (guiForestryClass != null) {
            forestry.storeClass("GuiForestry", guiForestryClass);
            forestry.available = true;

            Field inventorySlotsField = getField(guiForestryClass, "inventorySlots");
            if (inventorySlotsField != null) {
                forestry.storeField("inventorySlots", inventorySlotsField);

                Method getSlotAtPositionMethod = getMethod(guiForestryClass, "getSlotAtPosition", int.class, int.class);
                if (getSlotAtPositionMethod != null) {
                    forestry.storeMethod("getSlotAtPosition", getSlotAtPositionMethod);

                    Method handleMouseClickMethod = getMethod(
                            guiForestryClass, "handleMouseClick", Slot.class, int.class, int.class, int.class);
                    if (handleMouseClickMethod != null) {
                        forestry.storeMethod("handleMouseClick", handleMouseClickMethod);

                        forestry.compatible = true;

                        // Class containerForestryClass = getClass( "forestry.core.gui.ContainerForestry" );
                        // if ( containerForestryClass != null ) {
                        // forestry.storeClass( "ContainerForestry", containerForestryClass );
                        //
                        // Field slotCountField = getField( containerForestryClass, "slotCount" );
                        // if ( slotCountField != null ) {
                        // forestry.storeField( "slotCount", slotCountField );
                        //
                        // forestry.compatible = true;
                        // }
                        // }
                    }
                }
            }
        }

        return forestry.compatible;
    }

    public static boolean reflectCodeChickenCore() {
        codechickencore = new ReflectionCache();

        Class<?> guiContainerWidgetClass = getClass("codechicken.core.inventory.GuiContainerWidget");
        if (guiContainerWidgetClass != null) {
            codechickencore.storeClass("GuiContainerWidget", guiContainerWidgetClass);

            codechickencore.available = true;
            codechickencore.compatible = true;
        }

        return codechickencore.compatible;
    }

    public static boolean reflectNEI() {
        NEI = new ReflectionCache();

        Class<?> guiRecipeClass = getClass("codechicken.nei.recipe.GuiRecipe");
        if (guiRecipeClass != null) {
            NEI.storeClass("GuiRecipe", guiRecipeClass);
            NEI.available = true;

            Class<?> guiEnchantmentModifierClass = getClass("codechicken.nei.GuiEnchantmentModifier");
            if (guiEnchantmentModifierClass != null) {
                NEI.storeClass("GuiEnchantmentModifier", guiEnchantmentModifierClass);

                NEI.compatible = true;
            }
        }

        return NEI.compatible;
    }

    public static boolean is(Object object, String name) {
        return object.getClass().getSimpleName() == name;
    }

    public static boolean doesClassExist(String name) {
        Class<?> clazz = getClass(name);
        return clazz != null;
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            ;
        }

        return null;
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = null;

            try {
                field = clazz.getField(name);
            } catch (Exception e) {
                field = null;
            }

            if (field == null) {
                field = clazz.getDeclaredField(name);
            }

            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            if (name != "ofProfiler") {
                Constants.LOGGER.error(
                        "Could not retrieve field \"" + name + "\" from class \"" + clazz.getName() + "\"", e);
            }
        }

        return null;
    }

    public static Field getFinalField(Class<?> clazz, String name) {
        try {
            Field field = null;

            try {
                field = clazz.getField(name);
            } catch (Exception e) {
                field = null;
            }

            if (field == null) {
                field = clazz.getDeclaredField(name);
            }

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);

            modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            Constants.LOGGER.error(
                    "Could not retrieve field \"" + name + "\" from class \"" + clazz.getName() + "\"", e);
        }

        return null;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = null;

            try {
                method = clazz.getMethod(name, args);
            } catch (Exception e) {
                method = null;
            }

            if (method == null) {
                if ((args != null) & (args.length != 0)) {
                    method = clazz.getDeclaredMethod(name, args);
                } else {
                    method = clazz.getDeclaredMethod(name, new Class[0]);
                }
            }

            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            Constants.LOGGER.error(
                    "Could not retrieve method \"" + name + "\" from class \"" + clazz.getName() + "\"", e);
        }

        return null;
    }

    public static String methodToString(Method method) {
        return Modifier.toString(method.getModifiers()) + " "
                + ((method.getReturnType() != null) ? method.getReturnType().getName() : "void") + " "
                + method.getName();
    }
}
