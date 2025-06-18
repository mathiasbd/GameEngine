package rendering;

import components.SpriteRenderer;
import org.example.GameEngineManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import rendering.Shader;
import util.AssetPool;
import util.DTUMath;
import util.Time;
import org.joml.Vector2f;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/*
 * RenderBatch manages a batch of SpriteRenderers for efficient rendering.
 * It groups sprites sharing the same shader and textures into a single draw call.
 * Author(s): Mathias, Ahmed
 */
public class RenderBatch implements Comparable<RenderBatch> {
    ////////////////////////////////
    //      Vertex structure     //
    //////////////////////////////
    //Pos                color                              tex coords      tex id
    //float,float       float,float,float,float             float, float    float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;


    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;


    private List<Texture> texture;

    private Shader shader;
    private int vaoID, vboID, eboID;
    private float[] vertices;

    private SpriteRenderer[] sprites;
    private int numberSprites;
    private boolean hasRoom;
    private boolean needsRebuffer = false;
    private int []texSlots={0,1,2,3,4,5,6,7};

    private int maxBatchSize;

    private int zIndex;

    /*
     * Constructs a RenderBatch with capacity for a given number of sprites at a specified z-index.
     * @param batchSize - maximum number of sprites this batch can hold
     * @param zIndex - depth ordering index for rendering
     */
    public RenderBatch(int batchSize, int zIndex) {
        shader = AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        this.sprites = new SpriteRenderer[batchSize];
        this.maxBatchSize = batchSize;

        //There are batchSize tiles of four vertices that each have size 9
        vertices = new float[batchSize * 4 * VERTEX_SIZE];

        this.numberSprites = 0;
        this.hasRoom = true;
        this.texture = new ArrayList<>();
        this.zIndex = zIndex;
    }

    /*
     * Initializes OpenGL buffers and configures vertex attributes for this batch.
     */
    public void start() {
        // Our VAO, VBO and EBO buffer Objects.
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        int[] indices = generateIndices();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    /*
     * Renders all sprites in this batch, rebuffering if necessary and binding textures.
     */
    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numberSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.getIsDirty() || needsRebuffer) {
                if (spr.getTexture() != null && !texture.contains(spr.getTexture())) {
                    if (hasTextureRoom()) {
                        texture.add(spr.getTexture());
                    } else {
                        System.err.println("No room for new texture in batch!");
                        continue;
                    }
                }
                rebufferData = true;
                loadVertexProperties(i);
                this.needsRebuffer = false;
                spr.setClean();
            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        shader.useProgram();
        for (int i = 0; i < texture.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            texture.get(i).bind();
        }
        shader.uploadIntArray("uTexture", texSlots);
        shader.uploadMat4f("uProjection", GameEngineManager.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", GameEngineManager.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, this.numberSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        for (int i = 0; i < texture.size(); i++) {
            texture.get(i).unbind();
        }
        shader.detach();
    }

    /*
     * Generates index array for element buffer, creating two triangles per sprite.
     * @return int[] - index array for all sprites in the batch
     */
    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            int offset = i * 4;
            int arrayOffset = i * 6;
            elements[arrayOffset] = offset + 3;
            elements[arrayOffset + 1] = offset + 2;
            elements[arrayOffset + 2] = offset;
            elements[arrayOffset + 3] = offset;
            elements[arrayOffset + 4] = offset + 2;
            elements[arrayOffset + 5] = offset + 1;
        }
        return elements;
    }

    /*
     * Adds a sprite to this batch and loads its vertex data.
     * @param sprite - SpriteRenderer to add
     */
    public void addSprite(SpriteRenderer sprite) {
        int index = this.numberSprites;
        sprites[index] = sprite;
        numberSprites++;
        if (sprite.getTexture() != null && !texture.contains(sprite.getTexture())) {
            texture.add(sprite.getTexture());
        }
        loadVertexProperties(index);
        if (numberSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    /*
     * Removes a sprite from this batch and compacts the vertex buffer.
     * @param sprite - SpriteRenderer to remove
     */
    public void removeSprite(SpriteRenderer sprite) {
        for (int i = 0; i < numberSprites; i++) {
            if (sprites[i] == sprite) {
                for (int j = i; j < numberSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    System.arraycopy(vertices,
                            (j + 1) * 4 * VERTEX_SIZE,
                            vertices,
                            j * 4 * VERTEX_SIZE,
                            4 * VERTEX_SIZE);
                }
                sprites[numberSprites - 1] = null;
                int offset = (numberSprites - 1) * 4 * VERTEX_SIZE;
                for (int k = 0; k < 4 * VERTEX_SIZE; k++) {
                    vertices[offset + k] = 0.0f;
                }
                numberSprites--;
                hasRoom = true;
                this.needsRebuffer = true;
                break;
            }
        }
    }

    /*
     * Loads the vertex properties for a sprite at the given index into the vertex array.
     * @param index - index of the sprite in the batch
     */
    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();
        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < texture.size(); i++) {
                if (texture.get(i) == sprite.getTexture()) {
                    texId = i + 1;
                    break;
                }
            }
        }
        float xs = 1.0f;
        float ys = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) ys = 0.0f;
            else if (i == 2) xs = 0.0f;
            else if (i == 3) ys = 1.0f;
            Vector2f leftCorner = new Vector2f(
                    sprite.gameObject.transform.position.x + (xs * sprite.gameObject.transform.scale.x) -
                            (sprite.gameObject.transform.scale.x / 2),
                    sprite.gameObject.transform.position.y + (ys * sprite.gameObject.transform.scale.y) -
                            (sprite.gameObject.transform.scale.y / 2)
            );
            DTUMath.rotate(leftCorner, sprite.gameObject.transform.getRotation(), sprite.gameObject.transform.getPosition());
            vertices[offset] = leftCorner.x;
            vertices[offset + 1] = leftCorner.y;
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            vertices[offset + 8] = texId;
            offset += VERTEX_SIZE;
        }
    }

    /*
     * @return true if there is room for more sprites in this batch
     */
    public boolean hasRoom() {
        return this.hasRoom;
    }

    /*
     * @return true if there is room for additional textures (max 8)
     */
    public boolean hasTextureRoom() {
        return this.texture.size() < 8;
    }

    /*
     * @param sprite - SpriteRenderer to check
     * @return true if the specified sprite is in this batch
     */
    public boolean hasSprite(SpriteRenderer sprite) {
        for (int i = 0; i < numberSprites; i++) {
            if (sprites[i] == sprite) {
                return true;
            }
        }
        return false;
    }

    /*
     * @param texture - Texture to check
     * @return true if the specified texture is bound in this batch
     */
    public boolean hasTexture(Texture texture) {
        return this.texture.contains(texture);
    }

    /*
     * @return z-index ordering value for this batch
     */
    public int getzIndex() {
        return zIndex;
    }

    /*
     * Compares batches by their z-index for sorting.
     * @param o - another RenderBatch to compare against
     * @return negative, zero, or positive if this batch's z-index is less than, equal to, or greater than the other's
     */
    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.getzIndex(), o.getzIndex());
    }
}
