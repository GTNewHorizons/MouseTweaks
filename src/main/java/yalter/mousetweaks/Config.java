package yalter.mousetweaks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config {

    public static void handleOldConfig(File file) {
        if (!file.exists()) return;

        try {
            if (Files.readAllLines(file.toPath()).stream().noneMatch(str -> str.contains("general"))) {
                file.delete();
            }
        } catch (IOException e) {
            Constants.LOGGER.error(e);
        }
    }

}
