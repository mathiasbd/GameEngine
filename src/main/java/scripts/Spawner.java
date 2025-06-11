package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.Circle;
import physics.collisions.Rigidbody2D;
import scenes.Scene;
import physics.collisions.CollisionManifold;

import java.util.*;

public class Spawner extends Component {
    private Scene scene;
    private final List<GameObject> fallingObjects = new ArrayList<>();
    public List<String> spawnPoints = List.of("SpawnPoint1", "SpawnPoint2", "SpawnPoint3");

    // spawn timing
    private final transient Random random = new Random();
    private float timeSinceLastSpawn = 0f;
    private float nextSpawnInterval;
    private float minSpawnInterval = 0.2f;
    private float maxSpawnInterval =0.8f;
    private int maxPoints = 3;

    @Override
    public void start() {
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in Spawner");
        }
        // schedule first spawn
        resetSpawnTimer();
    }

    @Override
    public void update(float dt) {
        // First, clean up any objects that hit player or floor
        Iterator<GameObject> iter = fallingObjects.iterator();
        while (iter.hasNext()) {
            GameObject go = iter.next();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                List<CollisionManifold> collisions = GameEngineManager.getPhysicsSystem().getGhostCollisions();
                for (CollisionManifold m : collisions) {
                    Rigidbody2D rbA = m.getA();
                    Rigidbody2D rbB = m.getB();
                    if (rbA == rb || rbB == rb) {
                        if (rbA.getTag().equals("Player") || rbA.getTag().equals("Floor")
                                || rbB.getTag().equals("Player") || rbB.getTag().equals("Floor")) {
                            scene.removeGameObject(go);
                            iter.remove();
                            break;
                        }
                    }
                }
            }
        }

        // Advance timer
        timeSinceLastSpawn += dt;

        // If it's time, and we haven't exceeded maxPoints, spawn
        if (timeSinceLastSpawn >= nextSpawnInterval && fallingObjects.size() < maxPoints) {
            spawnNewObject();
            resetSpawnTimer();
        }
    }

    private void resetSpawnTimer() {
        timeSinceLastSpawn = 0f;
        // pick random interval between min and max
        nextSpawnInterval = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
    }

    private void spawnNewObject() {
        if (scene == null) return;

        // choose random spawn point
        String spawnName = spawnPoints.get(random.nextInt(spawnPoints.size()));
        GameObject spawnPoint = scene.getGameObjectByName(spawnName);
        Vector2f spawnPos;
        if (spawnPoint != null) {
            spawnPos = spawnPoint.getTransform().getPosition();
        } else {
            spawnPos = new Vector2f(0,0);
            System.err.println("Spawn point not found: " + spawnName);
        }

        // create the object
        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        GameObject fallingObject = new GameObject("Falling Box", transform, 0, true);
        fallingObject.setTag("SnowBall");

        // add physics
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.DYNAMIC);
        rb.setPosition(new Vector2f(spawnPos));
        fallingObject.addComponent(rb);

        Circle collider = new Circle(size.x/2);
        collider.setSolid(false);
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        fallingObject.addComponent(collider);

        // sync transform
        transform.setPosition(rb.getPosition());

        scene.addGameObject(fallingObject);
        fallingObjects.add(fallingObject);
    }
}
