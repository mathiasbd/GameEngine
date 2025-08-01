package scripts;

import components.Component;
import components.SpriteRenderer;
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

    // New spawn area parameters
    public float spawnY = 700.0f;
    public float minSpawnX = 100.0f;
    public float maxSpawnX = 1200.0f;
    private final transient Random random = new Random();
    private transient float timeSinceLastSpawn = 0f;
    private transient float nextSpawnInterval;
    public float minSpawnInterval = 1f;
    public float maxSpawnInterval = 2f;
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

            if (!scene.getGameObjects().contains(go)) {
                iter.remove();
                continue;
            }

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null && Physics2D.isColliding(rb, "Floor")) {
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

        // Pick a random X within range
        float x = minSpawnX + random.nextFloat() * (maxSpawnX - minSpawnX);
        Vector2f spawnPos = new Vector2f(x, spawnY);

        GameObject snowball = createPrefab(spawnPos);
        scene.addGameObjectToScene(snowball);
        fallingObjects.add(snowball);
    }

    private GameObject createPrefab(Vector2f spawnPos) {
        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        GameObject snowball = new GameObject("Snowball", transform, 0, true);
        snowball.setTag("Snowball");

        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            SpriteRenderer spr = new SpriteRenderer(gameObject.getComponent(SpriteRenderer.class));
            snowball.addComponent(spr);
        }

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
