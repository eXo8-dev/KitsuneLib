package me.eXo8_.kitsunelib.logger;

public final class Logger
{
    private static final String PREFIX = "[KitsuneLib] ";

    private static java.util.logging.Logger logger;

    public static void init(java.util.logging.Logger logger) {
        Logger.logger = logger;
    }

    public static void info(String str) {
        logger.info(PREFIX + str);
    }

    public static void warning(String str) {
        logger.warning(PREFIX + str);
    }

    public static void severe(String str) {
        logger.severe(PREFIX + str);
    }
}
