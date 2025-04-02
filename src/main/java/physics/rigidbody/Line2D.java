package physics.rigidbody;

import org.joml.Vector2f;

public class Line2D {
    private Vector2f from;
    private Vector2f to;
    private Vector2f color;
    private int lifetime;

    public Line2D(Vector2f from, Vector2f to, Vector2f color, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifetime = lifetime;
    }

    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector2f getColor() {
        return color;
    }
}
