package window;

import java.util.logging.Logger;
import org.joml.Vector4f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import event.Event;
import event.Observer;
import event.Subject;
import graphics.DebugDraw;
import graphics.Framebuffer;
import graphics.PickingTexture;
import graphics.Renderer;
import graphics.Shader;
import gui.ImGuiLayer;
import input.Keyboard;
import input.Mouse;
import logger.AverageFrameTimeLogger;
import logger.ExactFrameTimeLogger;
import logger.GlobalLogger;
import object.ObjectPool;
import physics.Physics;
import scene.Scene;
import scene.SceneInitializer;
import scene.scenes.GameObjectPickerScene;
import scene.scenes.GameScene;
import setting.EngineSettings;
import setting.GameSettings;
import sound.SoundDevice;

public final class Window implements Observer {
    private static Window window = null;

    private String title;
    private int monitorWidth, monitorHeight;
    private int refreshRate;
    private int windowWidth, windowHeight;
    private long glfwWindow;
    private int xPosition, yPosition;

    private Keyboard keyboard;
    private Mouse mouse;

    private int glClearColorAndDepthMask = GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT;

    private boolean initialized;

    private Logger globalLogger;
    private Logger averageFrameTimeLogger;
    private Logger exactFrameTimeLogger;

    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying;
    private Scene currentScene;

    private Window() {
        if (EngineSettings.DISPLAY_EDITOR) {
            title = EngineSettings.ENGINE_TITLE;
        } else {
            title = GameSettings.GAME_TITLE;
        }

        glClearColorAndDepthMask = GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT;

        initialized = false;
        runtimePlaying = false;

        globalLogger = GlobalLogger.getGlobalLogger().getLogger();
        averageFrameTimeLogger = AverageFrameTimeLogger.getAverageFrameTimeLogger().getLogger();
        exactFrameTimeLogger = ExactFrameTimeLogger.getExactFrameTimeLogger().getLogger();

        Subject.getSubject().addObserver(this);
    }

    public static Window getWindow() {
        if (window == null) {
            window = new Window();
        }

        return window;
    }

    public void run() {
        // TODO Remove after moving.
        // AverageFrameTimeLogger.getAverageFrameTimeLogger().start();
        // ExactFrameTimeLogger.getExactFrameTimeLogger().start();

        initialize();
        loop();
        terminate();

        // TODO Remove after moving.
        // AverageFrameTimeLogger.getAverageFrameTimeLogger().stop();
        // ExactFrameTimeLogger.getExactFrameTimeLogger().stop();
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            globalLogger.severe("Failed to initialize GLFW");
            throw new RuntimeException("Failed to initialize GLFW!");
        } else {
            globalLogger.info("GLFW initialized");
        }

        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(primaryMonitor);

        monitorWidth = videoMode.width();
        monitorHeight = videoMode.height();

        refreshRate = videoMode.refreshRate();

        windowWidth = monitorWidth / 1;
        windowHeight = monitorHeight / 1;

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        glfwWindow = GLFW.glfwCreateWindow(windowWidth, windowHeight, title, MemoryUtil.NULL,
                MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            globalLogger.severe("Failed to create glfwWindow");
            throw new RuntimeException("Failed to create glfwWindow!");
        }

        if (!EngineSettings.DISPLAY_EDITOR) {
            GLFW.glfwSetWindowAspectRatio(glfwWindow, monitorWidth, monitorHeight);
        }

        keyboard = Keyboard.getKeyboard();
        GLFW.glfwSetKeyCallback(glfwWindow, keyboard::keyCallback);

        mouse = Mouse.getMouse();
        GLFW.glfwSetCursorPosCallback(glfwWindow, mouse::cursorPositionCallback);
        GLFW.glfwSetMouseButtonCallback(glfwWindow, mouse::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(glfwWindow, mouse::scrollCallback);

        GLFW.glfwSetWindowSizeCallback(glfwWindow, window::windowSizeCallback);
        GLFW.glfwSetWindowPosCallback(glfwWindow, window::windowPosCollback);

        GLFW.glfwMakeContextCurrent(glfwWindow);

        GL.createCapabilities();

        GLFW.glfwSwapInterval(1);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        SoundDevice.getSoundDevice().initialize();

        framebuffer = new Framebuffer(monitorWidth, monitorHeight);
        pickingTexture = new PickingTexture(monitorWidth, monitorHeight);
        GL11.glViewport(0, 0, monitorWidth, monitorHeight);

        if (!EngineSettings.DISPLAY_EDITOR) {
            runtimePlaying = true;
            changeScene(new GameScene());
        } else {
            imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
            imguiLayer.initImGui();
            changeScene(new GameObjectPickerScene());
        }

        GLFW.glfwShowWindow(glfwWindow);

        initialized = true;
    }

