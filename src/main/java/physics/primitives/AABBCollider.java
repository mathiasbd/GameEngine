package physics.primitives;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

/*
 * AABBCollider represents an axis-aligned bounding box attached to a Rigidbody2D.
 * It provides min/max extents, vertex list, and half-size calculations.
 * Author(s):
 */
public class AABBCollider extends Collider {
    private Vector2f halfSize;
    private Vector2f size = new Vector2f();
    private Rigidbody2D rigidbody = null;

    /*
     * Constructs a default AABB with zero size.
     */
    public AABBCollider() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    /*
     * Constructs an AABB given world-space min and max points.
     * @param min - minimum corner of the box
     * @param max - maximum corner of the box
     */
    public AABBCollider(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);    // compute dimensions
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    /*
     * @return the minimum corner (world-space) assuming center at Rigidbody position
     */
    public Vector2f getMin() {
        return new Vector2f(rigidbody.getPosition()).sub(this.halfSize);
    }

    /*
     * @return the maximum corner (world-space) assuming center at Rigidbody position
     */
    public Vector2f getMax() {
        return new Vector2f(rigidbody.getPosition()).add(this.halfSize);
    }

    /*
     * @return array of the four vertices in CCW order: bottom-left, bottom-right, top-right, top-left
     */
    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        return new Vector2f[] {
                new Vector2f(min.x, min.y),  // bottom-left
                new Vector2f(max.x, min.y),  // bottom-right
                new Vector2f(max.x, max.y),  // top-right
                new Vector2f(min.x, max.y)   // top-left
        };
    }

    /*
     * @return the Rigidbody2D this collider is attached to
     */
    @Override
    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

    /*
     * Associates this collider with the given rigidbody.
     * @param rigidbody - the Rigidbody2D to attach
     */
    @Override
    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    /*
     * @return half the size of the box along each axis
     */
    public Vector2f getHalfSize() {
        return this.halfSize;
    }
}
