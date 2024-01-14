package yalter.mousetweaks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

import yalter.mousetweaks.config.MTConfig;

public class Config {

    private File file;

    public Config(File fileName) {
        this.file = fileName;
    }

    public boolean hasOldConfig() {
        Constants.LOGGER.info("checking if old config");
        if (!this.file.exists()) return false;

        try {
            boolean val = Files.readAllLines(this.file.toPath()).stream().noneMatch(str -> str.contains("general"));
            Constants.LOGGER.info("output of hasOldConfig: " + (val ? "true" : "false"));
            return val;
        } catch (IOException e) {
            Constants.LOGGER.info("Error reading old config");
            Constants.LOGGER.error(e);
            return true; // assume yes if it failed for whatever reason
        }
    }

    public void importOldConfig() {
        Constants.LOGGER.info("importOldConfig start");
        Properties tempProps = new Properties();
        try {
            FileReader reader = new FileReader(this.file);
            tempProps.load(reader);

            if (tempProps.containsKey("RMBTweak"))
                MTConfig.RMBTweak = Objects.equals(tempProps.get("RMBTweak").toString(), "1");

            if (tempProps.containsKey("LMBTweakWithItem"))
                MTConfig.LMBTweakWithItem = Objects.equals(tempProps.get("LMBTweakWithItem").toString(), "1");

            if (tempProps.containsKey("LMBTweakWithoutItem"))
                MTConfig.LMBTweakWithoutItem = Objects.equals(tempProps.get("LMBTweakWithoutItem").toString(), "1");

            if (tempProps.containsKey("WheelTweak"))
                MTConfig.WheelTweak = Objects.equals(tempProps.get("WheelTweak").toString(), "1");

            if (tempProps.containsKey("WheelSearchOrder"))
                MTConfig.WheelSearchOrder = Integer.parseInt(tempProps.get("WheelSearchOrder").toString());

            if (tempProps.containsKey("WheelScrollDirection"))
                MTConfig.WheelScrollDirection = Integer.parseInt(tempProps.get("WheelScrollDirection").toString());

            if (tempProps.containsKey("ScrollItemScaling"))
                MTConfig.ScrollItemScaling = Integer.parseInt(tempProps.get("ScrollItemScaling").toString());

            Constants.LOGGER.error(tempProps.toString());
            file.renameTo(new File(file.getPath() + ".bak"));
        } catch (IOException e) {
            Constants.LOGGER.info("importOldConfig error");
            Constants.LOGGER.error(tempProps.toString());
            Constants.LOGGER.error(e);
        }
    }
}
