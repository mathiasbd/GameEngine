package scenes;


import java.awt.event.KeyEvent;

import input.KeyboardHandler;
import org.example.Camera;
import org.example.GameEngineManager;
import org.joml.Vector2f;
import rendering.Shader;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.*;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 3.0f;

    private Shader shader;

    private float [] vertexArray={
            //postion              //color
            0.5f, -0.5f, 0.0f,      1.0f,0.0f,0.0f,1.0f,   //pos: bottom right, col: red,0
            -0.5f, 0.5f, 0.0f,      0.0f,1.0f,0.0f,1.0f,   //pos: Top Left, col: green, 1
            0.5f, 0.5f, 0.0f,       0.0f,0.0f,1.0f,1.0f,    //pos: Top Right, col: Blue, 2
            -0.5f, -0.5f, 0.0f,      1.0f,1.0f,0.0f,1.0f,   //pos: Bottom Left, col: yellow, 3
    };
    private int [] elementArray={
            2,1,0, //top right triangle
            0,1,3  //bottom left triangle
    };
    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }

    @Override
    public void init() {
        shader = new Shader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        shader.compileAndLinkShaders();
        this.camera = new Camera(new Vector2f());

        //Our VAO, VBO and EBO buffer Objects.
        //ved ikke om det her er rigtigt
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //A float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //VBO, the vertex buffer and upload
        vboID =glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);


        //The indices and upload
        IntBuffer elementBuffer =BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID =glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementBuffer, GL_STATIC_DRAW);

        //Pointers to the vertex attributes
        int positionsSize =3;
        int colorSize =4;
        int floatSizeBytes=4;                        //a size of a float is 4 bytes
        int vertexSizeBytes=(positionsSize+colorSize)*floatSizeBytes;

        glVertexAttribPointer(0,positionsSize,GL_FLOAT,false,vertexSizeBytes,0);
        glEnableVertexAttribArray(0);


        glVertexAttribPointer(1,colorSize,GL_FLOAT,false,vertexSizeBytes,positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);

    }
    @Override
    public void update(float dt) {
        shader.useProgram();
        shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shader.uploadMat4f("uView", camera.getViewMatrix());
        //bind the vao that we are using
        glBindVertexArray(vaoID);

        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT,0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        shader.detach();

        if (!changingScene && KeyboardHandler.isKeyPressed(KeyEvent.VK_SPACE)) { // Key to change scene
            changingScene = true;
            System.out.println("Changing scene");
        }

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            // Do stuff

        } else if (changingScene) {
            GameEngineManager.changeScene("GameScene"); // Problem making it static
        }


    }
}
