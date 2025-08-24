package window;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import event.Event;
import event.Observer;
import event.Subject;
import sound.SoundDevice;
import utilities.GlobalLogger;

public final class Window implements Observer {
    private static final Logger LOGGER = GlobalLogger.getLogger();
    private static final String TITLE = "Bala";

    private static Window window = null;

    private int width, height;
    private boolean initialized = false;
    private long glfwWindow;

    private Window() {
        LOGGER.fine(GlobalLogger.CLASS_INITIALIZATION);

        LOGGER.fine(() -> String.format("Old width: %1$s", width));
        LOGGER.fine(() -> String.format("Old height: %1$s", height));
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDisplayMode();
        width = displayMode.getWidth();
        height = displayMode.getHeight();
        LOGGER.fine(() -> String.format("New width: %1$s", width));
        LOGGER.fine(() -> String.format("New height: %1$s", height));

        Subject.addObserver(this);
    }

    public static Window getWindow() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        if (window == null) {
            LOGGER.fine(() -> String.format("Old window: %1$s", window));
            window = new Window();
            LOGGER.fine(() -> String.format("New window: %1$s", window));
        }

        LOGGER.fine(() -> String.format("Method returned: %1$s", window));
        return window;
    }

    public void run() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        initialize();

        terminate();

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    private void initialize() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        LOGGER.fine("Check if window is already initialized");
        if (initialized) {
            LOGGER.fine("window is already initialized");
            LOGGER.fine(GlobalLogger.METHOD_RETURN);
            return;
        }

        LOGGER.fine("Set error callback for GLFW");
        GLFWErrorCallback.createPrint(System.err).set();

        LOGGER.fine("Initializing GLFW");
        if (!GLFW.glfwInit()) {
            LOGGER.severe("Failed to initialize GLFW");
            throw new RuntimeException("Failed to initialize GLFW!");
        } else {
            LOGGER.info("GLFW initialized");
        }

        LOGGER.fine("Configuring glfwWindow");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        LOGGER.fine("Creating glfwWindow");
        glfwWindow = GLFW.glfwCreateWindow(width, height, TITLE, MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            LOGGER.severe("Failed to create glfwWindow");
            throw new RuntimeException("Failed to create glfwWindow!");
        }

        // TODO Set callbacks for keyboard and mouse.
        // TODO Set callbacks for resizing window.

        LOGGER.fine("Make glfwWindow's OpenGL context current");
        GLFW.glfwMakeContextCurrent(glfwWindow);

        LOGGER.fine("Create a GLCapabilities for glfwWindow's current OpenGL context");
        GL.createCapabilities();

        LOGGER.fine("Sync your buffer swaps with your monitor's refresh rate");
        GLFW.glfwSwapInterval(1);

        LOGGER.fine("Enable glBlendFunc");
        GL11.glEnable(GL11.GL_BLEND);
        LOGGER.fine("Enable transparency");
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        SoundDevice.getSoundDevice().initialize();

        LOGGER.fine("Make glfwWindow visible");
        GLFW.glfwShowWindow(glfwWindow);

        LOGGER.fine("Set initialized to true");
        initialized = true;

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    private void terminate() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        LOGGER.fine("Check if window is even initialized");
        if (!initialized) {
            LOGGER.warning("window is NOT even initialized");
            LOGGER.fine(GlobalLogger.METHOD_RETURN);
            return;
        }

        SoundDevice.getSoundDevice().terminate();

        LOGGER.fine("Unset all callbacks for glfwWindow");
        Callbacks.glfwFreeCallbacks(glfwWindow);
        LOGGER.fine("Destroy glfwWindow and its context");
        GLFW.glfwDestroyWindow(glfwWindow);

        LOGGER.fine("Terminate GLFW");
        GLFW.glfwTerminate();
        LOGGER.fine("Unset error callback for GLFW");
        GLFW.glfwSetErrorCallback(null).free();

        LOGGER.fine("Set initialized to false");
        initialized = false;

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    @Override
    public void notify(Event event) {
        LOGGER.fine(() -> String.format("Method called with: (event=%1$s)", event));

        switch (event) {
            case START_GAME:
                // TODO Create reaction to START_GAME.
                break;
            case STOP_GAME:
                // TODO Create reaction to STOP_GAME.
                break;
            case SAVE_LEVEL:
                // TODO Create reaction to SAVE_LEVEL.
                break;
            case LOAD_LEVEL:
                // TODO Create reaction to LOAD_LEVEL.
                break;
        }

        LOGGER.fine(GlobalLogger.METHOD_RETURN);

    }
}
