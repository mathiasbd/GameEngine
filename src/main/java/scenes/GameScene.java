package scenes;

import imGui.ImGuiLayer;
import org.example.Camera;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.PhysicsSystem;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import physics.collisions.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

/*
 * LevelScene manages the game scene including physics, updates, and rendering
 * of GameObjects.
 * Author(s): Gabriel, Ilias, Mathias, Ahmed
 */
public class GameScene extends Scene {
    private static List<GameObject> gameObjectsToLoad = null;
    private ImGuiLayer imGuiLayer;
    private PhysicsSystem physicsSystem;

    /*
     * Constructs the LevelScene and outputs initialization log.
     */
    public GameScene() {
        System.out.println("Inside the game scene");
    }

    /*
     * Initializes the scene by resetting physics, setting up camera and ImGui layer,
     * and starting GameObjects.
     * @param gameObjects - list of GameObjects to initialize
     */
    @Override
    public void init() {
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
        this.physicsSystem.reset();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        //this.gameObjects = gameObjects;
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
        this.renderer.render();
    }

}
