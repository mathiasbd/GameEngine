package physics.forces;

import physics.collisions.Rigidbody2D;

/*
 * ForceGenerator defines an interface for applying continuous forces
 * to Rigidbody2D objects over time.
 * Author(s):
 */
public interface ForceGenerator {
    /*
     * Applies or updates the force on the given rigidbody for the specified timestep.
     * @param rb - the Rigidbody2D to update with force
     * @param dt - duration of the timestep in seconds
     */
    void updateForce(Rigidbody2D rb, float dt);
}