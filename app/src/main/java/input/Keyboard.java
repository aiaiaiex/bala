package input;

import org.lwjgl.glfw.GLFW;

public final class Keyboard {
    private static Keyboard keyboard = null;

    private boolean[] keyPressedStates = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private boolean[] oldKeyPressedStates;

    private Keyboard() {
        keyPressedStates = new boolean[GLFW.GLFW_KEY_LAST + 1];
    }

    public static Keyboard getKeyboard() {
        if (keyboard == null) {
            keyboard = new Keyboard();
        }

        return keyboard;
    }

    public void setup() {
        oldKeyPressedStates = keyPressedStates.clone();
    }

    public void keyCallback(long glfwWindow, int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            keyPressedStates[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            keyPressedStates[key] = false;
        }
    }

    public boolean isKeyPressed(int key) {
        return keyPressedStates[key];
    }

    public boolean isKeyInitiallyPressed(int keyCode) {
        return keyPressedStates[keyCode] && !oldKeyPressedStates[keyCode];
    }
}
