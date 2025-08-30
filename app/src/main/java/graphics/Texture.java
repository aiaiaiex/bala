package graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import logger.GlobalLogger;

public class Texture {
    private String filepath;
    private transient int texID;
    private int width, height;

    public Texture() {
        texID = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height) {
        this.filepath = "Generated";

        texID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE, 0);
    }

    public void init(String filepath) {
        this.filepath = filepath;

        texID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = STBImage.stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

            if (channels.get(0) == 3) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width.get(0), height.get(0),
                        0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(0), height.get(0),
                        0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            } else {
                GlobalLogger.getGlobalLogger().getLogger().warning(() -> String
                        .format("Number of channels=%1$s not allowed", channels.get(0)));
            }
        } else {
            GlobalLogger.getGlobalLogger().getLogger()
                    .warning(() -> String.format("filepath=%1$s not loaded", filepath));
        }

        STBImage.stbi_image_free(image);
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return texID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Texture))
            return false;
        Texture oTex = (Texture) o;
        return oTex.getWidth() == width && oTex.getHeight() == height && oTex.getId() == texID
                && oTex.getFilepath().equals(filepath);
    }
}
