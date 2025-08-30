package graphics;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import logger.GlobalLogger;

public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture;

    public PickingTexture(int width, int height) {
        if (!init(width, height)) {
            GlobalLogger.getGlobalLogger().getLogger()
                    .warning("Failed to initialize picking texture");
        }
    }

    public boolean init(int width, int height) {
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        pickingTextureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pickingTextureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F, width, height, 0, GL11.GL_RGB,
                GL11.GL_FLOAT, 0);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D, this.pickingTextureId, 0);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0,
                GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL11.GL_TEXTURE_2D, depthTexture, 0);

        GL11.glReadBuffer(GL11.GL_NONE);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            GlobalLogger.getGlobalLogger().getLogger().warning("Framebuffer is not complete");
            return false;
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        return true;
    }

    public void enableWriting() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void disableWriting() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y) {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbo);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

        float pixels[] = new float[3];
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, pixels);

        return (int) (pixels[0]) - 1;
    }

    public float[] readPixels(Vector2i start, Vector2i end) {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fbo);
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

        Vector2i size = new Vector2i(end).sub(start).absolute();
        int numPixels = size.x * size.y;
        float pixels[] = new float[3 * numPixels];
        GL11.glReadPixels(start.x, start.y, size.x, size.y, GL11.GL_RGB, GL11.GL_FLOAT, pixels);
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] -= 1;
        }

        return pixels;
    }
}
