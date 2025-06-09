package components;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;
import input.KeyboardHandler;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    /** units per second */
    public float walkSpeed = 5000.0f;

    private Rigidbody2D rb;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb == null) {
            throw new IllegalStateException("PlayerController requires a Rigidbody2D");
        }
        System.out.println("PlayerController started with Rigidbody2D");
        // Make sure it’s kinematic so it doesn’t get pushed by collisions
        rb.setBodyType(Rigidbody2D.BodyType.KINEMATIC);
    }

    @Override
    public void update(float dt) {
        if (rb == null) return;

        // Read existing velocity (so vertical is untouched)
        Vector2f vel = rb.getLinearVelocity();

        // Determine horizontal input
        float h = 0f;
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_D) || KeyboardHandler.isKeyPressed(GLFW_KEY_RIGHT)) {
            h += 1f;
            System.out.println("Right key pressed");
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_A) || KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT)) {
            h -= 1f;
            System.out.println("Left key pressed");
        }

        // Apply horizontal speed
        vel.x = h * walkSpeed;
        // Commit back to the rigidbody
        rb.setVelocity(vel);
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rb = rb;
    }

}
