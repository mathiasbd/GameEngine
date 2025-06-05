package scenes;

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
import physics.rigidbody.Rigidbody2D;
import util.DebugDraw;

import java.util.List;

public class LevelScene extends Scene {

    private static List<GameObject> gameObjectsToLoad = null;
    private ImGuiLayer imGuiLayer;

    private boolean changingScene = false;
    private float timeToChangeScene = 3.0f;

    private PhysicsSystem physicsSystem;


    public LevelScene() {
        System.out.println("Inside the game scene");
    }

    @Override
    public void init(List<GameObject> gameObjects) {
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
        this.gameObjects = gameObjects;

        for (GameObject go : this.gameObjects) {
            Transform transform = go.getTransform();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                rb.setPosition(transform.getPosition());
                physicsSystem.addRigidbody(rb);
                transform.setRotation(rb.getRotation());
            }
        }
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
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
                        // Sync rotation directly
                        obb.getRigidbody().setRotation(rb.getRotation());
                    }
                } else {
                    System.err.println("Collider without Rigidbody2D: " + go.getName());
                }
                drawCollider(collider);
            }
            if (physicsSystem != null) {
                physicsSystem.update(dt);
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
