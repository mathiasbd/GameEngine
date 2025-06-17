package physics.forces;

import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

/*
 * Friction applies static and kinetic friction forces to Rigidbody2D objects.
 * Author(s):
 */
public class Friction implements ForceGenerator {
    private float staticCoefficient;
    private float kineticCoefficient;
    private float normalForce;
    private static final float VELOCITY_THRESHOLD_SQ = 0.001f;

    /*
     * Constructs a Friction generator using coefficients and gravity.
     * @param staticCoefficient - coefficient of static friction
     * @param kineticCoefficient - coefficient of kinetic friction
     * @param gravityMagnitude - magnitude of gravity to compute normal force
     * @param rb - the Rigidbody2D to use for mass lookup
     */
    public Friction(float staticCoefficient, float kineticCoefficient, float gravityMagnitude, Rigidbody2D rb) {
        this.staticCoefficient = staticCoefficient;
        this.kineticCoefficient = kineticCoefficient;
        this.normalForce = rb.getMass() * gravityMagnitude;
    }

    /*
     * Updates the friction force on the rigidbody.
     * @param rb - the Rigidbody2D to apply friction to
     * @param dt - duration of the timestep (unused)
     */
    @Override
    public void updateForce(Rigidbody2D rb, float dt) {
        if (rb.hasInfiniteMass()) return;
        Vector2f velocity = rb.getLinearVelocity();
        Vector2f netForce = rb.getForceAccumulator();

        if (velocity.lengthSquared() <= VELOCITY_THRESHOLD_SQ) {
            float maxStatic = staticCoefficient * normalForce;
            if (netForce.length() < maxStatic) {
                rb.addForce(new Vector2f(netForce).negate()); // cancel net force
            } else {
                applyKineticFriction(rb, velocity);
            }
        } else {
            applyKineticFriction(rb, velocity);
        }
    }

    /*
     * Applies kinetic friction based on current velocity.
     * @param rb - the Rigidbody2D to apply friction to
     * @param velocity - current linear velocity of the body
     */
    private void applyKineticFriction(Rigidbody2D rb, Vector2f velocity) {
        if (velocity.lengthSquared() < VELOCITY_THRESHOLD_SQ) return;
        Vector2f frictionForce = new Vector2f(velocity)
                .normalize()
                .negate()
                .mul(kineticCoefficient * normalForce);
        rb.addForce(frictionForce);
    }
}
