package scripts;

import components.Component;
import components.SpriteRenderer;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;
import physics.primitives.OBBCollider;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointSpawner extends Component {
    private Scene scene;
    private GameObject currentPoint = null;
    private String lastSpawnName = null;
    public List<String> spawnPoints;
    private final transient Random random = new Random();

    @Override
    public void start() {
        spawnPoints = List.of("SpawnPoint1", "SpawnPoint2", "SpawnPoint3", "SpawnPoint4", "SpawnPoint5");
        System.out.println("PointSpawner started");
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in PointSpawner");
        }
    }

    @Override
    public void update(float dt) {
        if (scene == null) return;

        if (currentPoint == null || !scene.getGameObjects().contains(currentPoint)) {
            spawnNewObject();
        }
    }

    private void spawnNewObject() {
        if (scene == null) return;

        List<String> choices = new ArrayList<>(spawnPoints);
        if (lastSpawnName != null) {
            choices.remove(lastSpawnName);
        }

        String spawnName = choices.get(random.nextInt(choices.size()));
        lastSpawnName = spawnName;

        GameObject spawnPoint = scene.getGameObjectByName(spawnName);
        if (spawnPoint == null) {
            throw new IllegalStateException("Spawn point not found: " + spawnName);
        }

        Vector2f spawnPos = spawnPoint.getTransform().getPosition();

        currentPoint = createPrefab(spawnPos);
        scene.addGameObjectToScene(currentPoint);
    }

    private GameObject createPrefab(Vector2f spawnPos) {
        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        GameObject pointObject = new GameObject("Point", transform, 0, true);
        pointObject.setTag("Point");

        if(gameObject.getComponent(SpriteRenderer.class)!=null) {
            SpriteRenderer spr = new SpriteRenderer(gameObject.getComponent(SpriteRenderer.class));
            pointObject.addComponent(spr);
        }

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.STATIC);
        rb.setPosition(new Vector2f(spawnPos));
        pointObject.addComponent(rb);

        OBBCollider collider = new OBBCollider(size);
        collider.setSolid(false);
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        pointObject.addComponent(collider);

        // Ensure transform matches physics body
        transform.setPosition(rb.getPosition());

        return pointObject;
    }
}
