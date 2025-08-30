package input;

import java.util.HashSet;
import java.util.Set;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import component.Component;
import component.SpriteRenderer;
import component.StateMachine;
import game.Player;
import graphics.DebugDraw;
import graphics.PickingTexture;
import object.GameObject;
import scene.Scene;
import setting.EngineSettings;
import window.Window;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();
    private Vector2f screenBoxSelectStart = new Vector2f();
    private Vector2f screenBoxSelectEnd = new Vector2f();

    private Keyboard keyboard;
    private Mouse mouse;

    private float draggingPlaceReload = 1.0f / 30.0f;
    private float draggingPlaceReloadRemaining = draggingPlaceReload;

    public MouseControls() {
        keyboard = Keyboard.getKeyboard();
        mouse = Mouse.getMouse();
    }

    public void pickupObject(GameObject go) {
        if (holdingObject != null) {
            holdingObject.destroy();
        }
        holdingObject = go;
        holdingObject.getComponent(SpriteRenderer.class)
                .setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject newObj = holdingObject.copy();
        if (newObj.getComponent(StateMachine.class) != null) {
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));

        if (newObj.getComponent(Player.class) != null) {
            for (GameObject gameObject : Window.getScene().getGameObjects()) {
                if (gameObject.getComponent(Player.class) != null) {
                    gameObject.destroy();
                }
            }
        }

        if (Window.getImguiLayer().getGameViewWindow().getWantCaptureMouse()) {
            Window.getScene().addGameObjectToScene(newObj);
        }
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        draggingPlaceReloadRemaining += dt;
        PickingTexture pickingTexture =
                Window.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if (holdingObject != null) {
            Vector2f world = mouse.getWorld();
            float x = world.x;
            float y = world.y;
            holdingObject.transform.position.x =
                    ((int) Math.floor(x / EngineSettings.GRID_WIDTH) * EngineSettings.GRID_WIDTH)
                            + EngineSettings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y =
                    ((int) Math.floor(y / EngineSettings.GRID_HEIGHT) * EngineSettings.GRID_HEIGHT)
                            + EngineSettings.GRID_HEIGHT / 2.0f;



            if (mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                if (mouse.isDragging() && draggingPlaceReloadRemaining >= draggingPlaceReload) {
                    place();
                    draggingPlaceReloadRemaining = 0f;
                } else if (!mouse.isDragging() && debounce < 0
                        && mouse.isButtonInitiallyPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    place();
                    debounce = debounceTime;
                }
            }

            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }

        } else if (!mouse.isDragging() && mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)
                && debounce < 0) {
            Vector2f screen = mouse.getScreen();
            int x = (int) screen.x;
            int y = (int) screen.y;
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null) {
                Window.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            } else if (!mouse.isDragging()) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }
            debounce = 0.2f;
        } else if (mouse.isDragging() && mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
                screenBoxSelectStart = mouse.getScreen();
                boxSelectStart = mouse.getWorld();
                boxSelectSet = true;
            }
            screenBoxSelectEnd = mouse.getScreen();
            boxSelectEnd = mouse.getWorld();
            Vector2f boxSelectStartWorld = boxSelectStart;
            Vector2f boxSelectEndWorld = boxSelectEnd;
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f), 0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int) screenBoxSelectStart.x;
            int screenStartY = (int) screenBoxSelectStart.y;
            int screenEndX = (int) screenBoxSelectEnd.x;
            int screenEndY = (int) screenBoxSelectEnd.y;
            screenBoxSelectStart.zero();
            screenBoxSelectEnd.zero();
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY), new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int) objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
                if (pickedObj != null) {
                    Window.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }
}
