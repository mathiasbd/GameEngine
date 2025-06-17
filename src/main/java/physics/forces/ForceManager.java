package physics.forces;

import physics.collisions.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

/*
 * ForceManager maintains registrations of ForceGenerators applied to Rigidbody2D instances.
 * It updates, adds, and removes force generators for the physics simulation.
 * Author(s):
 */
public class ForceManager {
    private List<ForceRegistration> reg;

    /*
     * Constructs a new ForceManager with an empty registry.
     */
    public ForceManager() {
        this.reg = new ArrayList<>();
    }

    /*
     * Registers a force generator to apply to the specified rigidbody.
     * @param rb - the Rigidbody2D to receive the force
     * @param fg - the ForceGenerator to apply
     */
    public void add(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        reg.add(fr);
    }

    /*
     * Removes a previously registered force generator from the specified rigidbody.
     * @param rb - the Rigidbody2D whose force generator is to be removed
     * @param fg - the ForceGenerator to remove
     */
    public void remove(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        reg.remove(fr);
    }

    /*
     * Clears all force generator registrations.
     */
    public void clearAll() {
        reg.clear();
    }

    /*
     * Updates all registered force generators for the given timestep.
     * @param dt - duration of the timestep in seconds
     */
    public void updateForces(float dt) {
        for (ForceRegistration fr : reg) {
            fr.fg.updateForce(fr.rb, dt);
        }
    }

    /*
     * Zeroes out all force accumulators (not yet implemented).
     */
    public void zeroForces() {
        // TODO: implement force zeroing logic
    }
}