package yalter.mousetweaks;

import net.minecraft.client.gui.GuiScreen;

public final class Main {

    public static boolean DisableRMBTweak = false;

    private Main() {}

    public static void initialise() {
        Runtime.initialise();
    }

    public static void onUpdateInGame() {
        Runtime.onUpdateInGame();
    }

    public static void onUpdateInGui(GuiScreen currentScreen) {
        Runtime.onUpdateInGui(currentScreen);
    }
}
