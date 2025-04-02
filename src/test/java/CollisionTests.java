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

//    @Test
//    public void lineInABox() {
//        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(1.5f, 0.5f), null, 1);
//        Square square = new Square(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        // Test rigidbody
//        Rigidbody2D squareRigidbody = new Rigidbody2D();
//        squareRigidbody.setPosition(new Vector2f(0f, 0f));
//        square.setRigidbody(squareRigidbody);
//
//        assertTrue(IntersectionDetecter.lineInSquare(line, square));
//    }
//
//    @Test
//    public void lineNotInABox() {
//        Line2D line = new Line2D(new Vector2f(-1.5f, -1.5f), new Vector2f(2.5f, -1f), null, 1);
//        Square square = new Square(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
//        // Test rigidbody
//        Rigidbody2D squareRigidbody = new Rigidbody2D();
//        squareRigidbody.setPosition(new Vector2f(0f, 0f));
//        square.setRigidbody(squareRigidbody);
//
//        assertFalse(IntersectionDetecter.lineInSquare(line, square));
//    }

    @Test
    public void lineOnSquarePerimeter() {
        Line2D line = new Line2D(new Vector2f(-1.5f, -0.5f), new Vector2f(0f, -1.5f), null, 1);
        Square square = new Square(new Vector2f(-1f, -1f), new Vector2f(1f, 1f));
        // Test rigidbody
        Rigidbody2D squareRigidbody = new Rigidbody2D();
        squareRigidbody.setPosition(new Vector2f(0f, 0f));
        square.setRigidbody(squareRigidbody);

        assertTrue(IntersectionDetecter.lineInSquare(line, square));
    }
}
