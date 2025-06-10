package physics.raycast;

import org.joml.Vector2f;
import physics.primitives.*;
import util.DTUMath;

public class RaycastManager {

    // Point methods

    public static boolean isPointOnLine(Vector2f point, Line2D line2D) {
        float dy = line2D.getTo().y - line2D.getFrom().y;
        float dx = line2D.getTo().x - line2D.getFrom().x;
        float m = dy / dx;
        float b = line2D.getFrom().y - (m * line2D.getFrom().x);
        return point.y == m * point.x + b;
    }

    public static boolean isPointInCircle(Vector2f point, Circle circle) {
        Vector2f center = circle.getCenter();
        Vector2f distance = new Vector2f(point).sub(center);
        return distance.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean isPointInAABB(Vector2f point, AABBCollider box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        return point.x <= max.x && min.x <= point.x && point.y <= max.y && min.y <= point.y;
    }

    public static boolean isPointInOBB(Vector2f point, OBBCollider box) {
        Vector2f localPoint = new Vector2f(point);
        DTUMath.rotate(localPoint, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return localPoint.x <= max.x && min.x <= localPoint.x && localPoint.y <= max.y && min.y <= localPoint.y;
    }

    // Line methods

    public static boolean isLineIntersectingCircle(Line2D line2D, Circle circle) {
        if (isPointInCircle(line2D.getFrom(), circle) || isPointInCircle(line2D.getTo(), circle)) {
            return true;
        }

        Vector2f ab = new Vector2f(line2D.getTo()).sub(line2D.getFrom());
        Vector2f center = circle.getCenter();
        Vector2f ac = new Vector2f(center).sub(line2D.getFrom());
        float p = ac.dot(ab) / ab.dot(ab);

        if (p < 0.0f || p > 1.0f) {
            return false;
        }

        Vector2f closestPoint = new Vector2f(line2D.getFrom()).add(ab.mul(p));
        return isPointInCircle(closestPoint, circle);
    }

    public static boolean isLineIntersectingAABB(Line2D line2D, AABBCollider box) {
        if (isPointInAABB(line2D.getFrom(), box) || isPointInAABB(line2D.getTo(), box)) {
            return true;
        }

        Vector2f[] corners = box.getVertices();
        Vector2f position = box.getRigidbody().getPosition();

        for (int i = 0; i < corners.length; i++) {
            Vector2f from = new Vector2f(corners[i]).add(position);
            Vector2f to = new Vector2f(corners[(i + 1) % corners.length]).add(position);
            Line2D sideLine2D = new Line2D(from, to, null, 1);

            if (line2D.intersectsLine(sideLine2D)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLineIntersectingOBB(Line2D line2D, OBBCollider box) {
        if (isPointInOBB(line2D.getFrom(), box) || isPointInOBB(line2D.getTo(), box)) {
            return true;
        }

        Vector2f[] corners = box.getVertices();
        Vector2f position = box.getRigidbody().getPosition();

        for (int i = 0; i < corners.length; i++) {
            Vector2f from = new Vector2f(corners[i]).add(position);
            Vector2f to = new Vector2f(corners[(i + 1) % corners.length]).add(position);
            Line2D edge = new Line2D(from, to, null, 1);

            if (line2D.intersectsLine(edge)) {
                return true;
            }
        }
        return false;
    }

    // Raycast methods

    public static RaycastResult raycastCircle(Raycast ray, Circle circle, RaycastResult rayResult) {
        Vector2f rayStart = ray.getStart();
        Vector2f rayDirection = ray.getDirection();
        Vector2f circleCenter = circle.getCenter();
        float circleRadius = circle.getRadius();

        Vector2f centerToRay = new Vector2f(rayStart).sub(circleCenter);

        float a = rayDirection.dot(rayDirection);
        float b = 2.0f * rayDirection.dot(centerToRay);
        float c = centerToRay.dot(centerToRay) - circleRadius * circleRadius;

        float discriminant = b * b - 4.0f * a * c;

        if (discriminant < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
        float t2 = (-b + sqrtDiscriminant) / (2.0f * a);


        if (t1 < 0.0f && t2 < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        float t = t1 >= 0.0f ? t1 : t2;

        Vector2f intersectionPoint = new Vector2f(rayDirection).mul(t).add(rayStart);
        Vector2f normal = new Vector2f(intersectionPoint).sub(circleCenter);
        if (normal.lengthSquared() > 0) {
            normal.normalize();
        }

        rayResult.init(intersectionPoint, normal, t, true);
        return rayResult;
    }

    public static RaycastResult raycastAABB(Raycast ray, AABBCollider box, RaycastResult rayResult) {
        Vector2f origin = ray.getStart();
        Vector2f dir = ray.getDirection();

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        float t1, t2, t3, t4;

        if (dir.x == 0.0f) {
            if (origin.x < min.x || origin.x > max.x) {
                RaycastResult.reset(rayResult);
                return rayResult;
            }
            t1 = Float.NEGATIVE_INFINITY;
            t2 = Float.POSITIVE_INFINITY;
        } else {
            t1 = (min.x - origin.x) / dir.x;
            t2 = (max.x - origin.x) / dir.x;
        }

        if (dir.y == 0.0f) {
            if (origin.y < min.y || origin.y > max.y) {
                RaycastResult.reset(rayResult);
                return rayResult;
            }
            t3 = Float.NEGATIVE_INFINITY;
            t4 = Float.POSITIVE_INFINITY;
        } else {
            t3 = (min.y - origin.y) / dir.y;
            t4 = (max.y - origin.y) / dir.y;
        }

        float tNearX = Math.min(t1, t2);
        float tFarX  = Math.max(t1, t2);
        float tNearY = Math.min(t3, t4);
        float tFarY  = Math.max(t3, t4);

        float tNear = Math.max(tNearX, tNearY);
        float tFar  = Math.min(tFarX, tFarY);

        if (tNear > tFar || tFar < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        float tHit = (tNear >= 0.0f) ? tNear : tFar;
        if (tHit < 0.0f) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        Vector2f hitPoint = new Vector2f(dir).mul(tHit).add(origin);

        Vector2f normal = new Vector2f();
        if (tNearX > tNearY) {
            normal.x = dir.x > 0 ? -1 : 1;
        } else {
            normal.y = dir.y > 0 ? -1 : 1;
        }

        rayResult.init(hitPoint, normal, tHit, true);
        return rayResult;
    }


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

        RaycastResult localResult = new RaycastResult();

        if (!raycastAABB(localRay, localBox, localResult).isHit()) {
            RaycastResult.reset(rayResult);
            return rayResult;
        }

        Vector2f worldHit = new Vector2f(localResult.getPoint());
        DTUMath.rotate(worldHit, box.getRigidbody().getRotation(), center);

        Vector2f worldNorm = new Vector2f(localResult.getNormal());
        DTUMath.rotate(worldNorm, box.getRigidbody().getRotation(), new Vector2f());

        rayResult.init(worldHit, worldNorm, localResult.getDistance(), true);
        return rayResult;
    }

    public static boolean pointInPolygon(Vector2f point, Vector2f[] polygon) {
        int vertexCount = polygon.length;
        for (int i = 0; i < vertexCount; i++) {
            Vector2f current = polygon[i];
            Vector2f next = polygon[(i + 1) % vertexCount];

            // Get the edge
            Vector2f edge = new Vector2f(next).sub(current);
            // Get the axis perpendicular to the edge (the normal)
            Vector2f axis = new Vector2f(-edge.y, edge.x).normalize();

            // Project the point onto the axis
            float projection = axis.dot(point);
            float min = axis.dot(polygon[0]);
            float max = min;
            for (int j = 1; j < vertexCount; j++) {
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

}
