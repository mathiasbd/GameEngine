package physics.primitives;

import components.Component;
import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

/*
 * Collider is the base class for collision shapes attached to rigid bodies.
 * It maintains an offset and solidity, and links to a Rigidbody2D for physics.
 * Author(s):
 */
public abstract class Collider extends Component {
    protected Vector2f offset = new Vector2f();
    private boolean isSolid = true;

    /*
     * Associates this collider with the given rigidbody.
     * @param rigidbody - the Rigidbody2D to attach
     */
    public abstract void setRigidbody(Rigidbody2D rigidbody);

    /*
     * @return the Rigidbody2D this collider is attached to
     */
    public abstract Rigidbody2D getRigidbody();

    /*
     * Enables or disables solidity of this collider.
     * @param solid - true to make collider solid, false for ghost
     */
    public void setSolid(boolean solid) {
        this.isSolid = solid;
    }

    /*
     * @return true if this collider participates in solid collisions
     */
    public boolean isSolid() {
        return isSolid;
    }

    @Override
    public void update(float dt) {
        // no-op: collider state is updated via its rigidbody
    }
}
