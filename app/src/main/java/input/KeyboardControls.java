package input;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import component.Component;
import component.StateMachine;
import gui.PropertiesWindow;
import object.GameObject;
import setting.EngineSettings;
import window.Window;

public class KeyboardControls extends Component {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    private Keyboard keyboard;

    public KeyboardControls() {
        keyboard = Keyboard.getKeyboard();
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        float multiplier = keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
                && keyboard.isKeyInitiallyPressed(GLFW.GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            newObj.transform.position.add(EngineSettings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
            if (newObj.getComponent(StateMachine.class) != null) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)
                && keyboard.isKeyInitiallyPressed(GLFW.GLFW_KEY_D)
                && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (keyboard.isKeyInitiallyPressed(GLFW.GLFW_KEY_DELETE)
                || keyboard.isKeyInitiallyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex--;
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex++;
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += EngineSettings.GRID_HEIGHT * multiplier;
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= EngineSettings.GRID_HEIGHT * multiplier;
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += EngineSettings.GRID_HEIGHT * multiplier;
            }
        } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= EngineSettings.GRID_HEIGHT * multiplier;
            }
        }
    }
}
