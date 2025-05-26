package scenes;


import components.SpriteRenderer;
import components.SpriteSheet;
import imGui.ImGuiLayer;
import input.MouseHandler;
import org.example.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.PhysicsSystem;
import physics.primitives.AABBCollider;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import physics.rigidbody.Rigidbody2D;
import util.AssetPool;
import util.DebugDraw;

import java.util.List;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private SpriteSheet sprites;
    private GameObject draggedObject = null;
    private ImGuiLayer imGuiLayer;

    private PhysicsSystem physicsSystem;

    private boolean physicsEnabled = false;

    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }
    @Override
    public void init(List<GameObject> gameObjects) {
        loadResources();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
        this.gameObjects = gameObjects;
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        AssetPool.addSpritesheet("assets/spritesheets/Blue_Slime/Attack_1.png",
                new SpriteSheet(AssetPool.getTexture("assets/spritesheets/Blue_Slime/Attack_1.png"), 80, 34, 4, 46, 94, 27));
    }
    @Override
    public void update(float dt) {
        if (!physicsEnabled) {
//            for (GameObject go : this.gameObjects) {
//                Transform transform = go.getTransform();
//                Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
//                if (rb != null) {
//                    rb.setPosition(transform.getPosition());
//                    physicsSystem.addRigidbody(rb);
//                }
//            }
            physicsEnabled = true;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
            Transform transform = go.getTransform();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            Collider collider = go.getComponent(Collider.class);
            if (collider != null) {
                if (rb != null) {
                    rb.setPosition(transform.getPosition());
                    collider.setRigidbody(rb);
                } else {
                    System.err.println("Collider without Rigidbody2D: " + go.getName());
                }
                drawCollider(collider);
            }
            if (physicsSystem != null) {
                physicsSystem.update(dt);
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
            case OBBCollider OBBCollider -> {
                Vector2f center = OBBCollider.getRigidbody().getPosition();
                Vector2f dimensions = OBBCollider.getHalfSize().mul(2, new Vector2f());
                float rotation = OBBCollider.getRigidbody().getRotation();
                DebugDraw.addBox(center, dimensions, rotation, new Vector3f(1, 0, 0), 1);
            }
            case AABBCollider AABBCollider -> {
                Vector2f center = AABBCollider.getRigidbody().getPosition();
                Vector2f dimensions = AABBCollider.getHalfSize().mul(2, new Vector2f());
                DebugDraw.addBox(center, dimensions, 0, new Vector3f(1, 0, 0), 1); // No rotation
            }
            case null, default -> System.err.println("Unknown collider type");
        }
    }
}
