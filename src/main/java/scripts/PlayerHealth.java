package scripts;

import components.Component;
import components.SpriteRenderer;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.joml.Vector4f;
import physics.Physics2D;
import physics.collisions.Rigidbody2D;
import scenes.Scene;

import java.util.List;

public class PlayerHealth extends Component {
    public int maxHealth = 3;
    private int currentHealth = maxHealth;
    private Rigidbody2D rb;
    private Scene scene;
    private float damageTimer = 0f;
    private boolean hurt = false;
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
            List<GameObject> collidingObjects = Physics2D.getCollidingObjects(rb, false, "Snowball");
            for (GameObject go : collidingObjects) {
                takeDamage(1);
                scene.removeGameObject(go);
            }
            if(damageTimer > 0 && hurt) {
                damageTimer-=0.1f;
            } else if(hurt) {
                gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
                hurt = false;
            }
        }
    }

    public void takeDamage(int amount) {
        currentHealth -= amount;
        System.out.println("Player took damage! Health now: " + currentHealth);
        if (currentHealth <= 0) {
            onDeath();
        }
        gameObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,0,0,1));
        damageTimer = 2f;
        hurt = true;
    }

    private void onDeath() {
        System.out.println("Player died.");
        scene.removeGameObject(gameObject);
    }

}
