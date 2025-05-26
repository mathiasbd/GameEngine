package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AlignedBox;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.Square;
import util.DTUMath;

import java.util.List;

public class Collisions {
    public static CollisionManifold findCollisionFeatures(Collider c1, Collider c2) {
        if (c1 instanceof Circle && c2 instanceof Circle) {
            return findCollisionFeatures((Circle)c1, (Circle)c2);
        } if (c1 instanceof Square && c2 instanceof  Square) {
            return findCollisionFeatures((Square)c1, (Square)c2);
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

    public static CollisionManifold findCollisionFeatures(Square s1, Square s2) {
        CollisionManifold manifold = new CollisionManifold();
        boolean colliding = squareAndSquare(s1, s2);
        if (colliding) {
            System.out.println("Two squares colliding");
        }
        return null;
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

    public static boolean squareAndSquare(Square square1, Square square2) {
        Vector2f[] axesToTest = new Vector2f[] {
                new Vector2f(1, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 1),
        };

        // Rotate square1's axes
        DTUMath.rotate(axesToTest[0], square1.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axesToTest[1], square1.getRigidbody().getRotation(), new Vector2f());

        // Rotate square2's axes
        DTUMath.rotate(axesToTest[2], square2.getRigidbody().getRotation(), new Vector2f());
        DTUMath.rotate(axesToTest[3], square2.getRigidbody().getRotation(), new Vector2f());

        for (Vector2f axis : axesToTest) {
            if (!overlapOnAxis(square1, square2, axis)) {
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
}
