package components;

import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.OBBCollider;
import physics.rigidbody.Rigidbody2D;
import scenes.Scene;
import physics.rigidbody.CollisionManifold;

import java.util.List;
import java.util.Random;

public class Spawner {

    private final Scene scene;
    private GameObject fallingObject;

    private final float spawnY = 570.0f;
    private final float groundY = 22.0f;
    private final float minX = 25.0f;
    private final float maxX = 1250.0f;
    private final Random random = new Random();

    private boolean hasSpawned = false;

    public Spawner(Scene scene) {
        this.scene = scene;
        spawnNewObject();
    }


    private void spawnNewObject() {
        if (hasSpawned) return;

        float randomX = minX + random.nextFloat() * (maxX - minX);
        Vector2f spawnPos = new Vector2f(randomX, spawnY);
        Vector2f size = new Vector2f(25, 25);

        Transform transform = new Transform(spawnPos, size);
        fallingObject = new GameObject("Falling Box", transform, 0, true);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.NO_IMPULSE);
        rb.setPosition(new Vector2f(spawnPos));
        fallingObject.addComponent(rb);

        OBBCollider collider = new OBBCollider(size);
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        fallingObject.addComponent(collider);

        transform.setPosition(rb.getPosition());

        // Add to scene
        scene.addGameObject(fallingObject);
        hasSpawned = true;

    }

    public void update(float dt) {
        if (fallingObject != null) {
            Rigidbody2D rb = fallingObject.getComponent(Rigidbody2D.class);
            if (rb != null) {

                List<CollisionManifold> collisions = GameEngineManager.getPhysicsSystem().getCollisions();

                for (CollisionManifold m : collisions) {
                    Rigidbody2D a = m.getA();
                    Rigidbody2D b = m.getB();

                    if (a == rb || b == rb) {
                        scene.removeGameObject(fallingObject);
                        System.out.println("Removing box after collision: " + rb.getPosition());

                        fallingObject = null;
                        hasSpawned = false;
                        break;
                    }
                }
            }
        }

        if (fallingObject == null && !hasSpawned) {

            spawnNewObject();
        }
    }
}