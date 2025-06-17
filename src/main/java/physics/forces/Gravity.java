package physics.forces;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

/*
 * Gravity applies a constant gravitational force to Rigidbody2D objects.
 * Author(s):
 */
public class Gravity implements ForceGenerator {

    private Vector2f gravity;

    /*
     * Constructs a Gravity generator with the specified acceleration vector.
     * @param force - gravity acceleration vector
     */
    public Gravity(Vector2f force) {
        this.gravity = new Vector2f(force);
    }

    /*
     * Applies gravitational force to the rigidbody.
     * @param rb - the Rigidbody2D to apply gravity to
     * @param dt - duration of the timestep (unused)
     */
    @Override
    public void updateForce(Rigidbody2D rb, float dt) {
        rb.addForce(new Vector2f(gravity).mul(rb.getMass()));
    }
}
