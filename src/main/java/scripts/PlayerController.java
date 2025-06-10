package scripts;

import components.Component;
import org.example.GameEngineManager;
import org.joml.Vector2f;
import physics.primitives.*;
import physics.rigidbody.Rigidbody2D;
import input.KeyboardHandler;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {
    public float walkSpeed = 10.0f;
    float skinWidth = 0.02f;
    Vector2f origin = new Vector2f(0, 0);
    float halfsizeY = 0.0f;
    private float rayLength = 0.1f;
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
            System.out.println("Right key pressed");
        }
        if (KeyboardHandler.isKeyPressed(GLFW_KEY_A) || KeyboardHandler.isKeyPressed(GLFW_KEY_LEFT)) {
            h -= 1f;
            System.out.println("Left key pressed");
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
                position.y - halfsizeY + skinWidth
        );
        Raycast downRay = new Raycast(origin, new Vector2f(0, -1).mul(rayLength));
        List<Rigidbody2D> rbs = GameEngineManager.getPhysicsSystem().getRigidbodies();
        for (Rigidbody2D otherRb : rbs) {
            if (otherRb == rb) continue;
            Collider otherCollider = otherRb.getCollider();
            if (otherCollider != null) {
                if (otherCollider instanceof OBBCollider) {
                    // TODO: Implement OBB raycast
                } else if (otherCollider instanceof Circle) {
                    Circle circle = (Circle) otherCollider;
                    RaycastResult result = raycastCircle(downRay, circle, new RaycastResult());
                    if (result.isHit() && result.getPoint().y >= origin.y) {
                        return true; // We hit the ground
                    }
                } else {
                    throw new IllegalStateException("Unsupported collider type for grounding check");
                }
            }
        }
        return true;
    }

    private boolean isTouchingWall() {
        return false;
    }

    public RaycastResult raycastOBB(Raycast ray, OBBCollider obb, RaycastResult rayResult) {
        RaycastResult.reset(rayResult);
        return rayResult;
    }

    public RaycastResult raycastCircle(Raycast ray, Circle circle, RaycastResult rayResult) {
        Vector2f rayStart = ray.getStart();
        Vector2f rayDirection = ray.getDirection();
        Vector2f circleCenter = circle.getCenter();
        float circleRadius = circle.getRadius();

        Vector2f centerToRay = new Vector2f(rayStart).sub(circleCenter);

        float a = rayDirection.dot(rayDirection);
        float b = 2.0f * rayDirection.dot(centerToRay);
        float c = centerToRay.dot(centerToRay) - circleRadius * circleRadius;

        float discriminant = b * b - 4.0f * a * c;

        if (discriminant < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        // t values tell us how far along the ray we hit the circle (length of the ray at the hit point)
        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
        float t2 = (-b + sqrtDiscriminant) / (2.0f * a);

        if (t1 < 0.0f && t2 < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        float t = t1 >= 0.0f ? t1 : t2;

        Vector2f intersectionPoint = new Vector2f(rayDirection).mul(t).add(rayStart);
        Vector2f normal = new Vector2f(intersectionPoint).sub(circleCenter);
        if (normal.lengthSquared() > 0) {
            normal.normalize();
        }

        rayResult.init(intersectionPoint, normal, t, true);
        return rayResult;
    }

    public void setRigidbody(Rigidbody2D rb) {
        this.rb = rb;
    }



}
