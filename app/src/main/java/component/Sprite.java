package component;

import org.joml.Vector2f;
import graphics.Texture;

public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] texCoords =
            {new Vector2f(1, 1), new Vector2f(1, 0), new Vector2f(0, 0), new Vector2f(0, 1)};

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setTexture(Texture tex) {
        texture = tex;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public int getTexId() {
        return texture == null ? -1 : texture.getId();
    }
}
