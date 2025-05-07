package physics.forces;

import org.joml.Vector2f;
import physics.rigidbody.Rigidbody2D;

public class Friction implements ForceGenerator {
    private float staticCoefficient;
    private float kineticCoefficient; // Coefficient of kinetic friction (μ)
    private float normalForce; // Normal force (N)= mass * gravity
    private static final float VELOCITY_THRESHOLD_SQ = 0.001f;


    // Constructor for friction with a coefficient and gravity-based normal force
    public Friction(float staticCoefficient, float kineticCoefficient, float gravityMagnitude, Rigidbody2D rb) {
        this.staticCoefficient = staticCoefficient;
        this.kineticCoefficient = kineticCoefficient;
        // Assume normal force is mass * gravity (flat surface)
        this.normalForce = rb.getMass() * gravityMagnitude; // N = m * g
    }

    @Override
    public void updateForce(Rigidbody2D rb, float dt) {
        if (rb.hasInfiniteMass()) return;
        Vector2f velocity = rb.getLinearVelocity();
        Vector2f netForce = rb.getForceAccumulator();


        if (velocity.lengthSquared() <=VELOCITY_THRESHOLD_SQ){
            // Object is  stationary - apply static friction to cancel out net force
            float maxStaticFriction = staticCoefficient * normalForce;

            if (netForce.length() < maxStaticFriction) {
                // Cancel the net force
                Vector2f cancelForce = new Vector2f(netForce).negate();
                rb.addForce(cancelForce);
            } else {
                // Static friction can't hold it — kinetic friction takes over
                applyKineticFriction(rb, velocity, normalForce);
            }
        }else {
                applyKineticFriction(rb, velocity, normalForce);
        };
    } ;
    private void applyKineticFriction(Rigidbody2D rb, Vector2f velocity, float normalForce) {
        // Avoid issues with near-zero velocity
        if (velocity.lengthSquared() < VELOCITY_THRESHOLD_SQ) return;

        // Calculate friction force: F = -μ * N * v
        // v = velocity / |velocity| (normalized velocity)
        Vector2f frictionForce = new Vector2f(velocity).normalize().negate().mul(kineticCoefficient * normalForce);
        // Apply the friction force to the rigidbody
        rb.addForce(frictionForce);
    }
}


