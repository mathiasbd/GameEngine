package physics.collisions;

import org.joml.Vector2f;
import physics.primitives.AABBCollider;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import physics.raycast.RaycastManager;
import util.DTUMath;

import java.util.ArrayList;
import java.util.List;

/*
 * CollisionManager provides methods to detect and compute collision details
 * between various collider types (Circle, OBB, AABB) using SAT and geometric tests.
 * Author(s): Ahmed, Ilias, Mathias, Gabriel
 */
public class CollisionManager {

    /*
     * Dispatches to appropriate collision detection method based on collider types.
     * @param c1 - first collider
     * @param c2 - second collider
     * @return CollisionManifold containing collision normal, penetration, and contacts, or null if unsupported
     */
    public static CollisionManifold findCollisionFeatures(Collider c1, Collider c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c1, (Circle) c2);
        } else if (c1 instanceof OBBCollider && c2 instanceof OBBCollider) {
            return findCollisionFeatures((OBBCollider) c1, (OBBCollider) c2);
        } else if (c1 instanceof OBBCollider && c2 instanceof Circle) {
            return findCollisionFeatures((Circle) c2, (OBBCollider) c1);
        } else if (c1 instanceof Circle && c2 instanceof OBBCollider) {
            return findCollisionFeatures((Circle) c1, (OBBCollider) c2);
        } else {
            System.err.println("Unsupported collision detection between "
                    + c1.getClass().getSimpleName() + " and " + c2.getClass().getSimpleName());
            return null;
        }
    }

    /*
     * Detects and computes collision between two circles.
     * @param a - first circle collider
     * @param b - second circle collider
     * @return CollisionManifold with normal, penetration, and a single contact point
     */
    public static CollisionManifold findCollisionFeatures(Circle a, Circle b) {
        CollisionManifold m = new CollisionManifold();
        float radiusSum = a.getRadius() + b.getRadius();
        Vector2f diff = new Vector2f(b.getCenter()).sub(a.getCenter());
        // no collision if centers too far apart
        if (diff.lengthSquared() > radiusSum * radiusSum) return m;

        float penetration = radiusSum - diff.length();
        Vector2f normal = new Vector2f(diff).normalize();
        float contactDist = a.getRadius() - penetration * 0.5f;
        Vector2f contactPoint = new Vector2f(a.getCenter()).add(new Vector2f(normal).mul(contactDist));

        m = new CollisionManifold(normal, penetration);
        m.addContactPoint(contactPoint);
        return m;
    }

    /*
     * Detects collision between two oriented boxes using SAT, falling back to AABB if unrotated.
     * @param s1 - first OBB collider
     * @param s2 - second OBB collider
     * @return CollisionManifold with smallest penetration axis and contacts
     */
    public static CollisionManifold findCollisionFeatures(OBBCollider s1, OBBCollider s2) {
        // if both unrotated, convert to AABB for simpler check
        if (s1.getRigidbody().getRotation() == 0f && s2.getRigidbody().getRotation() == 0f) {
            return findCollisionFeatures(convertToAlignedBox(s1), convertToAlignedBox(s2));
        }
        // build SAT axes from both boxes' local axes
        Vector2f[] axes = new Vector2f[]{
                new Vector2f(1,0), new Vector2f(0,1),
                new Vector2f(1,0), new Vector2f(0,1)
        };
        DTUMath.rotate(axes[0], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[1], s1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[2], s2.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axes[3], s2.getRigidbody().getRotation(), new Vector2f());
        return runSAT(s1, s2, axes);
    }

    /*
     * Detects collision between a circle and an OBB collider.
     * @param circle - circle collider
     * @param obb - oriented box collider
     * @return CollisionManifold with normal, penetration, and contact
     */
    private static CollisionManifold findCollisionFeatures(Circle circle, OBBCollider obb) {
        // if box unrotated, use AABB method
        if (obb.getRigidbody().getRotation() == 0f) {
            return findCollisionFeatures(circle, convertToAlignedBox(obb));
        }
        // transform circle center into box local space
        Vector2f center = new Vector2f(circle.getCenter()).sub(obb.getRigidbody().getPosition());
        DTUMath.rotate(center, -obb.getRigidbody().getRotation(), new Vector2f());

        Vector2f half = obb.getHalfSize();
        Vector2f min = new Vector2f(half).negate();
        Vector2f max = new Vector2f(half);
        // find closest point on AABB to local center
        float cx = Math.max(min.x, Math.min(center.x, max.x));
        float cy = Math.max(min.y, Math.min(center.y, max.y));
        Vector2f closest = new Vector2f(cx, cy);
        Vector2f diff = new Vector2f(center).sub(closest);
        float dist2 = diff.lengthSquared();
        if (dist2 > circle.getRadius()*circle.getRadius()) return new CollisionManifold();
        float dist = (float)Math.sqrt(dist2);
        Vector2f normalLocal = dist==0 ? new Vector2f(1,0) : new Vector2f(diff).div(dist);
        Vector2f normal = new Vector2f(normalLocal);
        DTUMath.rotate(normal, obb.getRigidbody().getRotation(), new Vector2f());
        float penetration = circle.getRadius() - dist;
        // compute world contact point
        Vector2f contact = new Vector2f(closest).sub(half);
        DTUMath.rotate(contact, obb.getRigidbody().getRotation(), new Vector2f());
        contact.add(obb.getRigidbody().getPosition());

        CollisionManifold m = new CollisionManifold(normal, penetration);
        m.addContactPoint(contact);
        return m;
    }

    /*
     * Detects collision between a circle and an axis-aligned box.
     * @param circle - circle collider
     * @param box - AABB collider
     * @return CollisionManifold with normal, penetration, and contact
     */
    private static CollisionManifold findCollisionFeatures(Circle circle, AABBCollider box) {
        CollisionManifold m = new CollisionManifold();
        Vector2f c = circle.getCenter();
        Vector2f min = box.getMin(), max = box.getMax();
        float cx = Math.max(min.x, Math.min(c.x, max.x));
        float cy = Math.max(min.y, Math.min(c.y, max.y));
        Vector2f closest = new Vector2f(cx, cy);
        Vector2f diff = new Vector2f(c).sub(closest);
        float dist2 = diff.lengthSquared();
        if (dist2 > circle.getRadius()*circle.getRadius()) return m;
        float dist = (float)Math.sqrt(dist2);
        Vector2f normal = dist==0 ? new Vector2f(1,0) : new Vector2f(diff).normalize();
        float penetration = circle.getRadius() - dist;
        m = new CollisionManifold(normal, penetration);
        m.addContactPoint(closest);
        return m;
    }

    /*
     * Detects collision between two AABBs.
     * @param a - first AABB collider
     * @param b - second AABB collider
     * @return CollisionManifold with minimal axis normal and all contact points
     */
    private static CollisionManifold findCollisionFeatures(AABBCollider a, AABBCollider b) {
        CollisionManifold m = new CollisionManifold();
        Vector2f aMin = a.getMin(), aMax = a.getMax();
        Vector2f bMin = b.getMin(), bMax = b.getMax();
        boolean xOverlap = aMax.x > bMin.x && aMin.x < bMax.x;
        boolean yOverlap = aMax.y > bMin.y && aMin.y < bMax.y;
        if (!(xOverlap && yOverlap)) return m;
        float overlapX = Math.min(aMax.x, bMax.x) - Math.max(aMin.x, bMin.x);
        float overlapY = Math.min(aMax.y, bMax.y) - Math.max(aMin.y, bMin.y);
        Vector2f centerDiff = new Vector2f(b.getRigidbody().getPosition()).sub(a.getRigidbody().getPosition());
        Vector2f normal;
        float penetration;
        if (overlapX < overlapY) {
            normal = new Vector2f(centerDiff.x<0?-1:1,0);
            penetration = overlapX;
        } else {
            normal = new Vector2f(0, centerDiff.y<0?-1:1);
            penetration = overlapY;
        }
        m = new CollisionManifold(normal, penetration);
        for (Vector2f p : getContactPoints(a, b, normal)) m.addContactPoint(p);
        return m;
    }

    /*
     * Runs the Separating Axis Theorem test between two oriented shapes.
     * @param a - first collider
     * @param b - second collider
     * @param axes - candidate separating axes
     * @return CollisionManifold with smallest overlap axis and contacts
     */
    private static CollisionManifold runSAT(Collider a, Collider b, Vector2f[] axes) {
        float minOverlap = Float.MAX_VALUE;
        Vector2f smallestAxis = new Vector2f();
        for (Vector2f axis : axes) {
            Vector2f i1 = getInterval(a, axis);
            Vector2f i2 = getInterval(b, axis);
            if (i1.y < i2.x || i2.y < i1.x) return new CollisionManifold();
            float overlap = Math.min(i1.y, i2.y) - Math.max(i1.x, i2.x);
            if (overlap < minOverlap) { minOverlap = overlap; smallestAxis.set(axis); }
        }
        Vector2f offset = new Vector2f(b.getRigidbody().getPosition()).sub(a.getRigidbody().getPosition());
        if (offset.dot(smallestAxis) < 0) smallestAxis.negate();
        CollisionManifold m = new CollisionManifold(new Vector2f(smallestAxis), minOverlap);
        for (Vector2f p : getContactPoints(a, b, smallestAxis)) m.addContactPoint(p);
        return m;
    }

    /*
     * Projects collider vertices onto an axis to get min/max interval.
     * @param c - collider (OBB or AABB)
     * @param axis - axis vector to project onto
     * @return Vector2f(x=min, y=max) projection interval
     */
    private static Vector2f getInterval(Collider c, Vector2f axis) {
        Vector2f[] verts = c instanceof OBBCollider ? ((OBBCollider)c).getVertices() : ((AABBCollider)c).getVertices();
        float min = axis.dot(verts[0]), max = min;
        for (int i = 1; i < verts.length; i++) {
            float proj = axis.dot(verts[i]);
            if (proj < min) min = proj; else if (proj > max) max = proj;
        }
        return new Vector2f(min, max);
    }

    /*
     * Computes contact points by testing polygon vertices against the other polygon.
     * @param c1 - first collider
     * @param c2 - second collider
     * @param normal - collision normal
     * @return list of contact points
     */
    private static List<Vector2f> getContactPoints(Collider c1, Collider c2, Vector2f normal) {
        List<Vector2f> contacts = new ArrayList<>();
        Vector2f[] v1 = c1 instanceof OBBCollider ? ((OBBCollider)c1).getVertices() : ((AABBCollider)c1).getVertices();
        Vector2f[] v2 = c2 instanceof OBBCollider ? ((OBBCollider)c2).getVertices() : ((AABBCollider)c2).getVertices();
        for (Vector2f v : v1) if (RaycastManager.pointInPolygon(v, v2)) contacts.add(new Vector2f(v));
        for (Vector2f v : v2) if (RaycastManager.pointInPolygon(v, v1)) contacts.add(new Vector2f(v));
        if (contacts.isEmpty()) {
            Vector2f c = new Vector2f(c1.getRigidbody().getPosition()).add(c2.getRigidbody().getPosition()).mul(0.5f);
            contacts.add(c);
        }
        return contacts;
    }

    /*
     * Converts an OBB collider to an AABB based on its world position and half-size.
     * @param obb - oriented box collider
     * @return new AABBCollider aligned with world axes
     */
    private static AABBCollider convertToAlignedBox(OBBCollider obb) {
        Vector2f center = obb.getRigidbody().getPosition();
        Vector2f half = obb.getHalfSize();
        Vector2f min = new Vector2f(center).sub(half);
        Vector2f max = new Vector2f(center).add(half);
        AABBCollider box = new AABBCollider(min, max);
        box.setRigidbody(obb.getRigidbody());
        return box;
    }
}