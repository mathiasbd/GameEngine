package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import physics.Physics2D;
import physics.collisions.Rigidbody2D;
import scenes.Scene;

import java.util.List;

public class ScoreManager extends Component {

    private int score = 0;
    private Rigidbody2D rb;
    private Scene scene;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb == null) {
            throw new IllegalStateException("PlayerController requires a Rigidbody2D");
        }
        this.scene = GameEngineManager.getCurrentScene();
        if (scene == null) {
            System.err.println("Scene is null in PlayerHealth");
        }
        rb.setBodyType(Rigidbody2D.BodyType.DYNAMIC);
    }

    @Override
    public void update(float dt) {
        if (rb != null) {
            List<GameObject> collidingObjects = Physics2D.getCollidingObjects(rb, false, "Point");
            for (GameObject go : collidingObjects) {
                addScore(1);
                scene.removeGameObject(go);
            }
        }
    }

    public void addScore(int amount) {
        score += amount;
        System.out.println("Score: " + score);
    }
}
