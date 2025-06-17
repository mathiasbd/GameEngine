package physics.primitives;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

/*
 * Circle collider represents a circular shape attached to a Rigidbody2D.
 * Author(s):
 */
public class Circle extends Collider {
    private float radius;
    private Rigidbody2D rigidbody = null;

    /*
     * Constructs a Circle with specified radius.
     * @param radius - radius of the circle
     */
    public Circle(float radius) {
        this.radius = radius;
    }

    /*
     * @return the radius of this circle collider
     */
    public float getRadius() {
        return radius;
    }

    /*
     * @return the center position of the circle in world coordinates
     */
    public Vector2f getCenter() {
        return rigidbody.getPosition();
    }

    /*
     * Attaches this collider to a rigidbody.
     * @param rigidbody - the Rigidbody2D to attach
     */
    @Override
    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    /*
     * @return the Rigidbody2D this collider is attached to
     */
    @Override
    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }
}
