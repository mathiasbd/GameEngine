package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AlignedBox;
import physics.primitives.Box2D;
import physics.primitives.Circle2D;
import util.DTUMath;

public class IntersectionDetecter2D {

    public static boolean pointOnLine(Vector2f point, Line2D line) { // test if a point is on a line (obviously)
        float dy = line.getTo().y - line.getFrom().y;
        float dx = line.getTo().x - line.getFrom().x;
        float m = dy / dx;
        float b = line.getFrom().y - (m * line.getFrom().x);
        return point.y == m * point.x + b;
    }

    public static boolean pointInCircle(Vector2f point, Circle2D circle) { // test if a point is in a circle
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

    public static boolean pointInBox2D(Vector2f point, Box2D box) { // test if a point is in a box (not necessarily axis aligned)
        // translate the point to the box's local space
        Vector2f localPoint = new Vector2f(point);
        DTUMath.rotate(localPoint, box.getRigidbody().getRotation(), box.getRigidbody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return localPoint.x <= max.x && min.x
                <= localPoint.x && localPoint.y <= max.y && min.y
                <= localPoint.y; // if the point is within the bounds of the box
    }
}
