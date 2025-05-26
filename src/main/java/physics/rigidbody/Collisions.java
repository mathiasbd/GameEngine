package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AlignedBox;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.Square;
import util.DTUMath;

import java.util.ArrayList;
import java.util.List;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider c1, Collider c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c1, (Circle)c2);
        } if (c1 instanceof Square && c2 instanceof  Square) {
            return findCollisionFeatures((Square)c1, (Square)c2);
        } if (c1 instanceof Square && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c2, (Square)c1);
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

    public static CollisionManifold findCollisionFeatures(Circle circle, Square square) {
        CollisionManifold result = new CollisionManifold();

        // Step 1: Convert circle center to square local space
        Vector2f squarePos = square.getRigidbody().getPosition();
        float squareRot = square.getRigidbody().getRotation();
        Vector2f halfSize = square.getHalfSize();
        Vector2f localCenter = new Vector2f(circle.getCenter()).sub(squarePos);
        DTUMath.rotate(localCenter, -squareRot, new Vector2f());
        localCenter.add(halfSize); // shift origin to square's local min corner

        // Step 2: Compute closest point on local AABB
        Vector2f min = new Vector2f(0, 0);
        Vector2f max = new Vector2f(halfSize).mul(2.0f);
        float closestX = Math.max(min.x, Math.min(localCenter.x, max.x));
        float closestY = Math.max(min.y, Math.min(localCenter.y, max.y));
        Vector2f closestPointLocal = new Vector2f(closestX, closestY);

        // Step 3: Compute local normal and distance
        Vector2f normalLocal = new Vector2f(localCenter).sub(closestPointLocal);
        float distSquared = normalLocal.lengthSquared();

        if (distSquared > circle.getRadius() * circle.getRadius()) {
            return result; // no collision
        }

        // Step 4: Resolve collision manifold
        float distance = (float) Math.sqrt(distSquared);
        Vector2f normalWorld;
        if (distance == 0) {
            // Circle center is exactly at closest point — use arbitrary normal
            normalWorld = new Vector2f(1, 0);
            closestPointLocal.set(localCenter); // fallback
        } else {
            normalLocal.div(distance); // normalize
            normalWorld = new Vector2f(normalLocal);
            DTUMath.rotate(normalWorld, squareRot, new Vector2f());
        }

        // Step 5: Rotate closest point back to world space
        Vector2f contactPointWorld = new Vector2f(closestPointLocal).sub(halfSize);
        DTUMath.rotate(contactPointWorld, squareRot, new Vector2f());
        contactPointWorld.add(squarePos);

        float penetration = circle.getRadius() - distance;

        result = new CollisionManifold(normalWorld, penetration);
        result.addContactPoint(contactPointWorld);
        return result;
    }


    public static CollisionManifold findCollisionFeatures(Square s1, Square s2) {
        Vector2f[] axes = new Vector2f[4];
        axes[0] = new Vector2f(1, 0);
        axes[1] = new Vector2f(0, 1);
        axes[2] = new Vector2f(1, 0);
        axes[3] = new Vector2f(0, 1);

        DTUMath.rotate(axes[0], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[1], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[2], s2.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[3], s2.getRigidbody().getRotation(), new Vector2f());

        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = new Vector2f();
        for (Vector2f axis : axes) {
            Vector2f i1 = getInterval(s1, axis);
            Vector2f i2 = getInterval(s2, axis);
            if (i1.y < i2.x || i2.y < i1.x) {
                return new CollisionManifold(); // No collision
            }

            float overlap = Math.min(i1.y, i2.y) - Math.max(i1.x, i2.x);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis.set(axis);
            }
        }

        // Ensure normal points from s1 to s2
        Vector2f centerOffset = new Vector2f(s2.getRigidbody().getPosition()).sub(s1.getRigidbody().getPosition());
        if (centerOffset.dot(smallestAxis) < 0) {
            smallestAxis.negate();
        }

        CollisionManifold manifold = new CollisionManifold(new Vector2f(smallestAxis), minOverlap);

        // Get contact points
        List<Vector2f> contactPoints = getContactPoints(s1, s2, manifold.getNormal());
        for (Vector2f p : contactPoints) {
            manifold.addContactPoint(p);
        }

        return manifold;
    }

    // Helpers

    public static boolean ABoxAndABox(AlignedBox aBox1, AlignedBox aBox2){
        Vector2f axesToTest[] = {
                new Vector2f(1, 0), // x-axis
                new Vector2f(0, 1), // y-axis
        };
        for (Vector2f axis : axesToTest) {
            if (!overlapOnAxis(aBox1, aBox2, axis)) {
                return false;
            }
        }
        return true;
    }

    public static boolean ABoxAndSquare(AlignedBox aBox, Square square){
        Vector2f axesToTest[] = {
                new Vector2f(0, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 0),
        };

        // Rotate square's axes
        DTUMath.rotate(axesToTest[2], square.getRigidbody().getRotation(), square.getRigidbody().getPosition());
        DTUMath.rotate(axesToTest[3], square.getRigidbody().getRotation(), square.getRigidbody().getPosition());

        for (Vector2f axis : axesToTest) {
            if (!overlapOnAxis(aBox, square, axis)) {
                return false;
            }
        }
        return true;
    }

    private static boolean overlapOnAxis(AlignedBox aBox1, AlignedBox aBox2, Vector2f axis) {
        Vector2f interval1 = getInterval(aBox1, axis);
        Vector2f interval2 = getInterval(aBox2, axis);

        return interval1.x <= interval2.y && interval2.x <= interval1.y;
    }

    private static boolean overlapOnAxis(AlignedBox aBox, Square square, Vector2f axis) {
        Vector2f interval1 = getInterval(aBox, axis);
        Vector2f interval2 = getInterval(square, axis);

        return interval1.x <= interval2.y && interval2.x <= interval1.y;
    }

    private static boolean overlapOnAxis(Square s1, Square s2, Vector2f axis) {
        Vector2f interval1 = getInterval(s1, axis);
        Vector2f interval2 = getInterval(s2, axis);

        return interval1.x <= interval2.y && interval2.x <= interval1.y;
    }


    private static Vector2f getInterval(AlignedBox box, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y)
        };

        result.x = axis.dot(vertices[0]);
        result.y = result.x;
        for (int i = 1; i < vertices.length; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < result.x) {
                result.x = projection;
            } else if (projection > result.y) {
                result.y = projection;
            }
        }
        return result;
    }

    private static Vector2f getInterval(Square box, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        Vector2f vertices[] = box.getVertices();
        result.x = axis.dot(vertices[0]);
        result.y = result.x;
        for (int i = 1; i < vertices.length; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < result.x) {
                result.x = projection;
            } else if (projection > result.y) {
                result.y = projection;
            }
        }
        return result;
    }

    private static List<Vector2f> getContactPoints(Square s1, Square s2, Vector2f normal) {
        List<Vector2f> contacts = new ArrayList<>();
        Vector2f[] verts1 = s1.getVertices();
        Vector2f[] verts2 = s2.getVertices();

        float maxDist = 0.01f; // Small epsilon

        for (Vector2f v1 : verts1) {
            for (Vector2f v2 : verts2) {
                if (v1.distance(v2) < maxDist) {
                    contacts.add(new Vector2f(v1).lerp(v2, 0.5f));
                }
            }
        }

        if (contacts.isEmpty()) {
            // Fallback: estimate contact by projecting center
            Vector2f mid = new Vector2f(s1.getRigidbody().getPosition())
                    .add(s2.getRigidbody().getPosition()).mul(0.5f);
            contacts.add(mid);
        }

        return contacts;
    }

}
