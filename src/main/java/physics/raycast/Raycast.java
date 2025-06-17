package physics.raycast;

import org.joml.Vector2f;

/*
 * Raycast represents a ray with an origin and normalized direction for intersection tests.
 * Author(s):
 */
public class Raycast {
    private Vector2f start;
    private Vector2f direction;

    /*
     * Constructs a Raycast with a starting point and direction.
     * @param start - origin point of the ray
     * @param direction - direction vector (will be normalized)
     */
    public Raycast(Vector2f start, Vector2f direction) {
        this.start = start;
        this.direction = new Vector2f(direction).normalize(); // ensure normalized direction
    }

    /*
     * @return the origin point of the ray
     */
    public Vector2f getStart() {
        return start;
    }

    /*
     * @return the normalized direction vector of the ray
     */
    public Vector2f getDirection() {
        return direction;
    }
}
