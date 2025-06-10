package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.*;
import util.DTUMath;

public class RaycastManager {

    public static boolean pointOnLine(Vector2f point, Line line) { // test if a point is on a line (obviously)
        float dy = line.getTo().y - line.getFrom().y;
        float dx = line.getTo().x - line.getFrom().x;
        float m = dy / dx;
        float b = line.getFrom().y - (m * line.getFrom().x);
        return point.y == m * point.x + b;
    }

    public static boolean pointInCircle(Vector2f point, Circle circle) { // test if a point is in a circle
        Vector2f center = circle.getCenter();
        Vector2f distance = new Vector2f(point).sub(center);
        return distance.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInABox(Vector2f point, AABBCollider box) { // test if a point is in an axis aligned box
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        return point.x <= max.x && min.x
                <= point.x && point.y <= max.y && min.y
                <= point.y; // if the point is within the bounds of the box
    }

    public static boolean pointInBox2D(Vector2f point, AABBCollider box) { // test if a point is in a box (not necessarily axis aligned)
        // translate the point to the box's local space
        Vector2f localPoint = new Vector2f(point);
        DTUMath.rotate(localPoint, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return localPoint.x <= max.x && min.x
                <= localPoint.x && localPoint.y <= max.y && min.y
                <= localPoint.y;
    }

    public static boolean lineInCircle(Line line, Circle circle) { // test if a line is in a circle
        if (pointInCircle(line.getFrom(), circle) || pointInCircle(line.getTo(), circle)) {
            return true;
        }

        Vector2f ab = new Vector2f(line.getTo()).sub(line.getFrom());
        Vector2f center = circle.getCenter();
        Vector2f ac = new Vector2f(center).sub(line.getFrom());
        float p = ac.dot(ab) / ab.dot(ab);

        if (p < 0.0f || p > 1.0f) {
            return false;
        }

        Vector2f closestPoint = new Vector2f(line.getFrom()).add(ab.mul(p));
        return pointInCircle(closestPoint, circle);
    }

    public static boolean lineInABox(Line line, AABBCollider AABBCollider) { // test if a line is in an AlignedBox
        if (pointInBox2D(line.getFrom(), AABBCollider) || pointInBox2D(line.getTo(), AABBCollider)) {
            return true;
        }

        Vector2f[] corners = AABBCollider.getVertices();
        Vector2f aBoxPosition = AABBCollider.getRigidbody().getPosition();

        for (int i = 0; i < corners.length; i++) {
            // Get current corner and next corner,
            Vector2f from = new Vector2f(corners[i]).add(aBoxPosition);
            Vector2f to = new Vector2f(corners[(i + 1) % corners.length]).add(aBoxPosition);

            // System.out.println(sideLine);

            // Create a line from the two corners
            Line sideLine = new Line(from, to, null, 1);

            if (line.intersectsLine(sideLine)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lineInSquare(Line line, OBBCollider OBBCollider){ // test if a line is in a square
        float theta = -OBBCollider.getRigidbody().getRotation();
        Vector2f center = OBBCollider.getRigidbody().getPosition();
        Vector2f localStart = new Vector2f(line.getFrom());
        Vector2f localEnd = new Vector2f(line.getTo());
        DTUMath.rotate(localStart, theta, center);
        DTUMath.rotate(localEnd, theta, center);

        Line localLine = new Line(localStart, localEnd,null, 1);
        AABBCollider AABBCollider = new AABBCollider(OBBCollider.getMin(), OBBCollider.getMax());
        AABBCollider.setRigidbody(OBBCollider.getRigidbody());

        return lineInABox(localLine, AABBCollider);
    }

    public static boolean raycastCircle(Raycast ray, Circle circle, RaycastResult rayResult) {
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
            return false;
        }

        // t values tell us how far along the ray we hit the circle (length of the ray at the hit point)
        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
        float t2 = (-b + sqrtDiscriminant) / (2.0f * a);


        if (t1 < 0.0f && t2 < 0.0f) {
            RaycastResult.reset(rayResult);
            return false;
        }

        float t = t1 >= 0.0f ? t1 : t2;

        Vector2f intersectionPoint = new Vector2f(rayDirection).mul(t).add(rayStart);
        Vector2f normal = new Vector2f(intersectionPoint).sub(circleCenter);
        if (normal.lengthSquared() > 0) {
            normal.normalize();
        }

        rayResult.init(intersectionPoint, normal, t, true);
        return true;
    }

    public static boolean raycastABox(Raycast ray, AABBCollider box, RaycastResult rayResult) {
        Vector2f origin = ray.getStart();
        Vector2f dir = ray.getDirection();

        Vector2f min = new Vector2f(box.getMin()).add(box.getRigidbody().getPosition());
        Vector2f max = new Vector2f(box.getMax()).add(box.getRigidbody().getPosition());

        float t1 = (min.x - origin.x) / dir.x;
        float t2 = (max.x - origin.x) / dir.x;
        float tNearX = Math.min(t1, t2);
        float tFarX  = Math.max(t1, t2);

        float t3 = (min.y - origin.y) / dir.y;
        float t4 = (max.y - origin.y) / dir.y;
        float tNearY = Math.min(t3, t4);
        float tFarY  = Math.max(t3, t4);

        float tNear = Math.max(tNearX, tNearY);
        float tFar  = Math.min(tFarX,  tFarY);

        if (tNear > tFar || tFar < 0.0f) {
            RaycastResult.reset(rayResult);
            return false;
        }

        float tHit = (tNear >= 0.0f) ? tNear : tFar;
        if (tHit < 0.0f) {
            RaycastResult.reset(rayResult);
            return false;
        }

        Vector2f hitPoint = new Vector2f(dir).mul(tHit).add(origin);

        Vector2f normal = new Vector2f();
        if (tNearX > tNearY) {
            normal.x = dir.x > 0 ? -1 : 1;
        } else {
            normal.y = dir.y > 0 ? -1 : 1;
        }

        rayResult.init(hitPoint, normal, tHit, true);
        return true;
    }

    public static boolean raycastSquare(Raycast ray, OBBCollider box, RaycastResult rayResult) {
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

        if (!raycastABox(localRay, localBox, localResult)) {
            RaycastResult.reset(rayResult);
            return false;
        }

        Vector2f worldHit = new Vector2f(localResult.getPoint());
        DTUMath.rotate(worldHit, box.getRigidbody().getRotation(), center);

        Vector2f worldNorm = new Vector2f(localResult.getNormal());
        DTUMath.rotate(worldNorm, box.getRigidbody().getRotation(), new Vector2f());

        rayResult.init(worldHit, worldNorm, localResult.getDistance(), true);
        return true;
    }

    //Shapes dectecter
    //Circle vs Circle detecter
    //// Checks for collision between a circle and an circle
    public static boolean circleAndcircle (Circle c1, Circle c2){
        Vector2f vecBetweenTheCenters = new Vector2f(c1.getCenter().sub(c2.getCenter()));
        float distance = vecBetweenTheCenters.lengthSquared();
        float radiusSum = c1.getRadius() + c2.getRadius();
        return distance <= radiusSum*radiusSum;
    }


    //AlignedBox vs circle
    // Checks for collision between a circle and an Axis Aligned Box
    public static boolean circleAndAlignedBox (Circle circle, AABBCollider aBox){

        Vector2f circleCenter = circle.getCenter();
        // Get the min and max corners of the AlignedBox
        Vector2f min = aBox.getMin();
        Vector2f max = aBox.getMax();
        // This gives us the closest point on the AlignedBox to the circle
        float closestX = Math.max(min.x, Math.min(circleCenter.x, max.x));
        float closestY = Math.max(min.y, Math.min(circleCenter.y, max.y));

        Vector2f closestPointToCircle = new Vector2f(closestX, closestY);
        // Create a vector from the closest point on the box to the circle's center
        Vector2f circleToBox = new Vector2f(circleCenter).sub(closestPointToCircle);

        // Check if the distance squared is less than or equal to the circle's radius squared
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    //Square vs circle
    // Treat the Square just like an AABB, after we rotate the vector from the center of the box to the center of the circle
    public static boolean circleAndSquare (Circle circle, OBBCollider OBBCollider){
        // Treat the square like an aligned  box in its local space
        Vector2f min = new Vector2f(); // (0, 0) in local square space
        Vector2f max = new Vector2f(OBBCollider.getHalfSize()).mul(2.0f); // Full size of the box (width, height)

        //Convert the circle's center to the box's local coordinate space
        Vector2f r = new Vector2f(circle.getCenter()).sub(OBBCollider.getRigidbody().getPosition());// Translate to box origin
        DTUMath.rotate(r, -OBBCollider.getRigidbody().getRotation(), new Vector2f());
        Vector2f localCircle= new Vector2f(r).add(OBBCollider.getHalfSize());

        // This gives us the closest point on the ABox to the circle
        float closestX = Math.max(min.x, Math.min(localCircle.x, max.x));
        float closestY = Math.max(min.y, Math.min(localCircle.y, max.y));

        Vector2f closestPointToCircle = new Vector2f(closestX, closestY);

        // Create a vector from the closest point on the box to the circle's center
        Vector2f circleToBox = new Vector2f(localCircle).sub(closestPointToCircle);

        // Check if the distance squared is less than or equal to the circle's radius squared
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }


    public static boolean ABoxAndCircle(AABBCollider aBox, Circle circle){
        return circleAndAlignedBox(circle, aBox);
    }

    public static boolean ABoxAndABox(AABBCollider aBox1, AABBCollider aBox2){
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

    public static boolean ABoxAndSquare(AABBCollider aBox, OBBCollider OBBCollider){
        Vector2f axesToTest[] = {
                new Vector2f(0, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 0),
        };
        DTUMath.rotate(axesToTest[2], OBBCollider.getRigidbody().getRotation(), OBBCollider.getRigidbody().getPosition());
        DTUMath.rotate(axesToTest[3], OBBCollider.getRigidbody().getRotation(), OBBCollider.getRigidbody().getPosition());

        for (Vector2f axis : axesToTest) {
            if (!overlapOnAxis(aBox, OBBCollider, axis)) {
                return false;
            }
        }
        return true;
    }

    private static boolean overlapOnAxis(AABBCollider aBox1, AABBCollider aBox2, Vector2f axis) {
        Vector2f interval1 = getInterval(aBox1, axis);
        Vector2f interval2 = getInterval(aBox2, axis);

        return interval1.x <= interval2.y && interval2.x <= interval1.y;
    }

    private static boolean overlapOnAxis(AABBCollider aBox, OBBCollider OBBCollider, Vector2f axis) {
        Vector2f interval1 = getInterval(aBox, axis);
        Vector2f interval2 = getInterval(OBBCollider, axis);

        return interval1.x <= interval2.y && interval2.x <= interval1.y;
    }



    private static Vector2f getInterval(AABBCollider box, Vector2f axis) {
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

    private static Vector2f getInterval(OBBCollider box, Vector2f axis) {
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
                return false; // Point is outside on this axis
            }
        }

        return true; // Inside all axes
    }

}
