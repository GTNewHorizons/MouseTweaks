package yalter.mousetweaks.config;

import com.gtnewhorizon.gtnhlib.config.Config;

import yalter.mousetweaks.Constants;

@Config(modid = Constants.MOD_ID)
public class MTConfig {

    @Config.Comment("Very similar to the standard RMB dragging mechanic, with one difference: if you drag over a slot multiple times, an item will be put there multiple times. Replaces the standard mechanic if enabled.")
    public static boolean RMBTweak = true;

    @Config.Comment("Lets you quickly pick up or move items of the same type")
    public static boolean LMBTweakWithItem = true;

    @Config.Comment("Quickly move items into another inventory")
    public static boolean LMBTweakWithoutItem = true;

    @Config.Comment("Scroll to quickly move items between inventories")
    public static boolean WheelTweak = true;

    @Config.Comment("Wheel Inventory Slot Search Order\n" + "0 - First to Last\n" + "1 - Last To First")
    @Config.RangeInt(min = 0, max = 1)
    public static int WheelSearchOrder = 1;

    @Config.Comment("Wheel Scroll Direction\n" + "0 - Down to Push, Up to Pull\n"
            + "1 - Up to Push, Down to Pull\n"
            + "2 - Inventory Position Aware\n"
            + "3 - Inventory Position Aware, Inverted\n")
    @Config.RangeInt(min = 0, max = 3)
    public static int WheelScrollDirection = 0;

    @Config.Comment("Scroll Scaling\n" + "0 - Multiple Wheel Clicks Move Multiple Items\n"
            + "1 - Always Move One Item (macOS Compatibility)")
    @Config.RangeInt(min = 0, max = 1)
    public static int ScrollItemScaling = 0;

}
