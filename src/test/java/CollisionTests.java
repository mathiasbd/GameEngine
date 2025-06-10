import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics.primitives.*;
import physics.rigidbody.RaycastManager;
import physics.primitives.Line;
import physics.rigidbody.Rigidbody2D;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionTests {

    // Line vs circle
    @Test
    public void lineOnCircle() {
        Line line = new Line(new Vector2f(-1f, 0f), new Vector2f(1f, 0f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        assertTrue(RaycastManager.lineInCircle(line, circle));
    }

    @Test
    public void lineOnCircleCircumference() {
        Line line = new Line(new Vector2f(-1.0f, 0f), new Vector2f(-1f, 1f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        assertTrue(RaycastManager.lineInCircle(line, circle));
    }

    @Test
    public void lineNotOnCircle() {
        Line line = new Line(new Vector2f(-1.5f, 0f), new Vector2f(-1f, 1f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        assertFalse(RaycastManager.lineInCircle(line, circle));
    }

    // Line vs AABB

    @Test
    public void lineOnAABB() {
        Line line = new Line(new Vector2f(-1.5f, -0.5f), new Vector2f(1.5f, 0.5f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInABox(line, aabb));
    }

    @Test
    public void lineOnAABBFace() {
        Line line = new Line(new Vector2f(-1f, -1f), new Vector2f(1f, -1f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInABox(line, aabb));
    }

    @Test
    public void lineOnAABBCorner() {
        Line line = new Line(new Vector2f(-2f, 0f), new Vector2f(0f, 2f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInABox(line, aabb));
    }

    @Test
    public void lineNotOnAABB() {
        Line line = new Line(new Vector2f(-1.5f, -1.5f), new Vector2f(2.5f, -1f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertFalse(RaycastManager.lineInABox(line, aabb));
    }

    // Line vs OBB

    @Test
    public void lineOnNonRotatedOBB() {
        Line line = new Line(new Vector2f(-0.5f, 0), new Vector2f(0.5f, 0), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(0f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInSquare(line, obb));
    }

    @Test
    public void lineOnRotatedOBB() {
        Line line = new Line(new Vector2f(-2, 0), new Vector2f(2, 0), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInSquare(line, obb));
    }

    @Test
    public void lineOnRotatedOBBFace() {
        Line line = new Line(new Vector2f(-2, -0.59f), new Vector2f(2, 3.41f), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInSquare(line, obb));
    }

    @Test
    public void lineOnRotatedOBBCorner() {
        Line line = new Line(new Vector2f(-1, -1.41f), new Vector2f(1f, 1.41f), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.lineInSquare(line, obb));
    }

    @Test
    public void lineNotOnOBB() {
        Line line = new Line(new Vector2f(-2, -2), new Vector2f(2, -1), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertFalse(RaycastManager.lineInSquare(line, obb));
    }

    // Raycast vs Circle

    @Test
    public void raycastOnCircle() {
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(-2f, 0f), new Vector2f(1f, 0f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertTrue(hit);
    }


    @Test
    public void raycastOnCircleCircumference() {
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(-1f, -1f), new Vector2f(0f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastNotOnCircle() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(2f, 0f), new Vector2f(0f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertFalse(hit);
    }

    // Raycast vs AABB

    @Test
    public void raycastOnAABB() {
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-2f, 0f), new Vector2f(1f, 0f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastABox(ray, aabb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastOnAABBFace() {
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-1f, -2f), new Vector2f(0f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastABox(ray, aabb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastOnAABBCorner() {
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-2f, -2f), new Vector2f(1f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastABox(ray, aabb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastNotOnAABB() {
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(2f, 0f), new Vector2f(0f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastABox(ray, aabb, rayResult);
        assertFalse(hit);
    }


    //  Raycast vs OBB

    @Test
    public void RaycastOnNonRotatedOBB() {
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(0f);
        obb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-2, 0), new Vector2f(1, 0));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastSquare(ray, obb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void RaycastOnRotatedOBB() {
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-2, 0), new Vector2f(1, 0));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastSquare(ray, obb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void RaycastOnRotatedOBBFace() {
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-2, -0.59f), new Vector2f(0.70710677f, 0.70710677f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastSquare(ray, obb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void RaycastOnRotatedOBBCorner() {
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(-1, -1.41f), new Vector2f(0.5787f, 0.8156f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastSquare(ray, obb, rayResult);
        assertTrue(hit);
    }

    @Test
    public void RaycastNotOnOBB() {
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        Raycast ray = new Raycast(new Vector2f(2, 0), new Vector2f(0, 1));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastSquare(ray, obb, rayResult);
        assertFalse(hit);
    }

//    @Test
//    public void testCircleCompletelyOutsideBox() {
//        Circle circle = new Circle(1f);
//        Rigidbody2D rigidbody = new Rigidbody2D();
//        rigidbody.setPosition(new Vector2f(5f, 5f));
//        circle.setRigidbody(rigidbody);
//
//        AABBCollider box = new AABBCollider(new Vector2f(0f, 0f), new Vector2f(2f, 2f));
//        Rigidbody2D boxBody = new Rigidbody2D();
//        boxBody.setPosition(new Vector2f(0f, 0f));
//        box.setRigidbody(boxBody);
//
//        assertFalse(RaycastManager.circleAndAlignedBox(circle, box));
//    }
//    @Test
//    public void testCircleFullyInsideBox() {
//        Circle circle = new Circle(0.5f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(0f, 0f)); // center in box
//        circle.setRigidbody(circleBody);
//
//        AABBCollider box = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D boxBody = new Rigidbody2D();
//        boxBody.setPosition(new Vector2f(0f, 0f));
//        box.setRigidbody(boxBody);
//
//        assertTrue(RaycastManager.circleAndAlignedBox(circle, box));
//    }
//    @Test
//    public void testCirclePartiallyOverlappingBox() {
//        Circle circle = new Circle(1f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(1.75f, 1f)); // Overlapping aBox
//        circle.setRigidbody(circleBody);
//
//        AABBCollider box = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D boxBody = new Rigidbody2D();
//        boxBody.setPosition(new Vector2f(0f, 0f));
//        box.setRigidbody(boxBody);
//
//        assertTrue(RaycastManager.circleAndAlignedBox(circle, box));
//    }
//    @Test
//    public void testCircleTouchingBoxEdge() {
//        Circle circle = new Circle(1.0f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(2.0f, 0f)); // touching right edge
//        circle.setRigidbody(circleBody);
//
//        AABBCollider box = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D boxBody = new Rigidbody2D();
//        boxBody.setPosition(new Vector2f(0f, 0f));
//        box.setRigidbody(boxBody);
//
//        assertTrue(RaycastManager.circleAndAlignedBox(circle, box));
//    }
//
//    @Test
//    public void testCircleJustOutsideSquare() {
//        Circle circle = new Circle(1.0f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(2.01f, 0f)); // just outside
//        circle.setRigidbody(circleBody);
//
//        OBBCollider OBBCollider = new OBBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D squareBody = new Rigidbody2D();
//        squareBody.setPosition(new Vector2f(0f, 0f));
//        OBBCollider.setRigidbody(squareBody);
//
//        assertFalse(RaycastManager.circleAndSquare(circle, OBBCollider));
//    }
//    @Test
//    public void testCircleMissesRotatedSquare() {
//        Circle circle = new Circle(1.0f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(2.5f, 0f)); // pushed further out
//        circle.setRigidbody(circleBody);
//
//        OBBCollider OBBCollider = new OBBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D squareBody = new Rigidbody2D();
//        squareBody.setPosition(new Vector2f(0f, 0f));
//        squareBody.setRotation((float) Math.toRadians(45));
//        OBBCollider.setRigidbody(squareBody);
//
//        assertFalse(RaycastManager.circleAndSquare(circle, OBBCollider));
//    }
//    @Test
//    public void testCircleTouchingRotatedSquare() {
//        Circle circle = new Circle(1.0f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(1.0f, 1.0f)); // near rotated edge
//        circle.setRigidbody(circleBody);
//
//        OBBCollider OBBCollider = new OBBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D squareBody = new Rigidbody2D();
//        squareBody.setPosition(new Vector2f(0f, 0f));
//        squareBody.setRotation((float) Math.toRadians(45));
//        OBBCollider.setRigidbody(squareBody);
//
//        assertTrue(RaycastManager.circleAndSquare(circle, OBBCollider));
//    }
//
//    @Test
//    public void testCircleFullyInsideSquare() {
//        Circle circle = new Circle(0.5f);
//        Rigidbody2D circleBody = new Rigidbody2D();
//        circleBody.setPosition(new Vector2f(0f, 0f)); // center of square
//        circle.setRigidbody(circleBody);
//
//        OBBCollider OBBCollider = new OBBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        Rigidbody2D squareBody = new Rigidbody2D();
//        squareBody.setPosition(new Vector2f(0f, 0f));
//        OBBCollider.setRigidbody(squareBody);
//
//        assertTrue(RaycastManager.circleAndSquare(circle, OBBCollider));
//    }
}
