package scene.scenes;

import camera.GameCamera;
import component.SpriteRenderer;
import component.StateMachine;
import object.GameObject;
import object.ObjectPool;
import scene.Scene;
import scene.SceneInitializer;
import setting.EngineSettings;

public class GameScene extends SceneInitializer {

        @Override
        public void initialize(Scene scene) {
                GameObject cameraObject = scene.createGameObject("GameCamera");
                cameraObject.addComponent(new GameCamera(scene.getCamera()));
                scene.getCamera().addZoom(EngineSettings.CAMERA_ZOOM_OUT);
                cameraObject.start();
                cameraObject.setNoSerialize();
                scene.addGameObjectToScene(cameraObject);

                ObjectPool.getSound((EngineSettings.DEFAULT_BACKGROUND_MUSIC.getFilePath())).play();
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

                for (GameObject g : scene.getGameObjects()) {
                        if (g.getComponent(SpriteRenderer.class) != null) {
                                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                                if (spr.getTexture() != null) {
                                        spr.setTexture(ObjectPool.getTexture(
                                                        spr.getTexture().getFilepath()));
                                }
                        }

                        if (g.getComponent(StateMachine.class) != null) {
                                StateMachine stateMachine = g.getComponent(StateMachine.class);
                                stateMachine.refreshTextures();
                        }
                }
        }

        @Override
        public void terminate(Scene scene) {
                ObjectPool.getSound((EngineSettings.DEFAULT_BACKGROUND_MUSIC.getFilePath())).stop();
        }

        @Override
        public void imGui() {}
}
