package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.joml.Vector2f;
import physics.primitives.*;
import physics.raycast.Raycast;
import physics.raycast.RaycastManager;
import physics.raycast.RaycastResult;
import physics.collisions.Rigidbody2D;
import input.KeyboardHandler;
import util.DebugDraw;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public float walkSpeed = 10.0f;
    private Vector2f origin = new Vector2f(0, 0);
    private float halfsizeY = 0.0f;
    private float rayLength = 500f;
    private Rigidbody2D rb;

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody2D.class);
        if (rb == null) {
            throw new IllegalStateException("PlayerController requires a Rigidbody2D");
        }
        rb.setBodyType(Rigidbody2D.BodyType.KINEMATIC);
    }

    @Override
    public void update(float dt) {
        if (rb == null) return;

        Vector2f vel = rb.getLinearVelocity();

        float h = 0f;
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_D) || KeyboardHandler.isKeyPressed(GLFW_KEY_RIGHT)) {
            h = 1f;
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_A) || KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT)) {
            h -= 1f;
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_SPACE)) {
            if (isGrounded()) {
                vel.y = 80.0f;
            }
        }

        vel.x = h * walkSpeed;
        rb.setVelocity(vel);
    }

    private boolean isGrounded() {
        Vector2f position = rb.getPosition();
        Collider collider = rb.getCollider();
        if (collider instanceof OBBCollider) {
            halfsizeY = ((OBBCollider) collider).getHalfSize().y;
        } else if (collider instanceof Circle) {
            halfsizeY = ((Circle) collider).getRadius();
        } else {
            throw new IllegalStateException("Unsupported collider type for grounding check");
        }
        Vector2f origin = new Vector2f(
                position.x,
                position.y
        );
        Raycast downRay = new Raycast(origin, new Vector2f(0, -1));
        List<Rigidbody2D> rbs = GameEngineManager.getPhysicsSystem().getRigidbodies();
        for (Rigidbody2D otherRb : rbs) {
            if (otherRb == rb) continue;
            Collider otherCollider = otherRb.getCollider();
            if (otherCollider != null) {
                if (otherCollider instanceof OBBCollider) {
                    OBBCollider obb = (OBBCollider) otherCollider;
                    RaycastResult result = RaycastManager.raycastOBB(downRay, obb, new RaycastResult());
                    if (result.getDistance() < rayLength && result.getDistance() > 0.0f) {
                        DebugDraw.addLine2D(origin, result.getPoint());
                        if (result.isHit() && result.getPoint().y >= position.y - halfsizeY) {
                            return true; // We hit the ground
                        }
                    }
                } else if (otherCollider instanceof Circle) {
                    Circle circle = (Circle) otherCollider;
                    RaycastResult result = RaycastManager.raycastCircle(downRay, circle, new RaycastResult());
                    if (result.isHit() && result.getPoint().y >= origin.y) {
                        return true; // We hit the ground
                    }
                } else {
                    throw new IllegalStateException("Unsupported collider type for grounding check");
                }
            }
        }
        return false;
    }

    private boolean isTouchingWall() {
        return false;
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rb = rb;
    }



}
