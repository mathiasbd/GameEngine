package physics.primitives;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;
import util.DTUMath;

/*
 * OBBCollider represents an oriented bounding box attached to a Rigidbody2D.
 * It provides world-space min/max extents, size, half-size, and rotated vertices.
 * Author(s): Ahmed
 */
public class OBBCollider extends Collider {
    private transient Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = null;

    /*
     * Constructs an empty OBB with zero size.
     */
    public OBBCollider() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    /*
     * Constructs an OBB given dimensions.
     * @param size - full width and height of the box
     */
    public OBBCollider(Vector2f size) {
        this.size = new Vector2f(size);
        this.halfSize = new Vector2f(size).mul(0.5f);
    }

    /*
     * Constructs an OBB given world-space min and max corners.
     * @param min - minimum corner of the box
     * @param max - maximum corner of the box
     */
    public OBBCollider(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    /*
     * @return world-space minimum corner assuming center at Rigidbody position
     */

    public Vector2f getMin() {
        return new Vector2f(rigidbody.getPosition()).sub(this.halfSize);
    }

    /*
     * @return half the size of the box along each axis
     */
    public Vector2f getHalfSize() {
        return this.halfSize;
    }

    /*
     * @return full size of the box (width, height)
     */
    public Vector2f getSize() {
        return this.size;
    }

    /*
     * @return world-space maximum corner assuming center at Rigidbody position
     */

    public Vector2f getMax() {
        return new Vector2f(rigidbody.getPosition()).add(this.halfSize);
    }

    /*
     * @return the Rigidbody2D this collider is attached to
     */
    @Override
    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

    /*
     * @return the four vertices of the box, rotated around its center if needed
     */

    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), // bottom-left
                new Vector2f(max.x, min.y), // bottom-right
                new Vector2f(max.x, max.y), // top-right
                new Vector2f(min.x, max.y)  // top-left
        };
        if (rigidbody != null && rigidbody.getRotation() != 0.0f) {
            Vector2f center = rigidbody.getPosition();
            for (Vector2f v : vertices) {
                DTUMath.rotate(v, rigidbody.getRotation(), center);  // rotate around center
            }
        }
        return vertices;
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
     * Updates half-size and recalculates full size accordingly.
     * @param halfSize - new half-size of the box
     */
    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = new Vector2f(halfSize);
        this.size = new Vector2f(halfSize).mul(2f);
    }
}
