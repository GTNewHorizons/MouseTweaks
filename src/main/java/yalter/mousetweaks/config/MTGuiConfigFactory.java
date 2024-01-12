package yalter.mousetweaks.config;

import com.gtnewhorizon.gtnhlib.config.SimpleGuiFactory;
import net.minecraft.client.gui.GuiScreen;

public class MTGuiConfigFactory implements SimpleGuiFactory {
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return MTGuiConfig.class;
    }
}
