package yalter.mousetweaks.loaders;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import yalter.mousetweaks.Config;
import yalter.mousetweaks.Constants;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.config.MTConfig;

@Mod(
        modid = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.MOD_VERSION,
        guiFactory = "yalter.mousetweaks.config.MTGuiConfigFactory",
        dependencies = "required-after:gtnhlib@[0.2.0,);",
        acceptedMinecraftVersions = "[1.7.10]",
        acceptableRemoteVersions = "*")
public class MouseTweaksForge {

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            try {
                Config.handleOldConfig(event.getSuggestedConfigurationFile());
                ConfigurationManager.registerConfig(MTConfig.class);
                ConfigurationManager.registerBus();
            } catch (ConfigException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            Main.initialise();
            FMLCommonHandler.instance().bus().register(this);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) Main.onUpdateInGame();
    }
}
