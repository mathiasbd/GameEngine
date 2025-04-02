package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AlignedBox;
import physics.primitives.Square;
import physics.primitives.Circle;
import util.DTUMath;

public class IntersectionDetecter {

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

    public static boolean pointInBox2D(Vector2f point, Square box) { // test if a point is in a box (not necessarily axis aligned)
        // translate the point to the box's local space
        Vector2f localPoint = new Vector2f(point);
        DTUMath.rotate(localPoint, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return localPoint.x <= max.x && min.x
                <= localPoint.x && localPoint.y <= max.y && min.y
                <= localPoint.y; // if the point is within the bounds of the box
    }

    public static boolean lineInCircle(Line2D line, Circle circle) {
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

    public static boolean lineInSquare(Line2D line, Square square) {
        if (pointInBox2D(line.getFrom(), square) || pointInBox2D(line.getTo(), square)) {
            return true;
        }

        Vector2f[] allSides = square.getVertices();
        for (Vector2f side : allSides) {
            Line2D sideLine = new Line2D(square.getRigidbody().getPosition(), new Vector2f(side).add(square.getRigidbody().getPosition()), null, 1);
            // System.out.println(sideLine);
            if (line.intersectsLine(sideLine)) {
                return true;
            }
        }
        return false;
    }
}
