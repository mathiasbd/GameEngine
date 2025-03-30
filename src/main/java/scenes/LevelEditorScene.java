package scenes;


import java.awt.event.KeyEvent;

import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import input.KeyboardHandler;
import org.example.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Shader;
import org.lwjgl.BufferUtils;
import rendering.Texture;
import util.AssetPool;
import util.Time;

import static org.lwjgl.opengl.GL30.*;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }

    private GameObject obj1;
    private SpriteSheet sprites;
    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpriteSheet("assets/spritesheets/Blue_Slime/Attack_1.png");
        //test texture batching for our  images
        obj1 =new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(320,128)), 3);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject greenCube = new GameObject("green cube", new Transform(new Vector2f(500, 100), new Vector2f(100, 100)), -2);
        greenCube.addComponent(new SpriteRenderer(new Vector4f(0,1,0,0.3f)));
        this.addGameObjectToScene(greenCube);

        GameObject redCube = new GameObject("red cube", new Transform(new Vector2f(550, 100), new Vector2f(100, 100)), 1);
        redCube.addComponent(new SpriteRenderer(new Vector4f(1,0,0,0.3f)));
        this.addGameObjectToScene(redCube);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        AssetPool.addSpritesheet("assets/spritesheets/Blue_Slime/Attack_1.png",
                new SpriteSheet(AssetPool.getTexture("assets/spritesheets/Blue_Slime/Attack_1.png"), 80, 34, 4, 46, 94, 27));
    } // the spritesheet dimensions arent correct so its not working perfectly.

    private float testTime = 0.1f;
    private int index = 0;

    @Override
    public void update(float dt) {

        testTime -= dt;

        if(testTime < 0) {
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(index));
            testTime = 0.1f;
            if(index < 3) {
                index++;
            } else {
                index = 0;
            }
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
