package input;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import logger.GlobalLogger;

public final class Keyboard {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static boolean[] keyPressedStates = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private Keyboard() {}

    public static void keyCallback(long glfwWindow, int key, int scancode, int action, int mods) {
        LOGGER.fine(() -> String.format(
                "Method called with: (glfwWindow=%1$s) (key=%2$s) (scancode=%3$s) (action=%4$s) (mods=%5$s)",
                glfwWindow, key, scancode, action, mods));

        if (action == GLFW.GLFW_PRESS) {
            LOGGER.fine(() -> String.format("(key=%1$s) is pressed", key));
            keyPressedStates[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            LOGGER.fine(() -> String.format("(key=%1$s) is released", key));
            keyPressedStates[key] = false;
        }

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    public static boolean isKeyPressed(int key) {
        LOGGER.fine(() -> String.format("Method called with: (key=%1$s)", key));

        LOGGER.fine(() -> String.format("Method returned: %1$s", keyPressedStates[key]));
        return keyPressedStates[key];
    }
}
