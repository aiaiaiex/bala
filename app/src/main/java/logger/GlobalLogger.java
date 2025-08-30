package logger;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import setting.EngineSettings;
import setting.LoggerSettings;

public final class GlobalLogger {
    private static GlobalLogger globalLogger = null;

    private Logger logger;

    private GlobalLogger() {
        logger = Logger.getGlobal();

        Level logLevel =
                EngineSettings.DISPLAY_EDITOR ? LoggerSettings.GLOBAL_LOGGER_LEVEL : Level.OFF;

        logger.setLevel(logLevel);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);

        Formatter formatter = new SimpleFormatter() {
            private String format = "%1$tF %1$tT.%1$tL | %2$-7S | %3$s [%4$s.%5$s] %n";

            @Override
            public synchronized String format(LogRecord logRecord) {
                return String.format(format, new Date(logRecord.getMillis()), logRecord.getLevel(),
                        logRecord.getMessage(), logRecord.getSourceClassName(),
                        logRecord.getSourceMethodName());
            }
        };
        consoleHandler.setFormatter(formatter);

        logger.setUseParentHandlers(false);

        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }

        logger.addHandler(consoleHandler);
    }

    public static GlobalLogger getGlobalLogger() {
        if (globalLogger == null) {
            globalLogger = new GlobalLogger();
        }
        return globalLogger;
    }

    public Logger getLogger() {
        return logger;
    }
}
