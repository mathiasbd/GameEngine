package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.*;
import util.DTUMath;

public class RaycastManager {

    public static boolean pointOnLine(Vector2f point, Line2D line) { // test if a point is on a line (obviously)
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

    public static boolean pointInABox(Vector2f point, AlignedBox box) { // test if a point is in an axis aligned box
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();
        return point.x <= max.x && min.x
                <= point.x && point.y <= max.y && min.y
                <= point.y; // if the point is within the bounds of the box
    }

    public static boolean pointInBox2D(Vector2f point, AlignedBox box) { // test if a point is in a box (not necessarily axis aligned)
        // translate the point to the box's local space
        Vector2f localPoint = new Vector2f(point);
        DTUMath.rotate(localPoint, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return localPoint.x <= max.x && min.x
                <= localPoint.x && localPoint.y <= max.y && min.y
                <= localPoint.y; // if the point is within the bounds of the box
    }

    public static boolean lineInCircle(Line2D line, Circle circle) { // test if a line is in a circle
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

    public static boolean lineInABox(Line2D line, AlignedBox alignedBox) { // test if a line is in an AlignedBox
        if (pointInBox2D(line.getFrom(), alignedBox) || pointInBox2D(line.getTo(), alignedBox)) {
            return true;
        }

        Vector2f[] corners = alignedBox.getVertices();
        Vector2f aBoxPosition = alignedBox.getRigidbody().getPosition();

        for (int i = 0; i < corners.length; i++) {
            // Get current corner and next corner,
            Vector2f from = new Vector2f(corners[i]).add(aBoxPosition);
            Vector2f to = new Vector2f(corners[(i + 1) % corners.length]).add(aBoxPosition);

            // System.out.println(sideLine);

            // Create a line from the two corners
            Line2D sideLine = new Line2D(from, to, null, 1);

            if (line.intersectsLine(sideLine)) {
                return true;
            }
        }
        return false;
    }

    public static boolean lineInSquare(Line2D line, Square square){ // test if a line is in a square
        float theta = -square.getRigidbody().getRotation();
        Vector2f center = square.getRigidbody().getPosition();
        Vector2f localStart = new Vector2f(line.getFrom());
        Vector2f localEnd = new Vector2f(line.getTo());
        DTUMath.rotate(localStart, theta, center);
        DTUMath.rotate(localEnd, theta, center);

        Line2D localLine = new Line2D(localStart, localEnd,null, 1);
        AlignedBox alignedBox = new AlignedBox(square.getMin(), square.getMax());
        alignedBox.setRigidbody(square.getRigidbody());

        return lineInABox(localLine, alignedBox);
    }

    public static boolean raycastCircle(Raycast ray, Circle circle, RaycastResult rayResult) {
        Vector2f rayStart = ray.getStart();
        Vector2f rayDirection = ray.getDirection();
        Vector2f circleCenter = circle.getCenter();
        float circleRadius = circle.getRadius();

        Vector2f centerToRay = new Vector2f(rayStart).sub(circleCenter);
        System.out.println("centerToRay: " + centerToRay);

        float a = rayDirection.dot(rayDirection);
        float b = 2.0f * rayDirection.dot(centerToRay);
        float c = centerToRay.dot(centerToRay) - circleRadius * circleRadius;
        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("c: " + c);

        float discriminant = b * b - 4.0f * a * c;
        System.out.println("discriminant: " + discriminant);

        if (discriminant < 0.0f) {
            RaycastResult.reset(rayResult);
            return false;
        }

        // t values tell us how far along the ray we hit the circle (length of the ray at the hit point)
        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
        float t2 = (-b + sqrtDiscriminant) / (2.0f * a);
        System.out.println("t1: " + t1);
        System.out.println("t2: " + t2);

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

    public static boolean raycastABox(Raycast ray, AlignedBox box, RaycastResult rayResult) {
        return false;
    }

    public static boolean raycastSquare(Raycast ray, Square square, RaycastResult rayResult) {
        return false;
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
    public static boolean circleAndAlignedBox (Circle circle, AlignedBox aBox){

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
    public static boolean circleAndSquare (Circle circle, Square square){
        // Treat the square like an aligned  box in its local space
        Vector2f min = new Vector2f(); // (0, 0) in local square space
        Vector2f max = new Vector2f(square.getHalfSize()).mul(2.0f); // Full size of the box (width, height)

        //Convert the circle's center to the box's local coordinate space
        Vector2f r = new Vector2f(circle.getCenter()).sub(square.getRigidbody().getPosition());// Translate to box origin
        DTUMath.rotate(r, -square.getRigidbody().getRotation(), new Vector2f());
        Vector2f localCircle= new Vector2f(r).add(square.getHalfSize());



        // This gives us the closest point on the ABox to the circle
        float closestX = Math.max(min.x, Math.min(localCircle.x, max.x));
        float closestY = Math.max(min.y, Math.min(localCircle.y, max.y));

        Vector2f closestPointToCircle = new Vector2f(closestX, closestY);

        // Create a vector from the closest point on the box to the circle's center
        Vector2f circleToBox = new Vector2f(localCircle).sub(closestPointToCircle);

        // Check if the distance squared is less than or equal to the circle's radius squared
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }


    public static boolean ABoxAndCircle(AlignedBox aBox, Circle circle){
        return circleAndAlignedBox(circle, aBox);
    }

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
}
