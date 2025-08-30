package scene;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.joml.Vector2f;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import camera.Camera;
import component.Component;
import component.ComponentDeserializer;
import component.Transform;
import graphics.Renderer;
import object.GameObject;
import object.GameObjectDeserializer;
import physics.Physics;
import setting.EngineSettings;

public class Scene {

    private Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private List<GameObject> pendingObjects;
    private Physics physics2D;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        physics2D = new Physics();
        renderer = new Renderer();
        gameObjects = new ArrayList<>();
        pendingObjects = new ArrayList<>();
        isRunning = false;
    }

    public Physics getPhysics() {
        return physics2D;
    }

    public void initialize() {
        camera = new Camera(new Vector2f(0, 0));
        sceneInitializer.loadResources(this);
        sceneInitializer.initialize(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.start();
            renderer.add(gameObject);
            physics2D.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        if (!isRunning) {
            gameObjects.add(gameObject);
        } else {
            pendingObjects.add(gameObject);
        }
    }

    public void terminate() {
        for (GameObject gameObject : gameObjects) {
            gameObject.destroy();
        }

        sceneInitializer.terminate(this);
    }

    public <T extends Component> GameObject getGameObjectWith(Class<T> componentClass) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getComponent(componentClass) != null) {
                return gameObject;
            }
        }

        return null;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId).findFirst();
        return result.orElse(null);
    }

    public void editorUpdate(float deltaTime) {
        camera.adjustProjection();

        for (int i = gameObjects.size() - 1; i > -1; i--) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.editorUpdate(deltaTime);

            if (gameObject.isDead()) {
                gameObjects.remove(i);
                renderer.destroyGameObject(gameObject);
                physics2D.destroyGameObject(gameObject);
            }
        }

        for (GameObject pendingObject : pendingObjects) {
            gameObjects.add(pendingObject);
            pendingObject.start();
            renderer.add(pendingObject);
            physics2D.add(pendingObject);
        }
        pendingObjects.clear();
    }

    public GameObject getGameObject(String gameObjectName) {
        Optional<GameObject> result = gameObjects.stream()
                .filter(gameObject -> gameObject.name.equals(gameObjectName)).findFirst();
        return result.orElse(null);
    }

    public void update(float deltaTime) {
        camera.adjustProjection();
        physics2D.update(deltaTime);

        for (int i = gameObjects.size() - 1; i > -1; i--) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.update(deltaTime);

            if (gameObject.isDead()) {
                gameObjects.remove(i);
                renderer.destroyGameObject(gameObject);
                physics2D.destroyGameObject(gameObject);
            }
        }

        for (GameObject pendingObject : pendingObjects) {
            gameObjects.add(pendingObject);
            pendingObject.start();
            renderer.add(pendingObject);
            physics2D.add(pendingObject);
        }
        pendingObjects.clear();
    }

    public void render() {
        renderer.render();
    }

    public Camera getCamera() {
        return camera;
    }

    public void imGui() {
        sceneInitializer.imGui();
    }

    public GameObject createGameObject(String name) {
        GameObject gameObject = new GameObject(name);
        gameObject.addComponent(new Transform());
        gameObject.transform = gameObject.getComponent(Transform.class);
        return gameObject;
    }

    public void saveFile() {
        saveFile("");
    }

    public void saveFile(String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization().create();

        try {
            FileWriter writer = new FileWriter(String.format("../scenes/%1$s.%2$s",
                    fileName.isBlank() ? EngineSettings.DEFAULT_GAME_SCENE_FILE_NAME : fileName,
                    EngineSettings.SCENE_FILE_EXTENSION));
            List<GameObject> serializableObjects = new ArrayList<>();
            for (GameObject gameObject : gameObjects) {
                if (gameObject.doSerialization()) {
                    serializableObjects.add(gameObject);
                }
            }
            writer.write(gson.toJson(serializableObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile() {
        loadFile("");
    }

    public void loadFile(String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization().create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(String.format("../scenes/%1$s.%2$s",
                    fileName.isBlank() ? EngineSettings.DEFAULT_GAME_SCENE_FILE_NAME : fileName,
                    EngineSettings.SCENE_FILE_EXTENSION))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objects.length; i++) {
                addGameObjectToScene(objects[i]);

                for (Component component : objects[i].getAllComponents()) {
                    if (component.getUid() > maxCompId) {
                        maxCompId = component.getUid();
                    }
                }
                if (objects[i].getUid() > maxGoId) {
                    maxGoId = objects[i].getUid();
                }
            }

            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }
}
