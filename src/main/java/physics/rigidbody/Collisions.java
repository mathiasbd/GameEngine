package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.Circle;
import physics.primitives.Collider;

import java.util.List;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider c1, Collider c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c1, (Circle)c2);
        } else {
            assert false : "Unknown Col '" + c1.getClass() + "' vs '" + c2.getClass() + "'";
        }

        return null;
    }
    public static CollisionManifold findCollisionFeatures(Circle a, Circle b) {
        CollisionManifold manifold = new CollisionManifold();

        float radiusSum = a.getRadius() + b.getRadius();
        Vector2f distance = new Vector2f(b.getCenter()).sub(a.getCenter());

        if (distance.lengthSquared() > radiusSum * radiusSum) {
            return manifold; // No collision
        }

        float penetrationDepth = Math.abs(distance.length() - radiusSum);
        float halfPenetrationDepth = penetrationDepth * 0.5f;
        // This logic is used to get a similar to realistic collision
        // We might change it to be more realistic by using mass to apply accurate force on each object

        Vector2f normal = new Vector2f(distance).normalize();
        float distanceToA = a.getRadius() - halfPenetrationDepth;
        Vector2f contactPointA = new Vector2f(a.getCenter()).add(new Vector2f(normal).mul(distanceToA));

        manifold = new CollisionManifold(normal, penetrationDepth);
        manifold.addContactPoint(contactPointA);
        return manifold;
    }
}
