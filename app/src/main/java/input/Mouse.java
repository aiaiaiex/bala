package input;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import logger.GlobalLogger;

public final class Mouse {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static double xPosition, yPosition;
    private static boolean[] buttonPressedStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
    private static int buttonsPressed = 0;
    private static double yScrollOffset;

    private Mouse() {}

    public static void cursorPositionCallback(long glfwWindow, double xPos, double yPos) {
        LOGGER.fine(
                () -> String.format("Method called with: (glfwWindow=%1$s) (xPos=%2$s) (yPos=%3$s)",
                        glfwWindow, xPos, yPos));

        xPosition = xPos;
        yPosition = yPos;

        LOGGER.fine(() -> String.format("New xPosition: %1$s", xPosition));
        LOGGER.fine(() -> String.format("New yPosition: %1$s", yPosition));

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public static void mouseButtonCallback(long glfwWindow, int button, int action, int mods) {
        LOGGER.fine(() -> String.format(
                "Method called with: (glfwWindow=%1$s) (button=%2$s) (action=%3$s) (mods=%4$s)",
                glfwWindow, button, action, mods));

        if (action == GLFW.GLFW_PRESS) {
            LOGGER.fine(() -> String.format("(button=%1$s) is pressed", button));
            buttonPressedStates[button] = true;

            buttonsPressed += 1;
            LOGGER.fine(() -> String.format("New buttonsPressed: %1$s", buttonsPressed));

        } else if (action == GLFW.GLFW_RELEASE) {
            LOGGER.fine(() -> String.format("(button=%1$s) is released", button));
            buttonPressedStates[button] = false;

            buttonsPressed -= 1;
            LOGGER.fine(() -> String.format("New buttonsPressed: %1$s", buttonsPressed));
        }

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public static void scrollCallback(long glfwWindow, double xOffset, double yOffset) {
        LOGGER.fine(() -> String.format(
                "Method called with: (glfwWindow=%1$s) (xOffset=%2$s) (yOffset=%3$s)", glfwWindow,
                xOffset, yOffset));

        yScrollOffset = yOffset;
        LOGGER.fine(() -> String.format("New yScrollOffset: %1$s", yScrollOffset));

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public static void resetYScrollOffset() {
        LOGGER.fine("Reset yScrollOffset to 0");
        yScrollOffset = 0.0d;
    }

    public static double getXPosition() {
        LOGGER.fine("Return xPosition");
        return xPosition;
    }

    public static double getYPosition() {
        LOGGER.fine("Return yPosition");
        return yPosition;
    }

    public static boolean isButtonPressed(int button) {
        LOGGER.fine("Return pressed state of button");
        return buttonPressedStates[button];
    }
}
