package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.Circle;
import physics.primitives.OBBCollider;
import physics.collisions.Rigidbody2D;
import scenes.Scene;
import physics.collisions.CollisionManifold;

import javax.swing.*;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Spawner extends Component {
    private Scene scene;
    private GameObject fallingObject;
    public List<String> spawnPoints = List.of("SpawnPoint1", "SpawnPoint2", "SpawnPoint3");
    private transient final Random random = new Random();
    private boolean hasSpawned = false;

    @Override
    public void start() {
        System.out.println("Spawner started");
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in Spawner");
        }
    }

    @Override
    public void update(float dt) {
        if (!hasSpawned) {
            spawnNewObject();
        }

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

    private void spawnNewObject() {
        if (scene == null || hasSpawned) return;

        String spawnName = spawnPoints.get(random.nextInt(spawnPoints.size()));
        GameObject spawnPoint = scene.getGameObjectByName(spawnName);
        Vector2f spawnPos;
        if (spawnPoint != null) {
            spawnPos = spawnPoint.getTransform().getPosition();
        } else {
            spawnPos = new Vector2f(0,0);
            System.err.println("Spawn point not found: " + spawnName);
        }

        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        fallingObject = new GameObject("Falling Box", transform, 0, true);

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.NO_IMPULSE);
        rb.setPosition(new Vector2f(spawnPos));
        fallingObject.addComponent(rb);

        Circle collider = new Circle(size.x/2); // Change with circle later
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        fallingObject.addComponent(collider);

        transform.setPosition(rb.getPosition());

        scene.addGameObject(fallingObject);
        hasSpawned = true;
    }
}