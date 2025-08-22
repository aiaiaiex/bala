package utilities;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import settings.EngineSettings;

public final class GlobalLogger {
    private static final Logger logger = Logger.getGlobal();
    private static GlobalLogger globalLogger = null;

    private GlobalLogger() {
        logger.setLevel(EngineSettings.LOG_LEVEL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(EngineSettings.LOG_LEVEL);

        Formatter formatter = new SimpleFormatter() {
            private String format = "%1$tT.%1$tL | %2$-7S | %3$s [%4$s.%5$s] %n";

            @Override
            public synchronized String format(LogRecord logRecord) {
                return String.format(format, new Date(logRecord.getMillis()), logRecord.getLevel(),
                        logRecord.getMessage(), logRecord.getSourceClassName(),
                        logRecord.getSourceMethodName());
            }
        };
        consoleHandler.setFormatter(formatter);

        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }

    public static Logger getLogger() {
        if (globalLogger == null) {
            globalLogger = new GlobalLogger();
        }
        return logger;
    }
}
