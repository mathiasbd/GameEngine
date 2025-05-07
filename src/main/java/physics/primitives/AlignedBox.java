package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.RaycastManager;
import physics.rigidbody.Rigidbody2D;

public class AlignedBox {
    private Vector2f halfSize;
    private Vector2f size = new Vector2f();
    private Rigidbody2D rigidbody = null;

    public AlignedBox() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public AlignedBox(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min); // Since we know its axis aligned, we can get all the points by just knowing the min and max points
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody.getPosition()).sub(this.halfSize); // assume the position is the center of the box
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody.getPosition()).add(this.halfSize);
    }
    public Vector2f[] getVertices() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), // bottom left
                new Vector2f(max.x, min.y), // bottom right
                new Vector2f(max.x, max.y), // top right
                new Vector2f(min.x, max.y) // top left
        };

        for (Vector2f vertex : vertices) {
            System.out.println(vertex);
        }
        if (rigidbody.getRotation() != 0.0f) {
            for (Vector2f vertex : vertices) {
                // Rotate the vertices
            }
        }
        return vertices;
    }
    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }
    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    public boolean cast(Raycast ray, RaycastResult rayResult) {
        return RaycastManager.raycastABox(ray, this, rayResult);
    }
}
