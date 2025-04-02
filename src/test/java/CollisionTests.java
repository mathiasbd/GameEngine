import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics.primitives.AlignedBox;
import physics.primitives.Circle;
import physics.primitives.Square;
import physics.rigidbody.IntersectionDetecter;
import physics.rigidbody.Line2D;
import physics.rigidbody.Rigidbody2D;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollisionTests {
    @Test
    public void lineInCircle() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1f, 0f), new Vector2f(1f, 0f), null, 1);
        assertTrue(IntersectionDetecter.lineInCircle(line, circle));
    }

    @Test
    public void lineOnCirclePerimeter() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1.0f, 0f), new Vector2f(-1f, 1f), null, 1);
        assertTrue(IntersectionDetecter.lineInCircle(line, circle));
    }

    @Test
    public void lineNotInCircle() {
        Circle circle = new Circle(1f);
        // Test rigidbody
        Rigidbody2D circleRigidbody = new Rigidbody2D();
        circleRigidbody.setPosition(new Vector2f(0f, 0f));
        circle.setRigidbody(circleRigidbody);

        Line2D line = new Line2D(new Vector2f(-1.5f, 0f), new Vector2f(-1f, 1f), null, 1);
        assertFalse(IntersectionDetecter.lineInCircle(line, circle));
    }

    @Test
    public void lineInABox() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(1.5f, 0.5f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);


        assertTrue(IntersectionDetecter.lineInABox(line, alignedBox));
    }

    @Test
    public void lineNotInABox() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -1.5f), new Vector2f(2.5f, -1f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);

        assertFalse(IntersectionDetecter.lineInABox(line, alignedBox));
    }

    @Test
    public void lineOnABoxPerimeter() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(-0.5f, -1.5f), null, 1);
        AlignedBox alignedBox = new AlignedBox(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D alignedBoxRigidbody = new Rigidbody2D();
        alignedBoxRigidbody.setPosition(new Vector2f(0f, 0f));
        alignedBox.setRigidbody(alignedBoxRigidbody);

        assertTrue(IntersectionDetecter.lineInABox(line, alignedBox));
    }
}
