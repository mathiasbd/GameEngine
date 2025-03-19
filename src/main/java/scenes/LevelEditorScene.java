package scenes;


import java.awt.event.KeyEvent;

import components.SpriteRenderer;
import input.KeyboardHandler;
import org.example.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Shader;
import org.lwjgl.BufferUtils;
import rendering.Texture;
import util.Time;

import static org.lwjgl.opengl.GL30.*;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class LevelEditorScene extends Scene {


    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }

    @Override
    public void init() {

        this.camera = new Camera(new Vector2f());

        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset*2);
        float totalHeight = (float)(300 - yOffset*2);
        float boxX = totalWidth/100.0f;
        float boxY = totalHeight/100.0f;

        for(int x = 0; x < 100; x++) {
            for(int y=0; y<100; y++) {
                float xPos = xOffset + (x*boxX);
                float yPos = yOffset + (y*boxY);
                GameObject go = new GameObject("Box" + x + "," + y, new Transform(new Vector2f(xPos,yPos), new Vector2f(boxX,boxY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos/totalWidth, yPos/totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }

    }
    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
