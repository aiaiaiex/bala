package component;

import org.joml.Vector2f;
import org.joml.Vector4f;
import graphics.Texture;
import gui.Properties;
import object.ObjectPool;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    @Override
    public void start() {
        if (sprite.getTexture() != null) {
            sprite.setTexture(ObjectPool.getTexture(sprite.getTexture().getFilepath()));
        }
        lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
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
