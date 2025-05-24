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
import physics.primitives.AlignedBox;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.Square;
import rendering.Shader;
import org.lwjgl.BufferUtils;
import rendering.Texture;
import serializers.ComponentSerializer;
import serializers.GameObjectSerializer;
import util.AssetPool;
import util.DebugDraw;
import util.Time;

import javax.swing.*;

import static org.lwjgl.opengl.GL30.*;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private SpriteSheet sprites;
    private GameObject draggedObject = null;
    private ImGuiLayer imGuiLayer;


    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }
    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        sprites = AssetPool.getSpriteSheet("assets/spritesheets/Blue_Slime/Attack_1.png");
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        AssetPool.addSpritesheet("assets/spritesheets/Blue_Slime/Attack_1.png",
                new SpriteSheet(AssetPool.getTexture("assets/spritesheets/Blue_Slime/Attack_1.png"), 80, 34, 4, 46, 94, 27));
    }
    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
            Collider collider = go.getComponent(Collider.class);
            if (collider != null) {
                drawCollider(collider);
            }
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

    private void drawCollider(Collider collider) {
        switch (collider) {
            case Circle circle -> DebugDraw.addCircle(circle.getCenter(), circle.getRadius(), new Vector3f(1, 0, 0), 1);
            case Square square -> {
                Vector2f center = square.getRigidbody().getPosition();
                Vector2f dimensions = square.getHalfSize().mul(2, new Vector2f());
                float rotation = square.getRigidbody().getRotation();
                DebugDraw.addBox(center, dimensions, rotation, new Vector3f(1, 0, 0), 1);
            }
            case AlignedBox alignedBox -> {
                Vector2f center = alignedBox.getRigidbody().getPosition();
                Vector2f dimensions = alignedBox.getHalfSize().mul(2, new Vector2f());
                DebugDraw.addBox(center, dimensions, 0, new Vector3f(1, 0, 0), 1); // No rotation
            }
            case null, default -> System.err.println("Unknown collider type");
        }
    }
}
