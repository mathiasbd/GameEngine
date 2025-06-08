package components;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;
import input.KeyboardHandler;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public float walkSpeed = 2.0f;
    public float jumpImpulse = 3.0f;

    public transient boolean isGrounded = false;
    private transient Rigidbody2D rb;

    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        if (this.rb == null) {
            System.err.println("PlayerController requires a Rigidbody2D.");
        } else {
            this.rb.setBodyType(Rigidbody2D.BodyType.DYNAMIC);
        }
    }

    public void update(float dt) {
        if (this.rb == null) return;

        Vector2f velocity = rb.getLinearVelocity();

        if (KeyboardHandler.isKeyPressed(GLFW_KEY_RIGHT) || KeyboardHandler.isKeyPressed(GLFW_KEY_D)) { // Move Right
            velocity.x = walkSpeed;
        } else if (KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT) || KeyboardHandler.isKeyPressed(GLFW_KEY_A)) { // Move Left
            velocity.x = -walkSpeed;
        } else { // Idle
            velocity.x = 0f;
        }

        if (KeyboardHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            jump();
        }
        rb.setVelocity(velocity);
    }

    public void jump() {
        if (isGrounded && rb != null) {
            Vector2f velocity = rb.getLinearVelocity();
            velocity.y = jumpImpulse;
            rb.setVelocity(velocity);
            isGrounded = false;
        }
    }
}