package scenes;

import scripts.PlayerController;
import imGui.ImGuiLayer;
import org.example.Camera;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.PhysicsSystem;
import physics.primitives.AABBCollider;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import physics.collisions.Rigidbody2D;
import util.DTUMath;
import util.DebugDraw;

import java.util.ArrayList;
import java.util.List;

/*
 * LevelScene manages the game scene including physics, updates, and rendering
 * of GameObjects.
 * Author(s): Gabriel, Ilias, Mathias, Ahmed
 */
public class LevelScene extends Scene {
    private static List<GameObject> gameObjectsToLoad = null;
    private ImGuiLayer imGuiLayer;
    private PhysicsSystem physicsSystem;

    /*
     * Constructs the LevelScene and outputs initialization log.
     */
    public LevelScene() {
        System.out.println("Inside the game scene");
    }

    /*
     * Initializes the scene by resetting physics, setting up camera and ImGui layer,
     * and starting GameObjects.
     * @param gameObjects - list of GameObjects to initialize
     */
    @Override
    public void init(List<GameObject> gameObjects) {
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
        this.physicsSystem.reset();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        this.gameObjects = gameObjects;
        for (GameObject go : this.gameObjects) {
            go.start();
            Transform transform = go.getTransform();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                rb.setPosition(transform.getPosition());
                physicsSystem.addRigidbody(rb);
                transform.setRotation(rb.getRotation());
            }
        }
    }

    /*
     * Updates GameObjects, handles physics updates, ImGui processing, and rendering.
     * @param dt - time elapsed since last frame (in seconds)
     */
    @Override
    public void update(float dt) {
        List<GameObject> snapshot = new ArrayList<>(this.gameObjects);
        for (GameObject go : snapshot) {
            go.update(dt);
            Transform transform = go.getTransform();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            Collider collider = go.getComponent(Collider.class);
            if (collider != null) {
                if (rb != null) {
                    transform.setPosition(rb.getPosition());
                    collider.setRigidbody(rb);
                    rb.setCollider(collider);
                    transform.setRotation(rb.getRotation());
                    if (collider instanceof OBBCollider obb) {
                        obb.getRigidbody().setRotation(rb.getRotation());
                    }
                } else {
                    System.err.println("Collider without Rigidbody2D: " + go.getName());
                }
                drawCollider(collider);
            }
        }

        if (physicsSystem != null) {
            physicsSystem.update(dt);
        }

        imGuiLayer.process(this);
        this.renderer.render();
    }

    /*
     * Adds a GameObject to the scene, starts it, and registers its Rigidbody if present.
     * @param go - GameObject to add to the scene
     */
    public void addGameObject(GameObject go) {
        if (go != null) {
            gameObjects.add(go);
            go.setInScene(true);
            go.start();

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                rb.setPosition(go.getTransform().getPosition());
                physicsSystem.addRigidbody(rb);
            }
        }
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
            default -> System.err.println("Unknown collider type");
        }
    }
}
