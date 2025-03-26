package rendering;

import components.SpriteRenderer;
import org.example.GameEngineManager;
import org.joml.Vector4f;
import rendering.Shader;
import util.AssetPool;
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

public class RenderBatch {
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
    private int []texSlots={0,1,2,3,4,5,6,7};

    private int maxBatchSize;
    public RenderBatch(int batchSize) {
        shader = AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        this.sprites = new SpriteRenderer[batchSize];
        this.maxBatchSize = batchSize;

        //There are batchsize tiles of four vertecis that each have size 6
        vertices = new float[batchSize * 4 * VERTEX_SIZE];

        this.numberSprites = 0;
        this.hasRoom = true;
        this.texture = new ArrayList<>();
    }

    public void start() {
        //Our VAO, VBO and EBO buffer Objects.
        //Vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create the VBO vertex buffer array with size
        vboID =glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        eboID =glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        int[] indices = generateIndices();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,COLOR_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2,TEX_COORDS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);


        glVertexAttribPointer(3,TEX_ID_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

    }

    public void render() {
        //We rebuffer all data every frame for now
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //Use shader
        shader.useProgram();
        //find textures to bind
        for (int i=0;i<texture.size();i++){
            glActiveTexture(GL_TEXTURE0+i+1);
            texture.get(i).bind();
        }
        shader.uploadIntArray("uTexture", texSlots);
        //Upload projection matrix
        shader.uploadMat4f("uProjection", GameEngineManager.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", GameEngineManager.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);

        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Draw the elements which is the number of sprites times 6
        glDrawElements(GL_TRIANGLES,this.numberSprites * 6,GL_UNSIGNED_INT,0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        for (int i=0;i<texture.size();i++){
            texture.get(i).unbind();
        }
        shader.detach();
    }

    private int[] generateIndices() {
        //3 indices per triangle and two triangles for one rectangle
        int[] elements = new int[6* maxBatchSize];
        for(int i=0; i < maxBatchSize; i++) {
            int offset = i*4;
            int arrayOffset = i*6;
            elements[arrayOffset] = offset+3;
            elements[arrayOffset+1] = offset+2;
            elements[arrayOffset+2] = offset;
            elements[arrayOffset+3] = offset;
            elements[arrayOffset+4] = offset+2;
            elements[arrayOffset+5] = offset+1;
        }
        return elements;
    }

    public void addSprite(SpriteRenderer sprite) {
        int index = this.numberSprites;
        sprites[index] = sprite;
        numberSprites++;
        if (sprite.getTexture() != null) {
            if (!texture.contains(sprite.getTexture())) {
                texture.add(sprite.getTexture());
            }
        }
        loadVertexProperties(index);

        if(numberSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        // Retrieve texture coordinates
        Vector2f[] texCoords = sprite.getTexCoords();

        int texId =0;
        //loop to find the tex that matches the sprite we are at
        if (sprite.getTexture() != null) {
            for (int i = 0; i < texture.size(); i++) {
                if (texture.get(i) == sprite.getTexture()) {
                    texId = i+1;  // OpenGL expects texture IDs starting from 1
                    break;
                }
            }
        }
        System.out.println(sprite.gameObject.getName() + " - Texture ID: " + texId);


        float xs = 1.0f;
        float ys = 1.0f;
        for(int i = 0; i < 4; i++) {
            if(i == 1) {
                ys = 0.0f;
            } else if(i == 2) {
                xs = 0.0f;
            } else if(i == 3) {
                ys = 1.0f;
            }
            //load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xs * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (ys * sprite.gameObject.transform.scale.y);
            //load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;
            //load texture coordinates
            vertices[offset + 6] =texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            //load texture id
            vertices[offset + 8] = texId;
            offset += VERTEX_SIZE;

        }
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.texture.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.texture.contains(texture);
    }
}
