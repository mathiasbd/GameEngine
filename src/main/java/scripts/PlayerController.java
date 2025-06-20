package scripts;

import components.Component;
import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;
import input.KeyboardHandler;
import physics.Physics2D;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public float walkSpeed = 25.0f;
    public float sprintSpeed = 35.0f;
    public float actualSpeed = walkSpeed;

    public float jumpStrength = 200.0f;
    public float rayLength = 150f;
    private Rigidbody2D rb;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb == null) {
            throw new IllegalStateException("PlayerController requires a Rigidbody2D");
        }
        rb.setBodyType(Rigidbody2D.BodyType.DYNAMIC);
        rb.setFixedRotation(true);
    }

    @Override
    public void update(float dt) {
        if (rb == null) return;
        rb.setFixedRotation(true);
        rb.setRotation(0.0f);
        Vector2f vel = rb.getLinearVelocity();

        float h = 0f;
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_D) || KeyboardHandler.isKeyPressed(GLFW_KEY_RIGHT)) {
            if (Physics2D.isTouchingWall(rb, rayLength) && !Physics2D.isGrounded(rb, rayLength)) {
                h = 0f;
            } else {
                h = 1f;
            }
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_A) || KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT)) {
            if (Physics2D.isTouchingWall(rb, rayLength) && !Physics2D.isGrounded(rb, rayLength)){
                h = 0f;
            } else {
                h = -1f;
            }
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            if (Physics2D.isGrounded(rb, rayLength) || Physics2D.isTouchingWall(rb, rayLength)) {
                vel.y = jumpStrength;
            }
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || KeyboardHandler.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            actualSpeed = sprintSpeed;
        } else {
            actualSpeed = walkSpeed;
        }

        vel.x = h * actualSpeed;
        rb.setVelocity(vel);
    }
}
