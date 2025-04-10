import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics.primitives.*;
import physics.rigidbody.RaycastManager;
import physics.rigidbody.Line2D;
import physics.rigidbody.Rigidbody2D;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionTests {
    @Test
    public void lineInCircle() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1f, 0f), new Vector2f(1f, 0f), null, 1);
        assertTrue(RaycastManager.lineInCircle(line, circle));
    }

    @Test
    public void lineOnCirclePerimeter() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1.0f, 0f), new Vector2f(-1f, 1f), null, 1);
        assertTrue(RaycastManager.lineInCircle(line, circle));
    }

    @Test
    public void lineNotInCircle() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1.5f, 0f), new Vector2f(-1f, 1f), null, 1);
        assertFalse(RaycastManager.lineInCircle(line, circle));
    }

    @Test
    public void lineInABox() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(1.5f, 0.5f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);


        assertTrue(RaycastManager.lineInABox(line, alignedBox));
    }

    @Test
    public void lineNotInABox() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -1.5f), new Vector2f(2.5f, -1f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);

        assertFalse(RaycastManager.lineInABox(line, alignedBox));
    }

    @Test
    public void lineOnABoxPerimeter() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(-0.5f, -1.5f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);

        assertTrue(RaycastManager.lineInABox(line, alignedBox));
    }

    @Test
    public void lineOnASquare() {
        Line2D line = new Line2D(new Vector2f(-0.5f, 0), new Vector2f(0.5f, 0), null, 1);
        Square square = new Square(new Vector2f(-1, -1), new Vector2f(1, 1));
        // Test rigidbody
        Rigidbody2D rb = new Rigidbody2D();
        rb.setPosition(new Vector2f(0, 0));
        rb.setRotation(0); // No rotation
        square.setRigidbody(rb);

        assertTrue(RaycastManager.lineInSquare(line, square));
    }

    @Test
    public void testLineIntersectingRotatedSquare() {
        Square square = new Square(new Vector2f(-1, -1), new Vector2f(1, 1));
        Line2D line = new Line2D(new Vector2f(-2, 0), new Vector2f(2, 0), null, 1);
        // Test rigidbody
        Rigidbody2D rb = new Rigidbody2D();
        rb.setPosition(new Vector2f(0, 0));
        rb.setRotation((float) Math.toRadians(45)); // Rotate 45 degrees
        square.setRigidbody(rb);

        assertTrue(RaycastManager.lineInSquare(line, square));
    }

    @Test
    public void raycastCircleDiagonalIntersection() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(2f, 2f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(0f, 0f), new Vector2f(1f, 1f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastCircleTangentIntersection() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 1f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(-2f, 0f), new Vector2f(1f, 0f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertTrue(hit);
    }

    @Test
    public void raycastCircleNoIntersection() {
        Circle circle = new Circle(1f);
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(2f, 2f));
        circle.setRigidbody(circleRigidbody);

        Raycast ray = new Raycast(new Vector2f(0f, 0f), new Vector2f(0.5f, 2f));
        RaycastResult rayResult = new RaycastResult();

        boolean hit = RaycastManager.raycastCircle(ray, circle, rayResult);
        assertFalse(hit);
    }

}
