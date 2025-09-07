package input;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import camera.Camera;
import setting.EngineSettings;
import window.Window;

public class Mouse {
    private static Mouse mouse;

    private double xPosition, yPosition;
    private boolean[] buttonPressedStates;
    private boolean[] oldButtonPressedStates;
    private int buttonsPressed;
    private double yScrollOffset;
    private boolean draggingState;

    private Vector2f gameViewportPos;
    private Vector2f gameViewportSize;

    private Window window;

    private Mouse() {
        clear();

        gameViewportPos = new Vector2f();
        gameViewportSize = new Vector2f();

        window = Window.getWindow();
    }

    public static Mouse getMouse() {
        if (Mouse.mouse == null) {
            Mouse.mouse = new Mouse();
        }

        return Mouse.mouse;
    }

    public void cursorPositionCallback(long glfwWindow, double xPos, double yPos) {
        if (EngineSettings.DISPLAY_EDITOR
                && !Window.getImguiLayer().getGameViewWindow().getWantCaptureMouse()) {
            clear();
        }


        draggingState = buttonsPressed > 0;

        xPosition = xPos;
        yPosition = yPos;
    }

    public boolean isDragging() {
        return draggingState;
    }

    public void mouseButtonCallback(long glfwWindow, int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            buttonPressedStates[button] = true;

            buttonsPressed += 1;
        } else if (action == GLFW.GLFW_RELEASE) {
            buttonPressedStates[button] = false;

            buttonsPressed -= 1;
        }
    }

    public boolean isButtonPressed(int button) {
        return buttonPressedStates[button];
    }

    public boolean isButtonInitiallyPressed(int button) {
        return buttonPressedStates[button] && !oldButtonPressedStates[button];
    }

    public void scrollCallback(long glfwWindow, double xOffset, double yOffset) {
        yScrollOffset = yOffset;
    }

    public float getYScrollOffset() {
        return (float) yScrollOffset;
    }

    public void setup() {
        oldButtonPressedStates = buttonPressedStates.clone();
    }

    public void cleanup() {
        yScrollOffset = 0.0d;

        // draggingState = false;
    }

    public void clear() {
        xPosition = 0.0d;
        yPosition = 0.0d;

        buttonPressedStates = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
        buttonsPressed = 0;

        yScrollOffset = 0.0d;

        draggingState = false;
    }

    public Vector2f getWorld() {
        return screenToWorld(getScreen());
    }

    public Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCords = new Vector2f(screenCoords.x / window.getMonitorWidth(),
                screenCoords.y / window.getMonitorHeight());
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().getCamera();
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Window.getScene().getCamera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(window.getMonitorWidth(), window.getMonitorHeight()));

        return windowSpace;
    }

    public Vector2f getScreen() {
        float currentX = (float) xPosition - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * window.getMonitorWidth();
        float currentY = (float) yPosition - gameViewportPos.y;
        currentY = (1.0f - (currentY / gameViewportSize.y)) * window.getMonitorHeight();
        return new Vector2f(currentX, currentY);
    }

    public void setGameViewportPos(Vector2f gameViewportPos) {
        this.gameViewportPos = gameViewportPos;
    }

    public void setGameViewportSize(Vector2f gameViewportSize) {
        this.gameViewportSize = gameViewportSize;
    }
}
