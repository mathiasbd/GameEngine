package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.example.GameObject;
import physics.Physics2D;
import physics.collisions.Rigidbody2D;
import scenes.Scene;

import java.util.List;

public class PlayerHealth extends Component {
    public int maxHealth = 3;
    private int currentHealth = maxHealth;
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
        rb.setBodyType(Rigidbody2D.BodyType.KINEMATIC);
    }

    public void takeDamage(int amount) {
        currentHealth -= amount;
        System.out.println("Player took damage! Health now: " + currentHealth);
        if (currentHealth <= 0) {
            onDeath();
        }
    }

    private void onDeath() {
        System.out.println("Player died.");
    }

    @Override
    public void update(float dt) {
        if (rb != null) {
            List<GameObject> collidingObjects = Physics2D.getCollidingObjects(rb, false, "Snowball");
            for (GameObject go : collidingObjects) {
                takeDamage(1);
                scene.removeGameObject(go);
            }
        }
    }
}
