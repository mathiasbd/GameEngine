package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class Circle {
    private float radius;
    private Rigidbody2D rigidbody = null;

    public Circle(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public Vector2f getCenter() {
        return rigidbody.getPosition();
    }

    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }
}
