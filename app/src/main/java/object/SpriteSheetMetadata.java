package object;

public class SpriteSheetMetadata {
    private String filePath;
    private int spriteWidth, spriteHeight;
    private int spriteQuantity;
    private int space;

    private SpriteSheetMetadata() {}

    public SpriteSheetMetadata(String filePath, int spriteWidth, int spriteHeight,
            int spriteQuantity) {
        this(filePath, spriteWidth, spriteHeight, spriteQuantity, 0);
    }

    public SpriteSheetMetadata(String filePath, int spriteWidth, int spriteHeight,
            int spriteQuantity, int space) {
        this.filePath = filePath;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.spriteQuantity = spriteQuantity;
        this.space = space;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getSpriteQuantity() {
        return spriteQuantity;
    }

    public int getSpace() {
        return space;
    }
}
