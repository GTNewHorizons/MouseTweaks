package yalter.mousetweaks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class Config {

    public static void handleOldConfig(File file) {
        if (!file.exists()) return;

        try (Stream<String> lines = Files.lines(file.toPath())) {
            if (lines.noneMatch(str -> str.contains("general")) && !file.delete()) {
                Constants.LOGGER.warn("Failed to delete outdated config file {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            Constants.LOGGER.error(e);
        }
    }

}
