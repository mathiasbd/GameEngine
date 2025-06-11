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
import scripts.Spawner;

import java.util.List;

public class LevelScene extends Scene {
    private static List<GameObject> gameObjectsToLoad = null;
    private ImGuiLayer imGuiLayer;
    private PhysicsSystem physicsSystem;


    public LevelScene() {
        System.out.println("Inside the game scene");
    }

    @Override
    public void init(List<GameObject> gameObjects) {
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
        this.physicsSystem.reset();
        this.camera = new Camera(new Vector2f());
        this.imGuiLayer = new ImGuiLayer();
        this.physicsSystem = GameEngineManager.getPhysicsSystem();
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
    public void addGameObject(GameObject go) {
        if (go != null) {
            gameObjects.add(go);
            go.setInScene(true);
            go.start();

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                rb.setPosition(go.getTransform().getPosition());
                physicsSystem.addRigidbody(rb);
                physicsSystem.getForceRegistry().add(rb, physicsSystem.getGravity());
            }
        }
    }
    public void removeGameObject(GameObject go) {
        if (go != null) {
            System.out.println("Removing: " + go.getName());

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                physicsSystem.removeRigidbody(rb);
            }
            gameObjects.remove(go);
        }
    }

    private void drawCollider(Collider collider) {

        switch (collider) {
            case Circle circle -> {
                Vector2f center =circle.getCenter();
                float radius = circle.getRadius();
                Rigidbody2D rb =circle.getRigidbody();
                float rotation = rb.getRotation();

                DebugDraw.addCircle(center,radius, new Vector3f(1, 0, 0), 1);

                Vector2f endpoint = new Vector2f(0,radius);
                DTUMath.rotate(endpoint,rotation,new Vector2f());
                endpoint.add(center);

                DebugDraw.addLine2D(center,endpoint,new Vector3f(1, 0, 0), 1);
            }
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
