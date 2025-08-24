package window;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;
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

public class Window implements Observer {
    private static final Logger LOGGER = GlobalLogger.getLogger();
    private static final String TITLE = "Bala";

    private static Window window = null;

    private int width, height;

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

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    private void initialize() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

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
        long glfwWindow =
                GLFW.glfwCreateWindow(width, height, TITLE, MemoryUtil.NULL, MemoryUtil.NULL);
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
