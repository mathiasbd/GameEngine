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

    public boolean intersectsLine(Line2D other) { // parametric line intersection algorithm
        float x1 = this.from.x;
        float y1 = this.from.y;
        float x2 = this.to.x;
        float y2 = this.to.y;
        float x3 = other.from.x;
        float y3 = other.from.y;
        float x4 = other.to.x;
        float y4 = other.to.y;
        float denominator = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (denominator == 0) { // parallel lines
            return false;
        }
        float t = (((x1 - x3) * (y3 - y4)) - ((y1 - y3) * (x3 - x4))) / denominator;
        float u = -((((x1 - x2) * (y1 - y3)) - ((y1 - y2) * (x1 - x3))) / denominator);
        return t >= 0 && t <= 1 && u >= 0 && u <= 1; // if t and u are between 0 and 1, the lines intersect
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
