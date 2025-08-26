package input;

import org.lwjgl.glfw.GLFW;

public final class Keyboard {
    private static boolean[] keyPressedStates = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private Keyboard() {}

    public static void keyCallback(long glfwWindow, int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            keyPressedStates[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            keyPressedStates[key] = false;
        }
    }
}
