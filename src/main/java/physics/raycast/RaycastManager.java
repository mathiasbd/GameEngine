package physics.raycast;

import org.joml.Vector2f;
import physics.primitives.*;
import util.DTUMath;

/*
 * RaycastManager provides utility methods for point, line, and ray intersection tests
 * against various primitive shapes (Circle, AABB, OBB).
 * Author(s): Gabriel
 */
public class RaycastManager {

    /*
     * @param point - the point to test
     * @param line2D - the line segment to test against
     * @return true if the point lies exactly on the infinite line defined by the segment
     */
    public static boolean isPointOnLine(Vector2f point, Line2D line2D) {
        float dy = line2D.getTo().y - line2D.getFrom().y;
        float dx = line2D.getTo().x - line2D.getFrom().x;
        float m = dy / dx;
        float b = line2D.getFrom().y - (m * line2D.getFrom().x);
        return point.y == m * point.x + b;
    }

    /*
     * @param point - the point to test
     * @param circle - the circle collider
     * @return true if the point lies inside or on the circle
     */
    public static boolean isPointInCircle(Vector2f point, Circle circle) {
        Vector2f center = circle.getCenter();
        Vector2f distance = new Vector2f(point).sub(center);
        return distance.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /*
     * @param point - the point to test
     * @param box - the axis-aligned box collider
     * @return true if the point lies within the AABB
     */
    public static boolean isPointInAABB(Vector2f point, AABBCollider box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        return point.x <= max.x && point.x >= min.x
                && point.y <= max.y && point.y >= min.y;
    }

    /*
     * @param point - the point to test
     * @param box - the oriented box collider
     * @return true if the point lies within the OBB
     */
    public static boolean isPointInOBB(Vector2f point, OBBCollider box) {
        Vector2f local = new Vector2f(point);
        DTUMath.rotate(local, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        return local.x <= max.x && local.x >= min.x
                && local.y <= max.y && local.y >= min.y;
    }

    /*
     * @param point - the point to test
     * @param polygon - array of polygon vertices
     * @return true if the point lies inside the convex polygon using SAT
     */
    public static boolean pointInPolygon(Vector2f point, Vector2f[] polygon) {
        int n = polygon.length;
        for (int i = 0; i < n; i++) {
            Vector2f curr = polygon[i];
            Vector2f next = polygon[(i + 1) % n];
            Vector2f edge = new Vector2f(next).sub(curr);
            Vector2f axis = new Vector2f(-edge.y, edge.x).normalize();
            float projection = axis.dot(point);
            float min = axis.dot(polygon[0]), max = min;
            for (int j = 1; j < n; j++) {
                float p = axis.dot(polygon[j]);
                min = Math.min(min, p);
                max = Math.max(max, p);
            }
            if (projection < min || projection > max) {
                return false;
            }
        }
        return true;
    }

    /*
     * @param line2D - the line segment to test
     * @param circle - the circle collider
     * @return true if the segment intersects the circle
     */
    public static boolean isLineIntersectingCircle(Line2D line2D, Circle circle) {
        if (isPointInCircle(line2D.getFrom(), circle)
                || isPointInCircle(line2D.getTo(), circle)) {
            return true;
        }
        Vector2f ab = new Vector2f(line2D.getTo()).sub(line2D.getFrom());
        Vector2f ac = new Vector2f(circle.getCenter()).sub(line2D.getFrom());
        float t  = ac.dot(ab) / ab.dot(ab);
        if (t < 0.0f || t > 1.0f) {
            return false;
        }
        Vector2f closest = new Vector2f(line2D.getFrom()).add(ab.mul(t));
        return isPointInCircle(closest, circle);
    }

    /*
     * @param line2D - the line segment to test
     * @param box - the axis-aligned box collider
     * @return true if the segment intersects the AABB
     */
    public static boolean isLineIntersectingAABB(Line2D line2D, AABBCollider box) {
        if (isPointInAABB(line2D.getFrom(), box)
                || isPointInAABB(line2D.getTo(),   box)) {
            return true;
        }
        Vector2f[] verts = box.getVertices();
        Vector2f pos = box.getRigidbody().getPosition();
        for (int i = 0; i < verts.length; i++) {
            Vector2f from = new Vector2f(verts[i]).add(pos);
            Vector2f to = new Vector2f(verts[(i + 1) % verts.length]).add(pos);
            Line2D edge = new Line2D(from, to, null, 1);
            if (line2D.intersectsLine(edge)) {
                return true;
            }
        }
        return false;
    }

    /*
     * @param line2D - the line segment to test
     * @param box - the oriented box collider
     * @return true if the segment intersects the OBB
     */
    public static boolean isLineIntersectingOBB(Line2D line2D, OBBCollider box) {
        if (isPointInOBB(line2D.getFrom(), box)
                || isPointInOBB(line2D.getTo(),   box)) {
            return true;
        }
        Vector2f[] verts = box.getVertices();
        Vector2f pos = box.getRigidbody().getPosition();
        for (int i = 0; i < verts.length; i++) {
            Vector2f from = new Vector2f(verts[i]).add(pos);
            Vector2f to = new Vector2f(verts[(i + 1) % verts.length]).add(pos);
            Line2D edge = new Line2D(from, to, null, 1);
            if (line2D.intersectsLine(edge)) {
                return true;
            }
        }
        return false;
    }

    /*
     * @param ray - the Raycast to test
     * @param circle - the circle collider
     * @param rayResult - preallocated result object
     * @return the RaycastResult containing hit data or reset if no hit
     */
    public static RaycastResult raycastCircle(Raycast ray, Circle circle, RaycastResult rayResult) {
        Vector2f start = ray.getStart();
        Vector2f dir = ray.getDirection();
        Vector2f toCenter= new Vector2f(start).sub(circle.getCenter());
        float a = dir.dot(dir);
        float b = 2f * dir.dot(toCenter);
        float c = toCenter.dot(toCenter) - circle.getRadius()*circle.getRadius();
        float disc = b*b - 4f*a*c;
        if (disc < 0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }
        float sqrtDisc = (float)Math.sqrt(disc);
        float t1 = (-b - sqrtDisc)/(2f*a);
        float t2 = (-b + sqrtDisc)/(2f*a);
        if (t1 < 0f && t2 < 0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }
        float t = t1 >= 0f ? t1 : t2;
        Vector2f hitPoint = new Vector2f(dir).mul(t).add(start);
        Vector2f normal = new Vector2f(hitPoint).sub(circle.getCenter());
        if (normal.lengthSquared() > 0) normal.normalize();
        rayResult.init(hitPoint, normal, t, true);
        return rayResult;
    }

    /*
     * @param ray - the Raycast to test
     * @param box - the axis-aligned box collider
     * @param rayResult - preallocated result object
     * @return the RaycastResult containing hit data or reset if no hit
     */
    public static RaycastResult raycastAABB(Raycast ray, AABBCollider box, RaycastResult rayResult) {
        Vector2f origin = ray.getStart();
        Vector2f dir = ray.getDirection();
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        float t1, t2, t3, t4;
        if (dir.x == 0f) {
            if (origin.x < min.x || origin.x > max.x) {
                RaycastResult.reset(rayResult);
                return rayResult;
            }
            t1 = Float.NEGATIVE_INFINITY; t2 = Float.POSITIVE_INFINITY;
        } else {
            t1 = (min.x - origin.x)/dir.x;
            t2 = (max.x - origin.x)/dir.x;
        }
        if (dir.y == 0f) {
            if (origin.y < min.y || origin.y > max.y) {
                RaycastResult.reset(rayResult);
                return rayResult;
            }
            t3 = Float.NEGATIVE_INFINITY; t4 = Float.POSITIVE_INFINITY;
        } else {
            t3 = (min.y - origin.y)/dir.y;
            t4 = (max.y - origin.y)/dir.y;
        }
        float tNearX = Math.min(t1, t2), tFarX = Math.max(t1, t2);
        float tNearY = Math.min(t3, t4), tFarY = Math.max(t3, t4);
        float tNear  = Math.max(tNearX, tNearY);
        float tFar   = Math.min(tFarX, tFarY);
        if (tNear > tFar || tFar < 0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }
        float tHit = (tNear >= 0f) ? tNear : tFar;
        if (tHit < 0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }
        Vector2f hitPoint = new Vector2f(dir).mul(tHit).add(origin);
        Vector2f normal = new Vector2f();
        if (tNearX > tNearY) normal.x = dir.x > 0 ? -1 : 1;
        else normal.y = dir.y > 0 ? -1 : 1;
        rayResult.init(hitPoint, normal, tHit, true);
        return rayResult;
    }

    /*
     * @param ray - the Raycast to test
     * @param box - the oriented box collider
     * @param rayResult - preallocated result object
     * @return the RaycastResult containing hit data or reset if no hit
     */
    public static RaycastResult raycastOBB(Raycast ray, OBBCollider box, RaycastResult rayResult) {
        float theta = -box.getRigidbody().getRotation();
        Vector2f center = box.getRigidbody().getPosition();
        Vector2f localStart = new Vector2f(ray.getStart());
        DTUMath.rotate(localStart, theta, center);

        Vector2f localDir = new Vector2f(ray.getDirection());
        DTUMath.rotate(localDir, theta, new Vector2f());

        Raycast localRay = new Raycast(localStart, localDir);
        AABBCollider localBox = new AABBCollider(box.getMin(), box.getMax());
        localBox.setRigidbody(box.getRigidbody());
        RaycastResult localRes = new RaycastResult();
        if (!raycastAABB(localRay, localBox, localRes).isHit()) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        Vector2f worldHit = new Vector2f(localRes.getPoint());
        DTUMath.rotate(worldHit, box.getRigidbody().getRotation(), center);

        Vector2f worldNorm = new Vector2f(localRes.getNormal());
        DTUMath.rotate(worldNorm, box.getRigidbody().getRotation(), new Vector2f());

        rayResult.init(worldHit, worldNorm, localRes.getDistance(), true);
        return rayResult;
    }
}
