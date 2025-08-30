package gui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import graphics.PickingTexture;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import input.Keyboard;
import input.Mouse;
import scene.Scene;
import window.Window;

public class ImGuiLayer {

    private long glfwWindow;

    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;

    private Window window;
    private Keyboard keyboard;
    private Mouse mouse;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);

        window = Window.getWindow();
        keyboard = Keyboard.getKeyboard();
        mouse = Mouse.getMouse();
    }

    public GameViewWindow getGameViewWindow() {
        return this.gameViewWindow;
    }

    public void initImGui() {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");


        GLFW.glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW.GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL)
                    || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT)
                    || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT)
                    || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER)
                    || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard()) {
                keyboard.keyCallback(w, key, scancode, action, mods);
            }
        });

        GLFW.glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW.GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        GLFW.glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW.GLFW_MOUSE_BUTTON_1 && action != GLFW.GLFW_RELEASE;
            mouseDown[1] = button == GLFW.GLFW_MOUSE_BUTTON_2 && action != GLFW.GLFW_RELEASE;
            mouseDown[2] = button == GLFW.GLFW_MOUSE_BUTTON_3 && action != GLFW.GLFW_RELEASE;
            mouseDown[3] = button == GLFW.GLFW_MOUSE_BUTTON_4 && action != GLFW.GLFW_RELEASE;
            mouseDown[4] = button == GLFW.GLFW_MOUSE_BUTTON_5 && action != GLFW.GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }


            if (gameViewWindow.getWantCaptureMouse()) {
                mouse.mouseButtonCallback(w, button, action, mods);
            }
        });

        GLFW.glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
                mouse.scrollCallback(w, xOffset, yOffset);
            } else {
                mouse.clear();
            }
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                GLFW.glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = GLFW.glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        imGuiGlfw.init(glfwWindow, false);
        imGuiGl3.init("#version 330 core");
    }

    public void update(float dt, Scene currentScene) {
        startFrame(dt);

        setupDockspace();
        currentScene.imGui();
        gameViewWindow.imgui();
        propertiesWindow.imgui();

        endFrame();
    }

    private void startFrame(final float deltaTime) {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        long backupWindowPtr = GLFW.glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        GLFW.glfwMakeContextCurrent(backupWindowPtr);
    }

    private void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.NoDocking;

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse
                | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Dockspace"));

        ImGui.end();
    }

    public PropertiesWindow getPropertiesWindow() {
        return this.propertiesWindow;
    }
}
