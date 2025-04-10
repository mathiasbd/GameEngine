package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.RaycastManager;
import physics.rigidbody.Rigidbody2D;

public class Square extends Shape {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private Rigidbody2D rigidbody = null;

    public Square() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Square(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody.getPosition()).sub(this.halfSize); // assume the position is the center of the box
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody.getPosition()).add(this.halfSize);
    }

    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }

    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    @Override
    public boolean cast(Raycast ray, RaycastResult rayResult) {
        return RaycastManager.raycastSquare(ray, this, rayResult);
    }
}
