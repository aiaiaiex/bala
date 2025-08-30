package graphics;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import component.SpriteRenderer;
import object.GameObject;
import window.Window;

public class RenderBatch implements Comparable<RenderBatch> {
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    private Renderer renderer;

    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;

        this.zIndex = zIndex;
        sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        numSprites = 0;
        hasRoom = true;
        textures = new ArrayList<>();
    }

    public void start() {
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.length * Float.BYTES,
                GL15.GL_DYNAMIC_DRAW);

        int eboID = GL15.glGenBuffers();
        int[] indices = generateIndices();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL20C.glVertexAttribPointer(0, POS_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES,
                POS_OFFSET);
        GL20.glEnableVertexAttribArray(0);

        GL20C.glVertexAttribPointer(1, COLOR_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES,
                COLOR_OFFSET);
        GL20.glEnableVertexAttribArray(1);

        GL20C.glVertexAttribPointer(2, TEX_COORDS_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES,
                TEX_COORDS_OFFSET);
        GL20.glEnableVertexAttribArray(2);

        GL20C.glVertexAttribPointer(3, TEX_ID_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES,
                TEX_ID_OFFSET);
        GL20.glEnableVertexAttribArray(3);

        GL20C.glVertexAttribPointer(4, ENTITY_ID_SIZE, GL11.GL_FLOAT, false, VERTEX_SIZE_BYTES,
                ENTITY_ID_OFFSET);
        GL20.glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer spr) {
        int index = numSprites;
        sprites[index] = spr;
        numSprites++;

        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        loadVertexProperties(index);

        if (numSprites >= maxBatchSize) {
            hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()) {
                if (!hasTexture(spr.getTexture())) {
                    renderer.destroyGameObject(spr.gameObject);
                    renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }

            if (spr.gameObject.transform.zIndex != zIndex) {
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }
        if (rebufferData) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL11.glDrawElements(GL11.GL_TRIANGLES, numSprites * 6, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }

    public boolean destroyIfExists(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == sprite) {
                for (int j = i; j < numSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }

        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                    sprite.gameObject.transform.position.y, 0f);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.transform.rotation), 0,
                    0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x,
                    sprite.gameObject.transform.scale.y, 1);
        }

        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(
                    sprite.gameObject.transform.position.x
                            + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y
                            + (yAdd * sprite.gameObject.transform.scale.y),
                    0, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            vertices[offset + 8] = texId;

            vertices[offset + 9] = sprite.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasTextureRoom() {
        return textures.size() < 7;
    }

    public boolean hasTexture(Texture tex) {
        return textures.contains(tex);
    }

    public int zIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(zIndex, o.zIndex());
    }
}
