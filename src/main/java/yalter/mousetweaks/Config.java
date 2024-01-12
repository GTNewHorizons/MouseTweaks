package yalter.mousetweaks;

import yalter.mousetweaks.config.MTConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Config {

    private File file;

    public Config(File fileName) {
        this.file = fileName;
    }

    public boolean hasOldConfig() {
        try {
            FileReader reader = new FileReader(this.file);
            Properties tempProps = new Properties();
            tempProps.load(reader);
            return tempProps.size() > 0 && !tempProps.containsKey("general");
        } catch (IOException e) {
            return false;
        }
    }

    public void importOldConfig(){
        try {
            FileReader reader = new FileReader(this.file);
            Properties tempProps = new Properties();
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


            file.renameTo(new File(file.getPath() + ".bak"));
        } catch (IOException ignored) {}
    }
}
