package component;

import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import graphics.Texture;
import gui.Properties;
import object.ObjectPool;
import procedural.SimplexNoise;
import setting.EngineSettings;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    private transient List<Sprite> sprites;

    private transient float changeSpriteCooldown = 0.0f;
    private transient float changeSpriteTimer = changeSpriteCooldown;

    private boolean procedurallyUpdate;

    @Override
    public void start() {
        if (sprite.getTexture() != null) {
            sprite.setTexture(ObjectPool.getTexture(sprite.getTexture().getFilepath()));
        }
        lastTransform = gameObject.transform.copy();

        if (procedurallyUpdate) {
            sprites = ObjectPool.getSpriteSheet(EngineSettings.NON_COLLIDABLE_TERRAIN.getFilePath())
                    .getSprites();
        }
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }

        changeSpriteTimer -= dt;
        if (changeSpriteTimer <= 0.0f && procedurallyUpdate) {
            int noise =
                    (int) Math.floor((SimplexNoise.getNoise(gameObject.transform.position.x * 10,
                            gameObject.transform.position.y * 10, (float) GLFW.glfwGetTime() * 10)
                            * 0.5f + 0.5f) * sprites.size());

            sprite = sprites.get(noise);
            if (noise == 0) {
                color = new Vector4f(173.0f / 255.0f, 216.0f / 255.0f, 230.0f / 255.0f, 1);
            } else {
                color = new Vector4f(255.0f / 255.0f, 255.0f / 255.0f, 255.0f / 255.0f, 1);
            }

            isDirty = true;
            changeSpriteTimer = changeSpriteCooldown;


            // Use for fog effect.
            // color = new Vector4f(1, 1, 1,
            // ProceduralNoise.getNoise(gameObject.transform.position.x * 10,
            // gameObject.transform.position.y * 10,
            // (float) GLFW.glfwGetTime() * 10) >= 0 ? 1.0f : 0.75f);

            // Use for more realistic fog effect.
            // color = new Vector4f(1, 1, 1,
            // (ProceduralNoise.getNoise(gameObject.transform.position.x * 10,
            // gameObject.transform.position.y * 10, (float) GLFW.glfwGetTime() * 10)
            // + 1.0f) / 2.0f + 0.1f);
        }

    }

    @Override
    public void editorUpdate(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        if (Properties.colorPicker4("Color Picker", color)) {
            isDirty = true;
        }
    }

    public void setProcedurallyUpdate(boolean procedurallyUpdate) {
        this.procedurallyUpdate = procedurallyUpdate;
    }

    public void setDirty() {
        isDirty = true;
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            isDirty = true;
            this.color.set(color);
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        isDirty = false;
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
    }
}
