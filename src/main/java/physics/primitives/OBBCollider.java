package physics.primitives;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;
import util.DTUMath;

public class OBBCollider extends Collider {
    private transient Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = null;

    public OBBCollider() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public OBBCollider(Vector2f size) {
        this.size = new Vector2f(size);
        this.halfSize = new Vector2f(size).mul(0.5f);
    }

    public OBBCollider(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody.getPosition()).sub(this.halfSize); // assume the position is the center of the box
    }
    public Vector2f getHalfSize() {
        return this.halfSize;
    }

    public Vector2f getSize() {
        return this.size;
    }
    public Vector2f getMax() {
        return new Vector2f(this.rigidbody.getPosition()).add(this.halfSize);
    }

    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

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
            for (Vector2f vertex : vertices) {
                DTUMath.rotate(vertex, rigidbody.getRotation(), center);
            }
        }
        return vertices;
    }
    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
        this.size = halfSize.mul(2);
    }
}
