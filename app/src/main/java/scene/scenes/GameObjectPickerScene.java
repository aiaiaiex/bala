package scene.scenes;

import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import camera.EditorCamera;
import component.GridLines;
import component.Sprite;
import component.SpriteRenderer;
import component.StateMachine;
import imgui.ImGui;
import imgui.ImVec2;
import input.KeyboardControls;
import input.MouseControls;
import object.GameObject;
import object.GameObjectGenerator;
import object.ObjectPool;
import object.SpriteSheet;
import physics.CircleCollider;
import physics.Rigidbody2D;
import scene.Scene;
import scene.SceneInitializer;
import setting.EngineSettings;

public class GameObjectPickerScene extends SceneInitializer {

    private SpriteSheet collidableTerrain;
    private SpriteSheet nonCollidableTerrain;
    private SpriteSheet players;
    private SpriteSheet enemies;
    private SpriteSheet drops;
    private GameObject levelEditor;

    @Override
    public void initialize(Scene scene) {
        collidableTerrain = ObjectPool.getSpriteSheet(EngineSettings.COLLIDABLE_TERRAIN);
        nonCollidableTerrain = ObjectPool.getSpriteSheet(EngineSettings.NON_COLLIDABLE_TERRAIN);
        players = ObjectPool.getSpriteSheet(EngineSettings.PLAYERS);
        enemies = ObjectPool.getSpriteSheet(EngineSettings.ENEMIES);
        drops = ObjectPool.getSpriteSheet(EngineSettings.DROPS);

        levelEditor = scene.createGameObject("LevelEditor");
        levelEditor.setNoSerialize();
        levelEditor.addComponent(new MouseControls());
        levelEditor.addComponent(new KeyboardControls());
        levelEditor.addComponent(new GridLines());
        levelEditor.addComponent(new EditorCamera(scene.getCamera()));
        scene.addGameObjectToScene(levelEditor);
    }

    @Override
    public void loadResources(Scene scene) {
        ObjectPool.getShader(EngineSettings.DEFAULT_SHADER);

        ObjectPool.addSpriteSheet(EngineSettings.COLLIDABLE_TERRAIN);
        ObjectPool.addSpriteSheet(EngineSettings.NON_COLLIDABLE_TERRAIN);
        ObjectPool.addSpriteSheet(EngineSettings.PLAYERS);
        ObjectPool.addSpriteSheet(EngineSettings.ENEMIES);
        ObjectPool.addSpriteSheet(EngineSettings.PROJECTILES);
        ObjectPool.addSpriteSheet(EngineSettings.DROPS);

        ObjectPool.addSound(EngineSettings.DEFAULT_BACKGROUND_MUSIC);
        ObjectPool.addSound(EngineSettings.DEFAULT_PLAYER_DEATH_SOUND);
        ObjectPool.addSound(EngineSettings.DEFAULT_ENEMY_DEATH_SOUND);
        ObjectPool.addSound(EngineSettings.DEFAULT_RELEASE_SOUND);
        ObjectPool.addSound(EngineSettings.DEFAULT_IMPACT_SOUND);
        ObjectPool.addSound(EngineSettings.DEFAULT_PICKUP_SOUND);

        for (GameObject gameObject : scene.getGameObjects()) {
            if (gameObject.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
                if (spriteRenderer.getTexture() != null) {
                    spriteRenderer.setTexture(
                            ObjectPool.getTexture(spriteRenderer.getTexture().getFilepath()));
                }
            }

            if (gameObject.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void terminate(Scene scene) {}

    @Override
    public void imGui() {
        ImGui.begin("Assets");

        if (ImGui.beginTabBar("WindowTabBar")) {

            if (ImGui.beginTabItem("Non-Collidable Terrain")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;

                for (int i = 0; i < nonCollidableTerrain.size(); i++) {

                    Sprite sprite = nonCollidableTerrain.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,
                            texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = GameObjectGenerator.generateSpriteObject(sprite,
                                EngineSettings.GRID_WIDTH, EngineSettings.GRID_HEIGHT);
                        levelEditor.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < nonCollidableTerrain.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Collidable Terrain")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < collidableTerrain.size(); i++) {

                    Sprite sprite = collidableTerrain.getSprite(i);
                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,
                            texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject gameObject = GameObjectGenerator.generateSpriteObject(sprite,
                                EngineSettings.GRID_WIDTH, EngineSettings.GRID_HEIGHT);

                        Rigidbody2D rigidBody = new Rigidbody2D();
                        rigidBody.setBodyType(BodyType.STATIC);
                        gameObject.addComponent(rigidBody);

                        CircleCollider circleCollider = new CircleCollider();
                        circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2);
                        gameObject.addComponent(circleCollider);

                        gameObject.transform.zIndex = 1;

                        levelEditor.getComponent(MouseControls.class).pickupObject(gameObject);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < collidableTerrain.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Players")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < players.size(); i++) {

                    Sprite sprite = players.getSprite(i);
                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,
                            texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject gameObject = GameObjectGenerator.generatePlayer(i);
                        levelEditor.getComponent(MouseControls.class).pickupObject(gameObject);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < players.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Enemies")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < enemies.size(); i++) {

                    Sprite sprite = enemies.getSprite(i);
                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,
                            texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject gameObject = GameObjectGenerator.generateEnemy(i);
                        levelEditor.getComponent(MouseControls.class).pickupObject(gameObject);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < enemies.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Drops")) {

                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < drops.size(); i++) {

                    Sprite sprite = drops.getSprite(i);
                    float spriteWidth = 64;
                    float spriteHeight = 64;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x,
                            texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject gameObject = GameObjectGenerator.generateDrop();
                        levelEditor.getComponent(MouseControls.class).pickupObject(gameObject);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < drops.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }

                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }
}
