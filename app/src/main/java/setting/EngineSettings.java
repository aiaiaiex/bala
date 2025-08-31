package setting;

import org.joml.Vector4f;
import object.SoundMetadata;
import object.SpriteSheetMetadata;

public final class EngineSettings {
        public static final String ENGINE_TITLE = "Bala";
        public static final boolean DISPLAY_EDITOR = true;

        public static final String DEFAULT_GAME_SCENE_FILE_NAME = "default-game-scene";
        public static final String SCENE_FILE_EXTENSION = "txt";

        public static final float GRID_WIDTH = 0.25f;
        public static final float GRID_HEIGHT = GRID_WIDTH;

        public static final float CAMERA_ZOOM_OUT = 0.00f;

        public static final Vector4f BACKGROUND_COLOR =
                        new Vector4f(255.0f / 255.0f, 255.0f / 255.0f, 255.0f / 255.0f, 1.0F);

        public static final SpriteSheetMetadata COLLIDABLE_TERRAIN = new SpriteSheetMetadata(
                        "./assets/images/collidable-terrain.png", 16, 16, 19);
        public static final SpriteSheetMetadata NON_COLLIDABLE_TERRAIN = new SpriteSheetMetadata(
                        "./assets/images/non-collidable-terrain.png", 16, 16, 3);
        public static final SpriteSheetMetadata PLAYERS =
                        new SpriteSheetMetadata("./assets/images/players.png", 16, 16, 13);
        public static final SpriteSheetMetadata ENEMIES =
                        new SpriteSheetMetadata("./assets/images/enemies.png", 16, 16, 13);
        public static final SpriteSheetMetadata PROJECTILES =
                        new SpriteSheetMetadata("./assets/images/projectiles.png", 16, 16, 1);
        public static final SpriteSheetMetadata DROPS =
                        new SpriteSheetMetadata("./assets/images/drops.png", 16, 16, 1);

        public static final SoundMetadata DEFAULT_BACKGROUND_MUSIC =
                        new SoundMetadata("./assets/audio/background-music.ogg", true);
        public static final SoundMetadata DEFAULT_PLAYER_DEATH_SOUND =
                        new SoundMetadata("./assets/audio/player-death-sound.ogg");
        public static final SoundMetadata DEFAULT_ENEMY_DEATH_SOUND =
                        new SoundMetadata("./assets/audio/enemy-death-sound.ogg");
        public static final SoundMetadata DEFAULT_RELEASE_SOUND =
                        new SoundMetadata("./assets/audio/release-sound.ogg");
        public static final SoundMetadata DEFAULT_IMPACT_SOUND =
                        new SoundMetadata("./assets/audio/impact-sound.ogg");
        public static final SoundMetadata DEFAULT_PICKUP_SOUND =
                        new SoundMetadata("./assets/audio/pickup-sound.ogg");

        private EngineSettings() {}
}
