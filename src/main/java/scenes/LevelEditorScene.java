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
import util.AssetPool;
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
        //test texture batching for our  images
        GameObject obj1 =new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(256,256)) );
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/test2.jpg")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 =new GameObject("object 2",new Transform(new Vector2f(400,100),new Vector2f(256,256)) );
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/tom_and_jerry.jpg")));
        this.addGameObjectToScene(obj2);

        GameObject obj3 =new GameObject("object 3",new Transform(new Vector2f(700,100),new Vector2f(256,256)) );
        obj3.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/tom_and_jerry-kopi.jpg")));
        this.addGameObjectToScene(obj3);

    }
    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
