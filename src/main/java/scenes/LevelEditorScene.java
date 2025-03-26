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

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        SpriteSheet sprites = AssetPool.getSpriteSheet("assets/spritesheets/Attack_1.png");
        //test texture batching for our  images
        GameObject obj1 =new GameObject("object 1",new Transform(new Vector2f(100,100),new Vector2f(256,256)) );
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 =new GameObject("object 2",new Transform(new Vector2f(400,100),new Vector2f(256,256)) );
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(1)));
        this.addGameObjectToScene(obj2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        AssetPool.addSpritesheet("assets/spritesheets/Attack_1.png",
                new SpriteSheet(AssetPool.getTexture("assets/spritesheets/Attack_1.png"), 100, 128, 4, 160));
    } // the spritesheet dimensions arent correct so its not working perfectly.

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
