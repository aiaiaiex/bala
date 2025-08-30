package camera;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import component.Component;
import input.Keyboard;
import input.Mouse;

public class EditorCamera extends Component {

    private float dragDebounce = 0.032f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    private Keyboard keyboard;
    private Mouse mouse;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        clickOrigin = new Vector2f();

        keyboard = Keyboard.getKeyboard();
        mouse = Mouse.getMouse();
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f world = mouse.getWorld();
        if (mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            clickOrigin = new Vector2f(world);
            dragDebounce -= dt;
            return;
        } else if (mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(world);
            Vector2f delta = new Vector2f(mousePos).sub(clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        if (mouse.getYScrollOffset() != 0.0f) {
            float addValue =
                    (float) Math.pow(Math.abs(mouse.getYScrollOffset() * scrollSensitivity),
                            1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(mouse.getYScrollOffset());
            levelEditorCamera.addZoom(addValue);
        }

        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_0)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(levelEditorCamera.getZoom()
                    + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f
                    && Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
