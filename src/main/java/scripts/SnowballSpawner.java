package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.Physics2D;
import physics.collisions.Rigidbody2D;
import physics.primitives.Circle;
import scenes.Scene;

import java.util.*;

public class SnowballSpawner extends Component {
    private Scene scene;
    private final List<GameObject> fallingObjects = new ArrayList<>();
    public List<String> spawnPoints = List.of("SpawnPoint1", "SpawnPoint2", "SpawnPoint3");

    private final transient Random random = new Random();
    private float timeSinceLastSpawn = 0f;
    private float nextSpawnInterval;
    private float minSpawnInterval = 0.2f;
    private float maxSpawnInterval = 0.8f;
    private int maxPoints = 3;

    @Override
    public void start() {
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in Spawner");
        }
        resetSpawnTimer();
    }

    @Override
    public void update(float dt) {
        Iterator<GameObject> iter = fallingObjects.iterator();
        while (iter.hasNext()) {
            GameObject go = iter.next();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null && Physics2D.isColliding(rb, "Player", "Floor")) {
                scene.removeGameObject(go);
                iter.remove();
            }
        }

        timeSinceLastSpawn += dt;

        if (timeSinceLastSpawn >= nextSpawnInterval && fallingObjects.size() < maxPoints) {
            spawnNewObject();
            resetSpawnTimer();
        }
    }

    private void resetSpawnTimer() {
        timeSinceLastSpawn = 0f;
        nextSpawnInterval = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
    }

    private void spawnNewObject() {
        if (scene == null) return;

        String spawnName = spawnPoints.get(random.nextInt(spawnPoints.size()));
        GameObject spawnPoint = scene.getGameObjectByName(spawnName);
        Vector2f spawnPos = (spawnPoint != null)
                ? spawnPoint.getTransform().getPosition()
                : new Vector2f(0, 0);

        if (spawnPoint == null) {
            System.err.println("Spawn point not found: " + spawnName);
        }

        GameObject snowball = createPrefab(spawnPos);
        scene.addGameObject(snowball);
        fallingObjects.add(snowball);
    }

    private GameObject createPrefab(Vector2f spawnPos) {
        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        GameObject snowball = new GameObject("Falling Box", transform, 0, true);
        snowball.setTag("SnowBall");

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.DYNAMIC);
        rb.setPosition(new Vector2f(spawnPos));
        snowball.addComponent(rb);

        Circle collider = new Circle(size.x / 2);
        collider.setSolid(false);
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        snowball.addComponent(collider);

        transform.setPosition(rb.getPosition());

        return snowball;
    }
}
