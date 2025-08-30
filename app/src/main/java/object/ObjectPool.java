package object;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import graphics.Shader;
import graphics.Texture;
import logger.GlobalLogger;
import sound.Sound;

public final class ObjectPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    private ObjectPool() {}

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (ObjectPool.shaders.containsKey(file.getAbsolutePath())) {
            return ObjectPool.shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            ObjectPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (ObjectPool.textures.containsKey(file.getAbsolutePath())) {
            return ObjectPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            ObjectPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, SpriteSheet spritesheet) {
        File file = new File(resourceName);
        if (!ObjectPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            ObjectPool.spriteSheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static void addSpriteSheet(SpriteSheetMetadata metadata) {
        addSpriteSheet(metadata.getFilePath(),
                new SpriteSheet(getTexture(metadata.getFilePath()), metadata.getSpriteWidth(),
                        metadata.getSpriteHeight(), metadata.getSpriteQuantity(),
                        metadata.getSpace()));
    }

    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if (!ObjectPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            GlobalLogger.getGlobalLogger().getLogger().warning(
                    () -> String.format("resourceName=$1$s not yet in sprite sheet", resourceName));
        }
        return ObjectPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static SpriteSheet getSpriteSheet(SpriteSheetMetadata metadata) {
        return getSpriteSheet(metadata.getFilePath());
    }

    public static Sound addSound(SoundMetadata metadata) {
        return addSound(metadata.getFilePath(), metadata.isLooping());
    }

    public static Sound addSound(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            ObjectPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }

    public static Sound getSound(SoundMetadata metadata) {
        return getSound(metadata.getFilePath());
    }

    public static Sound getSound(String soundFile) {
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            GlobalLogger.getGlobalLogger().getLogger()
                    .warning(() -> String.format("soundFile=%1$s not in sounds", soundFile));
        }

        return null;
    }

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }
}
