package yalter.mousetweaks.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;

import yalter.mousetweaks.Constants;

public class MTGuiConfig extends SimpleGuiConfig {

    public MTGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, MTConfig.class, Constants.MOD_ID, Constants.MOD_NAME);
    }
}
