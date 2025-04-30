package physics.forces;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class Gravity implements ForceGenerator{

    private Vector2f gravity;

    public Gravity(Vector2f force) {
        this.gravity = new Vector2f(force);
    }
    @Override
    public void updateForce(Rigidbody2D rb, float dt) {
        rb.addForce(new Vector2f(gravity).mul(rb.getMass()));
    }
}
