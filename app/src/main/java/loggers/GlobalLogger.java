package loggers;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import settings.EngineSettings;

public final class GlobalLogger {
    private static final Logger LOGGER = Logger.getGlobal();

    public static final String CLASS_INITIALIZATION = "Class initialized"; // without arguments.
    public static final String METHOD_CALL = "Method called"; // without arguments.
    public static final String METHOD_RETURN = "Method returned: void";

    private static GlobalLogger globalLogger = null;

    private GlobalLogger() {
        Level logLevel = EngineSettings.DISPLAY_EDITOR ? EngineSettings.LOG_LEVEL : Level.OFF;

        LOGGER.setLevel(logLevel);

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

        LOGGER.setUseParentHandlers(false);

        Handler[] handlers = LOGGER.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            LOGGER.removeHandler(handler);
        }

        LOGGER.addHandler(consoleHandler);
    }

    public static Logger getLogger() {
        if (globalLogger == null) {
            globalLogger = new GlobalLogger();
        }
        return LOGGER;
    }
}
