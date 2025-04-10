package physics.primitives;

import org.joml.Vector2f;

public class RaycastResult {
    private Vector2f point;
    private Vector2f normal;
    private float distance;
    private boolean hit;

    public RaycastResult() {
        this.point = new Vector2f();
        this.normal = new Vector2f();
        this.distance = -1;
        this.hit = false;
    }

    public void init(Vector2f point, Vector2f normal, float distance, boolean hit) {
        this.point.set(point);
        this.normal.set(normal);
        this.distance = distance;
        this.hit = hit;
    }

    public static void reset(RaycastResult result) {
        if (result != null) {
            result.point.zero();
            result.normal.set(0, 0);
            result.distance = -1;
            result.hit = false;
        }
    }

    public Vector2f getPoint() {
        return point;
    }

    public Vector2f getNormal() {
        return normal;
    }

    public float getDistance() {
        return distance;
    }

    public boolean isHit() {
        return hit;
    }
}
