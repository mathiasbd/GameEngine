package physics.forces;

import physics.collisions.Rigidbody2D;

/*
 * ForceRegistration pairs a ForceGenerator with a Rigidbody2D
 * to track which forces apply to which bodies each update cycle.
 * Author(s):
 */
public class ForceRegistration {
    public ForceGenerator fg;
    public Rigidbody2D rb;

    /*
     * Constructs a registration linking a force generator to a rigidbody.
     * @param fg - the ForceGenerator to register
     * @param rb - the Rigidbody2D that will receive the force
     */
    public ForceRegistration(ForceGenerator fg, Rigidbody2D rb) {
        this.fg = fg;
        this.rb = rb;
    }

    /*
     * Checks equality by comparing generator and body references.
     * @param other - the object to compare with
     * @return true if both ForceRegistration and Rigidbody2D match
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() != ForceRegistration.class) return false;

        ForceRegistration fr = (ForceRegistration) other;
        return fr.rb == this.rb && fr.fg == this.fg;
    }
}
