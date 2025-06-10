package physics.primitives;

import components.Component;
import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

public abstract class Collider extends Component {
    protected Vector2f offset = new Vector2f();

    public abstract void setRigidbody(Rigidbody2D rigidbody);

    public abstract Rigidbody2D getRigidbody();

    @Override
    public void update(float dt) {
    }
}
