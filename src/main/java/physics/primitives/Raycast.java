package physics.primitives;

import org.joml.Vector2f;

public class Raycast {
    private Vector2f start;
    private Vector2f direction;

    public Raycast(Vector2f start, Vector2f dir) {
        this.start = start;
        this.direction = dir.normalize();
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getDirection() {
        return direction;
    }
}
