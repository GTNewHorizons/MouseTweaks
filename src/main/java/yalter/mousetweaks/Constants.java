package yalter.mousetweaks;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static final String MOD_NAME = "GRADLETOKEN_MODNAME";
    public static final String MOD_ID = "GRADLETOKEN_MODID";
    public static final String MOD_VERSION = "GRADLETOKEN_VERSION";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final boolean DEV_ENV = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    // Inventory-related stuff
    public static final int INVENTORY_SIZE = 36; // Size of the player inventory

    // Mod GUI container IDs
    public static final int NOTASSIGNED = -1; // When we haven't determined it yet.
    public static final int NOTGUICONTAINER = 0; // This is not a container GUI.
    public static final int MINECRAFT = 1; // Containers that should be compatible with vanilla Minecraft ones.
    public static final int MTMODGUICONTAINER = 2; // Containers that implement the IMTModGuiContainer interface.
    public static final int FORESTRY = 3; // Forestry containers.
    public static final int CODECHICKENCORE = 4; // CodeChickenCore containers.
    public static final int NEI = 5; // NotEnoughItems containers (like the crafting menu).
}
