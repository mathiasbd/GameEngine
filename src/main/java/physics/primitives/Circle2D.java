package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class Circle2D {
    private float radius;
    private Rigidbody2D rigidbody = null;

    public Circle2D(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public Vector2f getCenter() {
        return rigidbody.getPosition();
    }
}
