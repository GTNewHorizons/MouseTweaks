package yalter.mousetweaks;

public class Logger {

    @Deprecated
    public static void Log(String textToLog) {
        Constants.LOGGER.info(textToLog);
    }
}
