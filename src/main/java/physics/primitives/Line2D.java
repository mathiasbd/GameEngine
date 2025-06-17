package physics.primitives;

import org.joml.Vector2f;
import org.joml.Vector3f;

/*
 * Line2D represents a line segment with color and lifetime,
 * and provides utilities for intersection testing and lifetime decay.
 * Author(s):
 */
public class Line2D {
    private Vector2f from;
    private Vector2f to;
    private Vector3f color;
    private int lifetime;

    /*
     * Constructs a Line2D with specified endpoints, color, and lifetime.
     * @param from - start point of the line segment
     * @param to - end point of the line segment
     * @param color - RGB color for rendering
     * @param lifetime - number of frames before expiration
     */
    public Line2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifetime = lifetime;
    }

    /*
     * Decrements lifetime by one frame.
     * @return remaining lifetime after decrement
     */
    public int beginFrame() {
        this.lifetime--;
        return this.lifetime;
    }

    /*
     * Tests whether this line intersects another line segment using a parametric algorithm.
     * @param other - another Line2D to test against
     * @return true if the segments intersect, false otherwise
     */
    public boolean intersectsLine(Line2D other) {
        float x1 = from.x, y1 = from.y;
        float x2 = to.x,   y2 = to.y;
        float x3 = other.from.x, y3 = other.from.y;
        float x4 = other.to.x,   y4 = other.to.y;
        float denom = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (denom == 0) {
            return false;  // parallel or coincident
        }
        float t = (((x1 - x3) * (y3 - y4)) - ((y1 - y3) * (x3 - x4))) / denom;
        float u = -((((x1 - x2) * (y1 - y3)) - ((y1 - y2) * (x1 - x3))) / denom);
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }

    /*
     * @return the start point of the line segment
     */
    public Vector2f getFrom() {
        return from;
    }

    /*
     * @return the end point of the line segment
     */
    public Vector2f getTo() {
        return to;
    }

    /*
     * @return the RGB color of the line segment
     */
    public Vector3f getColor() {
        return color;
    }
}