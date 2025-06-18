package physics.collisions;

import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.List;

/*
 * CollisionManifold stores collision information between two Rigidbody2D objects,
 * including contact normal, penetration depth, and contact points.
 * It also holds references to the involved bodies for resolution.
 * Author(s): Gabriel
 */
public class CollisionManifold {
    private Vector2f normal;
    private List<Vector2f> contactPoints;
    private float penetrationDepth;
    private boolean isColliding;
    private Rigidbody2D bodyA;
    private Rigidbody2D bodyB;

    /*
     * Constructs an empty CollisionManifold with no contacts.
     */
    public CollisionManifold() {
        this.normal = new Vector2f();
        this.penetrationDepth = 0.0f;
        this.contactPoints = new ArrayList<>();
        this.isColliding = false;  // default state
    }

    /*
     * Constructs a CollisionManifold with a specified normal and penetration.
     * @param normal - collision normal vector pointing from body A to B
     * @param penetrationDepth - penetration depth of the collision
     */
    public CollisionManifold(Vector2f normal, float penetrationDepth) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.penetrationDepth = penetrationDepth;
        this.isColliding = true;  // mark as colliding
    }

    /*
     * Adds a contact point to the manifold.
     * @param contactPoint - point of contact in world coordinates
     */
    public void addContactPoint(Vector2f contactPoint) {
        contactPoints.add(contactPoint);  // record contact point
    }

    /*
     * @return the collision normal vector
     */
    public Vector2f getNormal() {
        return normal;
    }

    /*
     * @return list of contact points in the collision
     */
    public List<Vector2f> getContactPoints() {
        return contactPoints;
    }

    /*
     * @return penetration depth of the collision
     */
    public float getPenetrationDepth() {
        return penetrationDepth;
    }

    /*
     * @return true if a collision has occurred
     */
    public boolean isColliding() {
        return isColliding;
    }

    /*
     * Sets the involved rigid bodies after collision detection.
     * @param a - first Rigidbody2D involved
     * @param b - second Rigidbody2D involved
     */
    public void setBodies(Rigidbody2D a, Rigidbody2D b) {
        this.bodyA = a;
        this.bodyB = b;
    }

    /*
     * @return the first Rigidbody2D involved in the collision
     */
    public Rigidbody2D getA() {
        return bodyA;
    }

    /*
     * @return the second Rigidbody2D involved in the collision
     */
    public Rigidbody2D getB() {
        return bodyB;
    }
}