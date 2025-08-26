package input;

import java.util.logging.Logger;
import logger.GlobalLogger;

public final class Mouse {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static double xPosition, yPosition;

    private Mouse() {}

    public static void cursorPositionCallback(long glfwWindow, double xPos, double yPos) {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        xPosition = xPos;
        yPosition = yPos;

        LOGGER.fine(() -> String.format("New xPos: %1$s", xPos));
        LOGGER.fine(() -> String.format("New yPos: %1$s", yPos));

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }
}
