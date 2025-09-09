package graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import logger.GlobalLogger;

public class Texture {
    private String filePath;
    private int width, height;
    private transient int textureName;

    public Texture(String filePath) {
        this.filePath = filePath;

        textureName = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = STBImage.stbi_load(filePath, widthBuffer, heightBuffer, channels, 0);

        if (image != null) {
            width = widthBuffer.get(0);
            height = heightBuffer.get(0);

            if (channels.get(0) == 3) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, widthBuffer.get(0),
                        heightBuffer.get(0), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, widthBuffer.get(0),
                        heightBuffer.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            } else {
                GlobalLogger.getGlobalLogger().getLogger().warning(
                        () -> String.format("Cannot support channels=%1$s", channels.get(0)));
            }

            STBImage.stbi_image_free(image);
        } else {
            GlobalLogger.getGlobalLogger().getLogger()
                    .warning(() -> String.format("Failed to load filePath=%1$s", filePath));
        }
    }

    public Texture(int width, int height) {
        this.filePath = "";

        textureName = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE, 0);
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureName);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public String getFilePath() {
        return filePath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureName() {
        return textureName;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof Texture))
            return false;
        Texture texture = (Texture) object;
        return texture.getWidth() == width && texture.getHeight() == height
                && texture.getTextureName() == textureName
                && texture.getFilePath().equals(filePath);
    }
}
