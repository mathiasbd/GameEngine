package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class AlignedBox {
    private Vector2f halfSize;
    private Vector2f size = new Vector2f();
    private Rigidbody2D rigidbody = null;



    public AlignedBox() {
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public AlignedBox(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min); // Since we know its axis aligned, we can get all the points by just knowing the min and max points
        this.halfSize = new Vector2f(this.size).mul(0.5f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody.getPosition()).sub(this.halfSize); // assume the position is the center of the box
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody.getPosition()).add(this.halfSize);
    }
}
