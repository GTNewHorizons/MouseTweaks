package yalter.mousetweaks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config {

    private File file;

    public Config(File fileName) {
        this.file = fileName;
    }

    public void handleOldConfig() {
        if (!this.file.exists()) return;

        try {
            if (Files.readAllLines(this.file.toPath()).stream().noneMatch(str -> str.contains("general"))) {
                this.file.delete();
            }
        } catch (IOException e) {
            Constants.LOGGER.error(e);
        }
    }

}
