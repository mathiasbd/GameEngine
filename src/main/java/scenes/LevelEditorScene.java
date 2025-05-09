package scenes;


import java.awt.event.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imGui.ImGuiLayer;
import input.KeyboardHandler;
import input.MouseHandler;
import org.example.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Shader;
import org.lwjgl.BufferUtils;
import rendering.Texture;
import serializers.ComponentSerializer;
import serializers.GameObjectSerializer;
import util.AssetPool;
import util.DebugDraw;
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
    private GameObject draggedObject = null;
    private ImGuiLayer imGuiLayer;
    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();

        DebugDraw.addLine2D(new Vector2f(0, 0), new Vector2f(800, 800), new Vector3f(1, 0, 0), 500);
        sprites = AssetPool.getSpriteSheet("assets/spritesheets/Blue_Slime/Attack_1.png");
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

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        if(dragDropper.isDragging()) {
            draggedObject = dragDropper.getDraggedObject();
            if(!MouseHandler.isButtonDown(0) && draggedObject != null) {
                System.out.println("Object is released");
                draggedObject.setTransform(new Transform(new Vector2f(MouseHandler.getOrthoX(this.camera)-50, MouseHandler.getOrthoY(this.camera)-50), new Vector2f(100,100)));
                draggedObject.start();
                if(!draggedObject.isInScene()) {
                    draggedObject.setInScene(true);
                    this.renderer.add(draggedObject);
                    System.out.println("Adding dragged object to renderer");
                } else {
                    draggedObject.getComponent(SpriteRenderer.class).setDirty();
                }
                dragDropper.setDragging(false);
                dragDropper.setDraggedObject(null);
            }
        }
        imGuiLayer.process(this);
        this.renderer.render();
    }
}
