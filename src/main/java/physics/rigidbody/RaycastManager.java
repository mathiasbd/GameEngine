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

    public static boolean cast(Raycast ray, Shape shape, RaycastResult rayResult) {
        return shape.cast(ray, rayResult);
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
}
