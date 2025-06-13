package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.collisions.CollisionManifold;
import physics.collisions.Rigidbody2D;
import physics.primitives.OBBCollider;
import scenes.Scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
            if (rb != null) {
                List<CollisionManifold> collisions = GameEngineManager.getPhysicsSystem().getGhostCollisions();
                for (CollisionManifold m : collisions) {
                    Rigidbody2D rbA = m.getA();
                    Rigidbody2D rbB = m.getB();
                    GameObject goA = m.getA().getGameObject();
                    GameObject goB = m.getB().getGameObject();
                    if (rbA == rb || rbB == rb) {
                        if (goA.getTag().equals("Player") || goB.getTag().equals("Player")) {
                            scene.removeGameObject(go);
                            iter.remove();
                            break;
                        }
                    }
                }
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
        Vector2f spawnPos;
        if (spawnPoint != null) {
            spawnPos = spawnPoint.getTransform().getPosition();
        } else {
            spawnPos = new Vector2f(0, 0);
            System.err.println("Spawn point not found: " + spawnName);
        }

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

        scene.addGameObject(pointObject);
        pointObjects.add(pointObject);
    }
}