package physics.rigidbody;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CollisionManifold {
    private Vector2f normal;
    private List<Vector2f> contactPoints;
    private float penetrationDepth;

    private boolean isColliding;

    public CollisionManifold() {
        this.normal = new Vector2f();
        this.penetrationDepth = 0.0f;
        this.contactPoints = new ArrayList<>();
        isColliding = false;
    }

    public CollisionManifold(Vector2f normal, float penetrationDepth) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.penetrationDepth = penetrationDepth;
        isColliding = true;
    }

    public void addContactPoint(Vector2f contactPoint) {
        contactPoints.add(contactPoint);
    }

    public Vector2f getNormal() {
        return normal;
    }

    public List<Vector2f> getContactPoints() {
        return contactPoints;
    }

    public float getPenetrationDepth() {
        return penetrationDepth;
    }
    public boolean isColliding() {
        return this.isColliding;
    }
}
