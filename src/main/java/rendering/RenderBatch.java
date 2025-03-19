package rendering;

import components.SpriteRenderer;
import org.example.GameEngineManager;
import org.joml.Vector4f;
import rendering.Shader;
import util.AssetPool;
import util.Time;

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
    //Pos                color
    //float,float       float,float,float,float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;


    private Shader shader;
    private int vaoID, vboID, eboID;
    private float[] vertices;

    private SpriteRenderer[] sprites;
    private int numberSprites;
    private boolean hasRoom;


    private int maxBatchSize;
    public RenderBatch(int batchSize) {
        shader = AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        this.sprites = new SpriteRenderer[batchSize];
        this.maxBatchSize = batchSize;

        //There are batchsize tiles of four vertecis that each have size 6
        vertices = new float[batchSize * 4 * VERTEX_SIZE];

        this.numberSprites = 0;
        this.hasRoom = true;
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
    }

    public void render() {
        //We rebuffer all data every frame for now
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //Use shader
        shader.useProgram();
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

        loadVertexProperties(index);

        if(numberSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

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

            vertices[offset] = sprite.gameObject.transform.position.x + (xs * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (ys * sprite.gameObject.transform.scale.y);

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;

        }
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
