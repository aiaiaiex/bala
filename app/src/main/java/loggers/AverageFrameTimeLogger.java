package loggers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AverageFrameTimeLogger {
    private static final String FILE_NAME = "average-frame-time-per-second-log";
    private static final String FILE_EXTENSION = "csv";

    private static AverageFrameTimeLogger frameTimeLogger = null;

    private Level defaultLogLevel = Level.INFO;
    private Logger logger;
    private Formatter formatter;
    private boolean started;

    private AverageFrameTimeLogger() {
        logger = Logger.getLogger("AverageFrameTimeLogger");

        logger.setLevel(Level.OFF);
        started = false;

        logger.setUseParentHandlers(false);

        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }

        formatter = new SimpleFormatter() {
            private String format = "%1$s%n";

            @Override
            public synchronized String format(LogRecord logRecord) {
                return String.format(format, logRecord.getMessage());
            }
        };
    }

    public static AverageFrameTimeLogger getAverageFrameTimeLogger() {
        if (frameTimeLogger == null) {
            frameTimeLogger = new AverageFrameTimeLogger();
        }
        return frameTimeLogger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void start() {
        start("");
    }

    public void start(String prefix) {
        if (!started) {
            try {
                Handler fileHandler = new FileHandler(
                        String.format("../logs/%1$s%2$s-%3$tF-%3$tH-%3$tM-%3$tS.%4$s",
                                prefix.isBlank() ? "" : prefix + "-", FILE_NAME, new Date(),
                                FILE_EXTENSION));
                fileHandler.setLevel(defaultLogLevel);
                fileHandler.setFormatter(formatter);

                logger.addHandler(fileHandler);
                logger.setLevel(defaultLogLevel);
                started = true;
            } catch (IOException | SecurityException | IllegalArgumentException exception) {
                stop();
                exception.printStackTrace();
            }
        }
    }

    public void stop() {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }

        logger.setLevel(Level.OFF);
        started = false;
    }
}
