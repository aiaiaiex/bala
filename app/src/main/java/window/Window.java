package window;

import java.util.logging.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import event.Event;
import event.Observer;
import event.Subject;
import settings.EngineSettings;
import settings.GameSettings;
import sound.SoundDevice;
import utilities.GlobalLogger;

public final class Window implements Observer {
    private static final Logger LOGGER = GlobalLogger.getLogger();

    private static Window window = null;

    private boolean initialized = false;
    private int glClearMask = GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT;

    private String title;
    private int monitorWidth, monitorHeight;
    private int windowWidth, windowHeight;
    private long glfwWindow;

    private Window() {
        LOGGER.fine(GlobalLogger.CLASS_INITIALIZATION);

        LOGGER.fine("Check if engine editor will be displayed");
        if (EngineSettings.DISPLAY_EDITOR) {
            LOGGER.fine("Engine editor will be displayed");
            title = EngineSettings.ENGINE_TITLE;
        } else {
            LOGGER.fine("Engine editor will NOT be displayed");
            title = GameSettings.GAME_TITLE;
        }
        LOGGER.fine(() -> String.format("New title: %1$s", title));

        Subject.addObserver(this);
    }

    public static Window getWindow() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        if (window == null) {
            window = new Window();
            LOGGER.fine(() -> String.format("New window: %1$s", window));
        }

        LOGGER.fine(() -> String.format("Method returned: %1$s", window));
        return window;
    }

    public void run() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        initialize();
        loop();
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

        LOGGER.fine("Get the primary monitor");
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        LOGGER.fine("Get the current video mode of the primaryMonitor");
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(primaryMonitor);

        monitorWidth = videoMode.width();
        monitorHeight = videoMode.height();
        LOGGER.fine(() -> String.format("New monitorWidth: %1$s", monitorWidth));
        LOGGER.fine(() -> String.format("New monitorHeight: %1$s", monitorHeight));

        LOGGER.fine(
                "Set the initial windowWidth and windowHeight based on HALF of the primary monitor's resolution");
        windowWidth = monitorWidth / 2;
        windowHeight = monitorHeight / 2;
        LOGGER.fine(() -> String.format("New windowWidth: %1$s", windowWidth));
        LOGGER.fine(() -> String.format("New windowHeight: %1$s", windowHeight));

        LOGGER.fine("Reset all window hints");
        GLFW.glfwDefaultWindowHints();
        LOGGER.fine("Make window invisible");
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        LOGGER.fine("Make window maximized");
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        LOGGER.fine("Make window resizable by the user");
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        LOGGER.fine("Creating glfwWindow");
        glfwWindow = GLFW.glfwCreateWindow(windowWidth, windowHeight, title, MemoryUtil.NULL,
                MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            LOGGER.severe("Failed to create glfwWindow");
            throw new RuntimeException("Failed to create glfwWindow!");
        }

        if (!EngineSettings.DISPLAY_EDITOR) {
            LOGGER.fine(
                    "Make glfwWindow follow the primary monitor's aspect ratio when resized when the engine editor is NOT displayed");
            GLFW.glfwSetWindowAspectRatio(glfwWindow, monitorWidth, monitorHeight);
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

        LOGGER.fine("Set the clear color to transparent white");
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        SoundDevice.getSoundDevice().initialize();

        LOGGER.fine("Make glfwWindow visible");
        GLFW.glfwShowWindow(glfwWindow);

        LOGGER.fine("Set initialized to true");
        initialized = true;

        LOGGER.fine(GlobalLogger.METHOD_RETURN);
    }

    private void loop() {
        LOGGER.fine(GlobalLogger.METHOD_CALL);

        LOGGER.fine("Initialize startTime, endTime, and deltaTime to 0");
        double startTime = 0.0d;
        double endTime = 0.0d;
        double deltaTime = endTime - startTime;

        LOGGER.fine("Set GLFW timer to startTime's initial value");
        GLFW.glfwSetTime(startTime);

        while (!GLFW.glfwWindowShouldClose(glfwWindow)) {
            LOGGER.fine("glfwWindow is NOT yet closed");

            LOGGER.fine("Clear the color buffer and depth buffer");
            GL11.glClear(glClearMask);

            LOGGER.fine("Swap the front and back buffer");
            GLFW.glfwSwapBuffers(glfwWindow);
            LOGGER.fine("Process events in the queue");
            GLFW.glfwPollEvents();

            LOGGER.fine("Set endTime to current value of GLFW timer");
            endTime = GLFW.glfwGetTime();
            LOGGER.fine("Calculate deltaTime");
            deltaTime = endTime - startTime;
            LOGGER.fine("Set startTime to current value of endTime");
            startTime = endTime;
        }

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
