package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.Physics2D;
import physics.collisions.Rigidbody2D;
import physics.primitives.OBBCollider;
import scenes.Scene;

import java.util.*;

public class PointSpawner extends Component {
    private Scene scene;
    private final List<GameObject> pointObjects = new ArrayList<>();
    public List<String> spawnPoints = List.of("SpawnPoint4", "SpawnPoint5", "SpawnPoint6");
    private final transient Random random = new Random();
    private final int maxPoints = 3;

    @Override
    public void start() {
        System.out.println("PointSpawner started");
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in PointSpawner");
        }
    }

    @Override
    public void update(float dt) {
        Iterator<GameObject> iter = pointObjects.iterator();
        while (iter.hasNext()) {
            GameObject go = iter.next();
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null && Physics2D.isColliding(rb, "Player")) {
                scene.removeGameObject(go);
                iter.remove();
            }
        }

        if (pointObjects.size() < maxPoints) {
            spawnNewObject();
        }
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

        GameObject point = createPrefab(spawnPos);
        scene.addGameObject(point);
        pointObjects.add(point);
    }

    private GameObject createPrefab(Vector2f spawnPos) {
        Vector2f size = new Vector2f(25.0f, 25.0f);
        Transform transform = new Transform(spawnPos, size);
        GameObject pointObject = new GameObject("Point Object", transform, 0, true);
        pointObject.setTag("Point");

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(Rigidbody2D.BodyType.STATIC);
        rb.setPosition(new Vector2f(spawnPos));
        pointObject.addComponent(rb);

        OBBCollider collider = new OBBCollider(size);
        collider.setSolid(false);
        collider.setRigidbody(rb);
        rb.setCollider(collider);
        pointObject.addComponent(collider);

        transform.setPosition(rb.getPosition());

        return pointObject;
    }
}
