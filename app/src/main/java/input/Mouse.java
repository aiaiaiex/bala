package input;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import logger.GlobalLogger;

public final class Mouse {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static double xPosition, yPosition;
    private static boolean[] buttonPressedStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];

    private Mouse() {}

    public static void cursorPositionCallback(long glfwWindow, double xPos, double yPos) {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        xPosition = xPos;
        yPosition = yPos;

        LOGGER.fine(() -> String.format("New xPos: %1$s", xPos));
        LOGGER.fine(() -> String.format("New yPos: %1$s", yPos));

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public static void mouseButtonCallback(long glfwWindow, int button, int action, int mods) {
        LOGGER.fine(() -> String.format(
                "Method called with: (glfwWindow=%1$s) (button=%2$s) (action=%3$s) (mods=%4$s)",
                glfwWindow, button, action, mods));

        if (action == GLFW.GLFW_PRESS) {
            LOGGER.fine(() -> String.format("(button=%1$s) is pressed", button));
            buttonPressedStates[button] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            LOGGER.fine(() -> String.format("(button=%1$s) is released", button));
            buttonPressedStates[button] = false;
        }

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }
}
