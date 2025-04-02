package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class Square {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = null;

    public Square() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Square(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
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

        if (rigidbody.getRotation() != 0.0f) {
            for (Vector2f vertex : vertices) {
                // Rotate the vertices
            }
        }
        return vertices;
    }

    public Vector2f[] getAllSides() {
        Vector2f min = getMin();
        Vector2f max = getMax();
        Vector2f[] vertices = getVertices();
        Vector2f[] sides = {
            new Vector2f(vertices[1]).sub(vertices[0]), // bottom
            new Vector2f(vertices[2]).sub(vertices[1]), // right
            new Vector2f(vertices[2]).sub(vertices[3]), // top
            new Vector2f(vertices[3]).sub(vertices[0]) // left
        };
        return sides;
    }

    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }
}
