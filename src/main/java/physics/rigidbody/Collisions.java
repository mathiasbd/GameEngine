package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AABBCollider;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import util.DTUMath;

import java.util.ArrayList;
import java.util.List;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider c1, Collider c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c1, (Circle)c2);
        } if (c1 instanceof OBBCollider && c2 instanceof OBBCollider) {
            return findCollisionFeatures((OBBCollider)c1, (OBBCollider)c2);
        } if (c1 instanceof OBBCollider && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c2, (OBBCollider)c1);
        } if (c1 instanceof Circle && c2 instanceof OBBCollider) {
            return findCollisionFeatures((Circle)c1, (OBBCollider)c2);
        } else {
            System.err.println("Unsupported collision detection between " + c1.getClass().getSimpleName() + " and " + c2.getClass().getSimpleName());
        }
        return null;
    }

    public static CollisionManifold findCollisionFeatures(Circle a, Circle b) {
        CollisionManifold manifold = new CollisionManifold();
        float radiusSum = a.getRadius() + b.getRadius();
        Vector2f distance = new Vector2f(b.getCenter()).sub(a.getCenter());

        if (distance.lengthSquared() > radiusSum * radiusSum) {
            return manifold;
        }

        float penetrationDepth = Math.abs(distance.length() - radiusSum);
        float halfPenetrationDepth = penetrationDepth * 0.5f;
        Vector2f normal = new Vector2f(distance).normalize();
        float distanceToA = a.getRadius() - halfPenetrationDepth;
        Vector2f contactPointA = new Vector2f(a.getCenter()).add(new Vector2f(normal).mul(distanceToA));

        manifold = new CollisionManifold(normal, penetrationDepth);
        manifold.addContactPoint(contactPointA);
        return manifold;
    }

    public static CollisionManifold findCollisionFeatures(OBBCollider s1, OBBCollider s2) {
        if (s1.getRigidbody().getRotation() == 0.0f && s2.getRigidbody().getRotation() == 0.0f) {
            AABBCollider ab1 = convertToAlignedBox(s1);
            AABBCollider ab2 = convertToAlignedBox(s2);
            return findCollisionFeatures(ab1, ab2);
        }

        Vector2f[] axes = new Vector2f[]{
                new Vector2f(1, 0), new Vector2f(0, 1),
                new Vector2f(1, 0), new Vector2f(0, 1)
        };

        DTUMath.rotate(axes[0], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[1], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[2], s2.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[3], s2.getRigidbody().getRotation(), new Vector2f());

        return runSAT(s1, s2, axes, true);
    }

    private static CollisionManifold findCollisionFeatures(Circle circle, OBBCollider OBBCollider) {
        if (OBBCollider.getRigidbody().getRotation() == 0.0f) {
            AABBCollider ab = convertToAlignedBox(OBBCollider);
            return findCollisionFeatures(circle, ab);
        }
        CollisionManifold result = new CollisionManifold();
        Vector2f squarePos = OBBCollider.getRigidbody().getPosition();
        float squareRot = OBBCollider.getRigidbody().getRotation();
        Vector2f halfSize = OBBCollider.getHalfSize();
        Vector2f localCenter = new Vector2f(circle.getCenter()).sub(squarePos);
        DTUMath.rotate(localCenter, -squareRot, new Vector2f());
        localCenter.add(halfSize);

        Vector2f min = new Vector2f(0, 0);
        Vector2f max = new Vector2f(halfSize).mul(2.0f);
        float closestX = Math.max(min.x, Math.min(localCenter.x, max.x));
        float closestY = Math.max(min.y, Math.min(localCenter.y, max.y));
        Vector2f closestPointLocal = new Vector2f(closestX, closestY);

        Vector2f normalLocal = new Vector2f(localCenter).sub(closestPointLocal);
        float distSquared = normalLocal.lengthSquared();

        if (distSquared > circle.getRadius() * circle.getRadius()) return result;

        float distance = (float) Math.sqrt(distSquared);
        Vector2f normalWorld;
        if (distance == 0) {
            normalWorld = new Vector2f(1, 0);
            closestPointLocal.set(localCenter);
        } else {
            normalLocal.div(distance);
            normalWorld = new Vector2f(normalLocal);
            DTUMath.rotate(normalWorld, squareRot, new Vector2f());
        }

        Vector2f contactPointWorld = new Vector2f(closestPointLocal).sub(halfSize);
        DTUMath.rotate(contactPointWorld, squareRot, new Vector2f());
        contactPointWorld.add(squarePos);

        float penetration = circle.getRadius() - distance;

        result = new CollisionManifold(normalWorld, penetration);
        result.addContactPoint(contactPointWorld);
        return result;
    }

    public static CollisionManifold findCollisionFeatures(AABBCollider box, OBBCollider OBBCollider) {
        CollisionManifold manifold = new CollisionManifold();

        Vector2f[] axesToTest = {
                new Vector2f(1, 0), new Vector2f(0, 1), // AABB axes
                new Vector2f(1, 0), new Vector2f(0, 1)  // Square axes to be rotated
        };

        DTUMath.rotate(axesToTest[2], OBBCollider.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axesToTest[3], OBBCollider.getRigidbody().getRotation(), new Vector2f());

        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = new Vector2f();

        for (Vector2f axis : axesToTest) {
            Vector2f interval1 = getInterval(box, axis);
            Vector2f interval2 = getInterval(OBBCollider, axis);
            if (interval1.y < interval2.x || interval2.y < interval1.x) {
                return manifold; // No collision
            }

            float overlap = Math.min(interval1.y, interval2.y) - Math.max(interval1.x, interval2.x);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis.set(axis);
            }
        }

        Vector2f centerOffset = new Vector2f(OBBCollider.getRigidbody().getPosition()).sub(box.getRigidbody().getPosition());
        if (centerOffset.dot(smallestAxis) < 0) smallestAxis.negate();

        manifold = new CollisionManifold(new Vector2f(smallestAxis), minOverlap);
        List<Vector2f> contacts = getContactPoints(box, OBBCollider, smallestAxis);
        for (Vector2f p : contacts) {
            manifold.addContactPoint(p);
        }
        return manifold;
    }

    private static CollisionManifold findCollisionFeatures(Circle circle, AABBCollider box) {
        CollisionManifold result = new CollisionManifold();
        Vector2f circleCenter = circle.getCenter();
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        float closestX = Math.max(min.x, Math.min(circleCenter.x, max.x));
        float closestY = Math.max(min.y, Math.min(circleCenter.y, max.y));
        Vector2f closestPoint = new Vector2f(closestX, closestY);

        Vector2f toCircle = new Vector2f(circleCenter).sub(closestPoint);
        float distSquared = toCircle.lengthSquared();
        if (distSquared > circle.getRadius() * circle.getRadius()) return result;

        float distance = (float) Math.sqrt(distSquared);
        float penetration = circle.getRadius() - distance;
        Vector2f normal = (distance == 0) ? new Vector2f(1, 0) : toCircle.normalize();

        result = new CollisionManifold(normal, penetration);
        result.addContactPoint(closestPoint);
        return result;
    }

    private static CollisionManifold findCollisionFeatures(AABBCollider a, AABBCollider b) {
        CollisionManifold manifold = new CollisionManifold();
        Vector2f aMin = a.getMin();
        Vector2f aMax = a.getMax();
        Vector2f bMin = b.getMin();
        Vector2f bMax = b.getMax();

        boolean xOverlap = aMax.x > bMin.x && aMin.x < bMax.x;
        boolean yOverlap = aMax.y > bMin.y && aMin.y < bMax.y;

        if (!(xOverlap && yOverlap)) return manifold;

        float overlapX = Math.min(aMax.x, bMax.x) - Math.max(aMin.x, bMin.x);
        float overlapY = Math.min(aMax.y, bMax.y) - Math.max(aMin.y, bMin.y);

        Vector2f centerA = a.getRigidbody().getPosition();
        Vector2f centerB = b.getRigidbody().getPosition();
        Vector2f diff = new Vector2f(centerB).sub(centerA);

        Vector2f normal;
        float penetration;
        if (overlapX < overlapY) {
            normal = diff.x < 0 ? new Vector2f(-1, 0) : new Vector2f(1, 0);
            penetration = overlapX;
        } else {
            normal = diff.y < 0 ? new Vector2f(0, -1) : new Vector2f(0, 1);
            penetration = overlapY;
        }

        manifold = new CollisionManifold(normal, penetration);

        // here we get multiple contacts points in a list
        List<Vector2f> contacts = getContactPoints(a, b, normal);
        for (Vector2f p : contacts) {
            manifold.addContactPoint(p);
        }

        //  debug
        System.out.println("[AABB-AABB] Contacts: " + contacts.size());
        for (Vector2f p : contacts) {
            System.out.println(" -> " + p);
        }

        return manifold;
    }


    // === SAT Collision Detection ===
    private static CollisionManifold runSAT(Collider a, Collider b, Vector2f[] axes, boolean useContactPoints) {
        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = new Vector2f();

        for (Vector2f axis : axes) {
            Vector2f interval1 = getInterval(a, axis);
            Vector2f interval2 = getInterval(b, axis);

            if (interval1.y < interval2.x || interval2.y < interval1.x) {
                return new CollisionManifold();
            }

            float overlap = Math.min(interval1.y, interval2.y) - Math.max(interval1.x, interval2.x);
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis.set(axis);
            }
        }

        Vector2f centerOffset = new Vector2f(b.getRigidbody().getPosition()).sub(a.getRigidbody().getPosition());
        if (centerOffset.dot(smallestAxis) < 0) smallestAxis.negate();

        CollisionManifold manifold = new CollisionManifold(new Vector2f(smallestAxis), minOverlap);
        if (useContactPoints) {
            List<Vector2f> contacts = getContactPoints(a, b, smallestAxis);
            for (Vector2f p : contacts) manifold.addContactPoint(p);
        }
        return manifold;
    }

    // === HELPER METHODS ===

    private static Vector2f getInterval(Collider c, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);
        Vector2f[] vertices = c instanceof OBBCollider ? ((OBBCollider) c).getVertices() : ((AABBCollider) c).getVertices();

        result.x = axis.dot(vertices[0]);
        result.y = result.x;
        for (int i = 1; i < vertices.length; i++) {
            float projection = axis.dot(vertices[i]);
            if (projection < result.x) result.x = projection;
            else if (projection > result.y) result.y = projection;
        }
        return result;
    }

    private static List<Vector2f> getContactPoints(Collider c1, Collider c2, Vector2f normal) {
        List<Vector2f> contacts = new ArrayList<>();
        Vector2f[] verts1 = c1 instanceof OBBCollider ? ((OBBCollider) c1).getVertices() : ((AABBCollider) c1).getVertices();
        Vector2f[] verts2 = c2 instanceof OBBCollider ? ((OBBCollider) c2).getVertices() : ((AABBCollider) c2).getVertices();

        for (Vector2f v : verts1) {
            if (RaycastManager.pointInPolygon(v, verts2)) {
                contacts.add(new Vector2f(v));
            }
        }
        for (Vector2f v : verts2) {
            if (RaycastManager.pointInPolygon(v, verts1)) {
                contacts.add(new Vector2f(v));
            }
        }

        if (contacts.isEmpty()) {
            Vector2f center1 = c1.getRigidbody().getPosition();
            Vector2f center2 = c2.getRigidbody().getPosition();
            contacts.add(new Vector2f(center1).add(center2).mul(0.5f));
        }

        return contacts;
    }


    private static AABBCollider convertToAlignedBox(OBBCollider OBBCollider) {
        Vector2f center = OBBCollider.getRigidbody().getPosition();
        Vector2f halfSize = OBBCollider.getHalfSize();
        Vector2f min = new Vector2f(center).sub(halfSize);
        Vector2f max = new Vector2f(center).add(halfSize);

        AABBCollider AABBCollider = new AABBCollider(min, max);
        AABBCollider.setRigidbody(OBBCollider.getRigidbody()); // preserve transform info
        return AABBCollider;
    }

}