    public void loop() {
        if (!initialized) {
            globalLogger.warning("window is NOT even initialized");
            return;
        }

        Shader defaultShader = ObjectPool.getShader(EngineSettings.DEFAULT_SHADER);
        Shader pickingShader = ObjectPool.getShader(EngineSettings.PICKER_SHADER);

        double startTime = 0.0d;
        double endTime = 0.0d;
        double deltaTime = endTime - startTime;

        int frames = 0;
        double elapsedTime = 0.0d;

        GLFW.glfwSetTime(startTime);

        while (!GLFW.glfwWindowShouldClose(glfwWindow)) {
            keyboard.setup();
            mouse.setup();

            GLFW.glfwPollEvents();

            GL11.glDisable(GL11.GL_BLEND);

            pickingTexture.enableWriting();

            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(glClearColorAndDepthMask);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();

            GL11.glEnable(GL11.GL_BLEND);

            DebugDraw.beginFrame();

            framebuffer.bind();
            Vector4f clearColor = currentScene.getCamera().clearColor;
            GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            if (deltaTime > 0) {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update((float) deltaTime);
                } else {
                    currentScene.editorUpdate((float) deltaTime);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            framebuffer.unbind();

            if (!EngineSettings.DISPLAY_EDITOR) {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.getFboID());
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
                GL30.glBlitFramebuffer(0, 0, framebuffer.width, framebuffer.height, 0, 0,
                        windowWidth, windowHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
            } else {
                imguiLayer.update((float) deltaTime, currentScene);
            }

            mouse.cleanup();

            GLFW.glfwSwapBuffers(glfwWindow);

            endTime = GLFW.glfwGetTime();
            deltaTime = endTime - startTime;

            exactFrameTimeLogger.info(endTime + "," + deltaTime);

            startTime = endTime;

            frames += 1;
            elapsedTime += deltaTime;
            if (elapsedTime >= 1.0d) {
                averageFrameTimeLogger
                        .info(elapsedTime + "," + frames + "," + (elapsedTime / (double) frames));

                frames = 0;
                elapsedTime = 0.0d;
            }
        }
    }

    private void terminate() {
        if (!initialized) {
            globalLogger.warning("window is NOT even initialized");
            return;
        }

        SoundDevice.getSoundDevice().terminate();;

        Callbacks.glfwFreeCallbacks(glfwWindow);
        GLFW.glfwDestroyWindow(glfwWindow);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();

        initialized = false;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public int getMonitorWidth() {
        return monitorWidth;
    }

    public int getMonitorHeight() {
        return monitorHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void windowSizeCallback(long glfwWindow, int width, int height) {
        windowWidth = width;
        windowHeight = height;

        framebuffer = new Framebuffer(windowWidth, windowHeight);
    }

    public void windowPosCollback(long glfwWindow, int xpos, int ypos) {
        xPosition = xpos;
        yPosition = ypos;
    }

    @Override
    public void notify(Event event) {
        switch (event) {
            case START_GAME:
                runtimePlaying = true;
                currentScene.saveFile();
                changeScene(new GameScene());
                break;
            case STOP_GAME:
                runtimePlaying = false;
                changeScene(new GameObjectPickerScene());
                break;
            case SAVE_LEVEL:
                currentScene.saveFile();
                break;
            case LOAD_LEVEL:
                changeScene(new GameObjectPickerScene());
                break;
        }
    }

    public static void changeScene(SceneInitializer sceneInitializer) {
        if (getWindow().currentScene != null) {
            getWindow().currentScene.terminate();
        }

        if (EngineSettings.DISPLAY_EDITOR) {
            getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        }

        getWindow().currentScene = new Scene(sceneInitializer);
        getWindow().currentScene.loadFile();
        getWindow().currentScene.initialize();
        getWindow().currentScene.start();
    }

    public static Physics getPhysics() {
        return getWindow().currentScene.getPhysics();
    }

    public static Scene getScene() {
        return getWindow().currentScene;
    }

    public static Framebuffer getFramebuffer() {
        return getWindow().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return (float) getWindow().monitorWidth / (float) getWindow().monitorHeight;
    }

    public static ImGuiLayer getImguiLayer() {
        return getWindow().imguiLayer;
    }
}
