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
import physics.collisions.Rigidbody2D;
import util.AssetPool;
import util.DTUMath;
import util.DebugDraw;

import java.util.List;

/*
 * LevelEditorScene provides an interactive scene for placing and editing
 * GameObjects with drag-and-drop and physics collider visualization.
 * Author(s):
 */
public class LevelEditorScene extends Scene {
    private GameObject draggedObject = null;
    private ImGuiLayer imGuiLayer;

    /*
     * Constructs the LevelEditorScene and outputs initialization log.
     */
    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }

    /*
     * Initializes the scene by loading assets, setting up the camera,
     * and configuring the ImGui layer.
     * @param gameObjects - list of GameObjects managed by this scene
     */
    @Override
    public void init(List<GameObject> gameObjects) {
        loadResources();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        this.gameObjects = gameObjects;
    }

    /*
     * Loads shaders and sprite sheets required by the level editor.
     */
    private void loadResources() {
        AssetPool.getShader("assets/shaders/vertex.glsl", "assets/shaders/fragment.glsl");
        AssetPool.addSpritesheet(
                "assets/spritesheets/Blue_Slime/Attack_1.png",
                new SpriteSheet(
                        AssetPool.getTexture("assets/spritesheets/Blue_Slime/Attack_1.png"),
                        80, 34, 4, 46, 94, 27
                )
        );
    }

    /*
     * Updates all GameObjects, handles drag-and-drop placement,
     * renders the ImGui layer, and draws colliders.
     * @param dt - time elapsed since last frame (in seconds)
     */
    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            if (go.isInScene()) {
                go.update(dt);
                Transform transform = go.getTransform();
                Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
                Collider collider = go.getComponent(Collider.class);
                if (collider != null) {
                    if (rb != null) {
                        rb.setPosition(transform.getPosition());
                        collider.setRigidbody(rb);
                        transform.setRotation(rb.getRotation());
                    } else {
                        System.err.println("Collider without Rigidbody2D: " + go.getName());
                    }
                    drawCollider(collider);
                }
            }
        }

        if (dragDropper.isDragging()) {
            draggedObject = dragDropper.getDraggedObject();
            if (!MouseHandler.isButtonDown(0) && draggedObject != null) {
                System.out.println("Object is released");
                draggedObject.setTransform(
                        new Transform(
                                new Vector2f(
                                        MouseHandler.getOrthoX(this.camera) - 50,
                                        MouseHandler.getOrthoY(this.camera) - 50
                                ),
                                new Vector2f(100, 100)
                        )
                );
                draggedObject.start();
                if (!draggedObject.isInScene()) {
                    draggedObject.setInScene(true);
                    this.renderer.add(draggedObject);
                    System.out.println("Adding dragged object to renderer");
                } else {
                    if (draggedObject.getComponent(SpriteRenderer.class) != null) {
                        draggedObject.getComponent(SpriteRenderer.class).setDirty();
                    }
                }
                dragDropper.setDragging(false);
                dragDropper.setDraggedObject(null);
            }
        }
        imGuiLayer.process(this);
        this.renderer.render();
    }

    /*
     * Draws the debug outline for different Collider types.
     * @param collider - Collider instance to visualize
     */
    private void drawCollider(Collider collider) {
        switch (collider) {
            case Circle circle -> {
                Vector2f center = circle.getCenter();
                float radius = circle.getRadius();
                Rigidbody2D rb = circle.getRigidbody();
                float rotation = rb.getRotation();

                DebugDraw.addCircle(center, radius, new Vector3f(1, 0, 0), 1);

                Vector2f endpoint = new Vector2f(0, radius);
                DTUMath.rotate(endpoint, rotation, new Vector2f());
                endpoint.add(center);

                DebugDraw.addLine2D(center, endpoint, new Vector3f(1, 0, 0), 1);
            }
            case OBBCollider obb -> {
                Vector2f center = obb.getRigidbody().getPosition();
                Vector2f dimensions = obb.getHalfSize().mul(2, new Vector2f());
                float rotation = obb.getRigidbody().getRotation();
                DebugDraw.addBox(center, dimensions, rotation, new Vector3f(1, 0, 0), 1);
            }
            case AABBCollider aabb -> {
                Vector2f center = aabb.getRigidbody().getPosition();
                Vector2f dimensions = aabb.getHalfSize().mul(2, new Vector2f());
                DebugDraw.addBox(center, dimensions, 0, new Vector3f(1, 0, 0), 1);
            }
            case null, default -> System.err.println("Unknown collider type");
        }
    }
}
