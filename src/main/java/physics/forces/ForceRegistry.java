package physics.forces;

import physics.collisions.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

/*
 * ForceRegistry tracks associations between ForceGenerators and Rigidbody2D instances.
 * It allows registering, removing, and updating all forces each physics step.
 * Author(s): Ahmed
 */
public class ForceRegistry {
    private List<ForceRegistration> registry;

    /*
     * Constructs an empty ForceRegistry.
     */
    public ForceRegistry() {
        this.registry = new ArrayList<>();
    }

    /*
     * Registers a force generator to apply to the specified rigidbody.
     * @param rb - the Rigidbody2D to receive the force
     * @param fg - the ForceGenerator to register
     */
    public void add(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.add(fr);
    }

    /*
     * Removes a previously registered force generator from the specified rigidbody.
     * @param rb - the Rigidbody2D whose force generator is to be removed
     * @param fg - the ForceGenerator to remove
     */
    public void remove(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        registry.remove(fr);
    }

    /*
     * Clears all force registrations.
     */
    public void clearAll() {
        registry.clear();
    }

    /*
     * Updates all registered forces for the given timestep.
     * @param dt - duration of the timestep in seconds
     */
    public void updateForces(float dt) {
        for (ForceRegistration fr : registry) {
            fr.fg.updateForce(fr.rb, dt);
        }
    }

    /*
     * Resets all accumulated forces (not yet implemented).
     */
    public void resetForces() {
        // TODO: implement force accumulator reset for each rigidbody
    }
}
