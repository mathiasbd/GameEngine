package physics.raycast;

import org.joml.Vector2f;

/*
 * RaycastResult stores the result of a raycast, including hit point,
 * surface normal, distance, and a flag indicating whether an intersection occurred.
 * Author(s):
 */
public class RaycastResult {
    private Vector2f point;
    private Vector2f normal;
    private float distance;
    private boolean hit;

    /*
     * Constructs an empty RaycastResult with no hit.
     */
    public RaycastResult() {
        this.point = new Vector2f();
        this.normal = new Vector2f();
        this.distance = -1;
        this.hit = false;
    }

    /*
     * Initializes this result with intersection data.
     * @param point - world-space intersection point
     * @param normal - surface normal at the intersection
     * @param distance - distance from the ray origin to the intersection
     * @param hit - whether the ray hit a collider
     */
    public void init(Vector2f point, Vector2f normal, float distance, boolean hit) {
        this.point.set(point);
        this.normal.set(normal);
        this.distance = distance;
        this.hit = hit;
    }

    /*
     * Resets the given RaycastResult to its default no-hit state.
     * @param result - the RaycastResult to reset
     */
    public static void reset(RaycastResult result) {
        if (result != null) {
            result.point.zero();
            result.normal.set(0, 0);
            result.distance = -1;
            result.hit = false;
        }
    }

    /*
     * @return the intersection point of the raycast
     */
    public Vector2f getPoint() {
        return point;
    }

    /*
     * @return the surface normal at the intersection
     */
    public Vector2f getNormal() {
        return normal;
    }

    /*
     * @return the distance from the ray origin to the intersection point
     */
    public float getDistance() {
        return distance;
    }

    /*
     * @return true if the raycast hit a collider
     */
    public boolean isHit() {
        return hit;
    }
}
