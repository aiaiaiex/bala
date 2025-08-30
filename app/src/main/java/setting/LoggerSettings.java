package setting;

import java.util.logging.Level;

public final class LoggerSettings {
    public static final Level GLOBAL_LOGGER_LEVEL = Level.INFO;

    public static final Level AVERAGE_FRAME_TIME_LOGGER_LEVEL = Level.INFO;
    public static final Level EXACT_FRAME_TIME_LOGGER_LEVEL = AVERAGE_FRAME_TIME_LOGGER_LEVEL;

    public static final String AVERAGE_FRAME_TIME_LOGGER_FILE_NAME =
            "average-frame-time-per-second-log";
    public static final String EXACT_FRAME_TIME_LOGGER_FILE_NAME = "exact-frame-time-log";

    public static final String AVERAGE_FRAME_TIME_LOGGER_FILE_EXTENSION = ".csv";
    public static final String EXACT_FRAME_TIME_LOGGER_FILE_EXTENSION =
            AVERAGE_FRAME_TIME_LOGGER_FILE_EXTENSION;

    private LoggerSettings() {}
}
