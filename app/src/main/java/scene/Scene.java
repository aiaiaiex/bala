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
import game.Enemy;
import graphics.Renderer;
import object.GameObject;
import object.GameObjectDeserializer;
import object.GameObjectGenerator;
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

    private boolean isGameScene;
    private float generateEnemiesCooldown = 1.0f;
    private float generateEnemiesTimer = generateEnemiesCooldown;
    private List<GameObject> enemies = new ArrayList<>();

    public Scene(SceneInitializer sceneInitializer) {
        this(sceneInitializer, false);
    }

    public Scene(SceneInitializer sceneInitializer, boolean isGameScene) {
        this.sceneInitializer = sceneInitializer;
        physics2D = new Physics();
        renderer = new Renderer();
        gameObjects = new ArrayList<>();
        pendingObjects = new ArrayList<>();
        isRunning = false;

        this.isGameScene = isGameScene;
    }

    public Physics getPhysics() {
        return physics2D;
    }

    public void initialize() {
        camera = new Camera(new Vector2f(0, 0));
        sceneInitializer.loadResources(this);
        sceneInitializer.initialize(this);

        if (EngineSettings.GENERATE_ENEMIES_INITIALLY_WHILE_PLAYING && isGameScene) {
            fillSceneWithEnemies();
        }
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.start();
            renderer.add(gameObject);
            physics2D.add(gameObject);

            if (gameObject.getComponent(Enemy.class) != null) {
                enemies.add(gameObject);
            }
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

        generateEnemiesTimer -= deltaTime;
        if (EngineSettings.PROCEDURALLY_GENERATE_ENEMIES_WHILE_PLAYING && isGameScene
                && generateEnemiesTimer <= 0.0f) {
            GameObjectGenerator.procedurallyGenerateEnemies(camera).forEach(gameObject -> {
                addGameObjectToScene(gameObject);
                enemies.add(gameObject);
            });

            generateEnemiesTimer = generateEnemiesCooldown;
        }
    }

    public int getEnemyCount() {
        return enemies.size();
    }

    public List<GameObject> getEnemies() {
        return enemies;
    }

    public void clearScene() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.name.equals("LevelEditor") || gameObject.name.equals("GameCamera")) {
                continue;
            }
            gameObject.destroy();
        }
    }

    public void fillSceneWithNonCollidableTerrain() {
        GameObjectGenerator.procedurallyGenerateNonCollidableTerrain(camera)
                .forEach(this::addGameObjectToScene);
    }

    public void fillSceneWithEnemies() {
        GameObjectGenerator.generateEnemies(camera, EngineSettings.ENEMY_COUNT_TO_GENERATE)
                .forEach(gameObject -> {
                    addGameObjectToScene(gameObject);
                    enemies.add(gameObject);
                });
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
            FileWriter writer = new FileWriter(String.format("./scenes/%1$s.%2$s",
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
            inFile = new String(Files.readAllBytes(Paths.get(String.format("./scenes/%1$s.%2$s",
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
