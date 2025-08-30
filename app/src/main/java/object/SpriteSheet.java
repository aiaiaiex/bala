package object;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import component.Sprite;
import graphics.Texture;

public class SpriteSheet {

    private Texture texture;
    private List<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites,
            int spacing) {
        sprites = new ArrayList<>();

        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        for (int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] texCoords = {new Vector2f(rightX, topY), new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY), new Vector2f(leftX, topY)};
            Sprite sprite = new Sprite();
            sprite.setTexture(texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    public int size() {
        return sprites.size();
    }
}
