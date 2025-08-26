package input;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import logger.GlobalLogger;

public final class Keyboard {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static boolean[] keyPressedStates = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private Keyboard() {}

    public static void keyCallback(long glfwWindow, int key, int scancode, int action, int mods) {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        if (action == GLFW.GLFW_PRESS) {
            LOGGER.fine(() -> String.format("(key=%1$s) is pressed", key));
            keyPressedStates[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            LOGGER.fine(() -> String.format("(key=%1$s) is released", key));
            keyPressedStates[key] = false;
        }

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }
}
