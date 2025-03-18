package org.example;

import components.SpriteRenderer;
import rendering.Shader;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
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
        shader = new Shader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        shader.compileAndLinkShaders();
        this.sprites = new SpriteRenderer[batchSize];
        this.maxBatchSize = batchSize;

        //There are batchsize tiles of four vertecis that each have size 6
        vertices = new float[batchSize * 4 * VERTEX_SIZE];

        this.numberSprites = 0;
        this.hasRoom = true;
    }

    private void start() {
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
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices.length, GL_STATIC_DRAW);

        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,COLOR_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    private int[] generateIndices() {
        //3 indices per triangle and two triangles for one rectangle
        int[] elements = new int[6* maxBatchSize];
        for(int i=0; i < maxBatchSize; i++) {
            elements[i] = i*4+3;
            elements[i+1] = i*4+2;
            elements[i+2] = i*4;
            elements[i+3] = i*4;
            elements[i+4] = i*4+2;
            elements[i+5] = i*4+1;
        }
        return elements;
    }
}
