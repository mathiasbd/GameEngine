import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics.primitives.*;
import physics.raycast.Raycast;
import physics.raycast.RaycastManager;
import physics.primitives.Line2D;
import physics.raycast.RaycastResult;
import physics.collisions.Rigidbody2D;
import physics.collisions.CollisionManager;

import static org.junit.jupiter.api.Assertions.*;
import static physics.raycast.RaycastManager.pointInPolygon;

public class CollisionTests {

    //point vs polygon
    @Test
    public void pointInsideConvexPolygon() {
        Vector2f[] polygon = new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(2, 0),
                new Vector2f(2, 2),
                new Vector2f(0, 2)
        };

        Vector2f pointInside = new Vector2f(1, 1);
        boolean result = pointInPolygon(pointInside, polygon);
        assertTrue(result);
    }

    @Test
    public void pointOutsideConvexPolygon() {
        Vector2f[] polygon = new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(2, 0),
                new Vector2f(2, 2),
                new Vector2f(0, 2)
        };

        Vector2f pointOutside = new Vector2f(3, 3);
        boolean result = pointInPolygon(pointOutside, polygon);
        assertFalse(result);
    }

    @Test
    public void pointOnEdgeOfPolygon() {
        Vector2f[] polygon = new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(2, 0),
                new Vector2f(2, 2),
                new Vector2f(0, 2)
        };

        Vector2f pointOnEdge = new Vector2f(1, 0);
        boolean result = pointInPolygon(pointOnEdge, polygon);
        assertTrue(result);
    }
    @Test
    public void pointOnCornerPolygon() {
        Vector2f[] polygon = new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(2, 0),
                new Vector2f(2, 2),
                new Vector2f(0, 2)
        };

        Vector2f pointOnCorner = new Vector2f(2, 2);
        boolean result = pointInPolygon(pointOnCorner, polygon);
        assertTrue(result);
    }
    
    // Line vs circle
    @Test
    public void lineOnCircle() {
        Line2D line2D = new Line2D(new Vector2f(-1f, 0f), new Vector2f(1f, 0f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        assertTrue(RaycastManager.isLineIntersectingCircle(line2D, circle));
    }

    @Test
    public void lineOnCircleCircumference() {
        Line2D line2D = new Line2D(new Vector2f(-1.0f, 0f), new Vector2f(-1f, 1f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        assertTrue(RaycastManager.isLineIntersectingCircle(line2D, circle));
    }

    @Test
    public void lineNotOnCircle() {
        Line2D line2D = new Line2D(new Vector2f(-1.5f, 0f), new Vector2f(-1f, 1f), null, 1);
        Circle circle = new Circle(1f);

        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        assertFalse(RaycastManager.isLineIntersectingCircle(line2D, circle));
    }

    // Line vs AABB

    @Test
    public void lineOnAABB() {
        Line2D line2D = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(1.5f, 0.5f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingAABB(line2D, aabb));
    }

    @Test
    public void lineOnAABBFace() {
        Line2D line2D = new Line2D(new Vector2f(-1f, -1f), new Vector2f(1f, -1f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingAABB(line2D, aabb));
    }

    @Test
    public void lineOnAABBCorner() {
        Line2D line2D = new Line2D(new Vector2f(-2f, 0f), new Vector2f(0f, 2f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingAABB(line2D, aabb));
    }

    @Test
    public void lineNotOnAABB() {
        Line2D line2D = new Line2D(new Vector2f(-1.5f, -1.5f), new Vector2f(2.5f, -1f), null, 1);
        AABBCollider aabb = new AABBCollider(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0f, 0f));
        aabb.setRigidbody(boxRb);

        assertFalse(RaycastManager.isLineIntersectingAABB(line2D, aabb));
    }

    // Line vs OBB

    @Test
    public void lineOnNonRotatedOBB() {
        Line2D line2D = new Line2D(new Vector2f(-0.5f, 0), new Vector2f(0.5f, 0), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(0f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingOBB(line2D, obb));
    }

    @Test
    public void lineOnRotatedOBB() {
        Line2D line2D = new Line2D(new Vector2f(-2, 0), new Vector2f(2, 0), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingOBB(line2D, obb));
    }

    @Test
    public void lineOnRotatedOBBFace() {
        Line2D line2D = new Line2D(new Vector2f(-2, -0.59f), new Vector2f(2, 3.41f), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingOBB(line2D, obb));
    }

    @Test
    public void lineOnRotatedOBBCorner() {
        Line2D line2D = new Line2D(new Vector2f(-1, -1.41f), new Vector2f(1f, 1.41f), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertTrue(RaycastManager.isLineIntersectingOBB(line2D, obb));
    }

    @Test
    public void lineNotOnOBB() {
        Line2D line2D = new Line2D(new Vector2f(-2, -2), new Vector2f(2, -1), null, 1);
        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));

        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        assertFalse(RaycastManager.isLineIntersectingOBB(line2D, obb));
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

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastAABB(ray, aabb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastAABB(ray, aabb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastAABB(ray, aabb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastAABB(ray, aabb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastOBB(ray, obb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastOBB(ray, obb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastOBB(ray, obb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastOBB(ray, obb, rayResult).isHit();
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

        boolean hit = RaycastManager.raycastOBB(ray, obb, rayResult).isHit();
        assertFalse(hit);
    }

    // Circle vs Circle

    @Test
    public void circleVsCircleCollision() {
        Circle circle1 = new Circle(1f);
        Rigidbody2D circle1Rb = new Rigidbody2D();
        circle1Rb.setPosition(new Vector2f(0f, 0f));
        circle1.setRigidbody(circle1Rb);

        Circle circle2 = new Circle(1f);
        Rigidbody2D circle2Rb = new Rigidbody2D();
        circle2Rb.setPosition(new Vector2f(2f, 0f)); // touching
        circle2.setRigidbody(circle2Rb);

        boolean collision = CollisionManager.findCollisionFeatures(circle1, circle2).isColliding();
        assertTrue(collision);
    }

    @Test
    public void circleVsCircleNoCollision() {
        Circle circle1 = new Circle(1f);
        Rigidbody2D circle1Rb = new Rigidbody2D();
        circle1Rb.setPosition(new Vector2f(0f, 0f));
        circle1.setRigidbody(circle1Rb);

        Circle circle2 = new Circle(1f);
        Rigidbody2D circle2Rb = new Rigidbody2D();
        circle2Rb.setPosition(new Vector2f(3f, 0f)); // far enough to not collide
        circle2.setRigidbody(circle2Rb);

        boolean collision = CollisionManager.findCollisionFeatures(circle1, circle2).isColliding();
        assertFalse(collision);
    }

    // Circle vs OBB

    @Test
    public void circleVsOBBNonRotatedCollision() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(0f);
        obb.setRigidbody(boxRb);

        boolean collision = CollisionManager.findCollisionFeatures(circle, obb).isColliding();
        assertTrue(collision);
    }

    @Test
    public void circleVsOBBNonRotatedNoCollision() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(3, 3));
        boxRb.setRotation(0f);
        obb.setRigidbody(boxRb);

        boolean collision = CollisionManager.findCollisionFeatures(circle, obb).isColliding();
        assertFalse(collision);
    }

    @Test
    public void circleVsOBBRotatedCollision() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(0, 0));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        boolean collision = CollisionManager.findCollisionFeatures(circle, obb).isColliding();
        assertTrue(collision);
    }

    @Test
    public void circleVsOBBRotatedNoCollision() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRb = new Rigidbody2D();
        circleRb.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRb);

        OBBCollider obb = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb = new Rigidbody2D();
        boxRb.setPosition(new Vector2f(3, 3));
        boxRb.setRotation(45f);
        obb.setRigidbody(boxRb);

        boolean collision = CollisionManager.findCollisionFeatures(circle, obb).isColliding();
        assertFalse(collision);
    }

    // OBB vs OBB

    @Test
    public void OBBVsOBBNonRotatedCollision() {
        OBBCollider obb1 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb1 = new Rigidbody2D();
        boxRb1.setPosition(new Vector2f(0, 0));
        boxRb1.setRotation(0f);
        obb1.setRigidbody(boxRb1);

        OBBCollider obb2 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb2 = new Rigidbody2D();
        boxRb2.setPosition(new Vector2f(0.5f, 0.5f));
        boxRb2.setRotation(0f);
        obb2.setRigidbody(boxRb2);

        boolean collision = CollisionManager.findCollisionFeatures(obb1, obb2).isColliding();
        assertTrue(collision);
    }

    @Test
    public void OBBVsOBBNonRotatedNoCollision() {
        OBBCollider obb1 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb1 = new Rigidbody2D();
        boxRb1.setPosition(new Vector2f(0, 0));
        boxRb1.setRotation(0f);
        obb1.setRigidbody(boxRb1);

        OBBCollider obb2 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb2 = new Rigidbody2D();
        boxRb2.setPosition(new Vector2f(3, 3));
        boxRb2.setRotation(0f);
        obb2.setRigidbody(boxRb2);

        boolean collision = CollisionManager.findCollisionFeatures(obb1, obb2).isColliding();
        assertFalse(collision);
    }

    @Test
    public void OBBVsOBBRotatedCollision() {
        OBBCollider obb1 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb1 = new Rigidbody2D();
        boxRb1.setPosition(new Vector2f(0, 0));
        boxRb1.setRotation(45f);
        obb1.setRigidbody(boxRb1);

        OBBCollider obb2 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb2 = new Rigidbody2D();
        boxRb2.setPosition(new Vector2f(0.5f, 0.5f));
        boxRb2.setRotation(45f);
        obb2.setRigidbody(boxRb2);

        boolean collision = CollisionManager.findCollisionFeatures(obb1, obb2).isColliding();
        assertTrue(collision);
    }

    @Test
    public void OBBVsOBBRotatedNoCollision() {
        OBBCollider obb1 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb1 = new Rigidbody2D();
        boxRb1.setPosition(new Vector2f(0, 0));
        boxRb1.setRotation(45f);
        obb1.setRigidbody(boxRb1);

        OBBCollider obb2 = new OBBCollider(new Vector2f(-1, -1), new Vector2f(1, 1));
        Rigidbody2D boxRb2 = new Rigidbody2D();
        boxRb2.setPosition(new Vector2f(3, 3));
        boxRb2.setRotation(45f);
        obb2.setRigidbody(boxRb2);

        boolean collision = CollisionManager.findCollisionFeatures(obb1, obb2).isColliding();
        assertFalse(collision);
    }


}