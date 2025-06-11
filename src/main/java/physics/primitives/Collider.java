package physics.primitives;

import components.Component;
import org.joml.Vector2f;
import physics.collisions.Rigidbody2D;

public abstract class Collider extends Component {
    protected Vector2f offset = new Vector2f();
    public boolean isSolid = true;

    public abstract void setRigidbody(Rigidbody2D rigidbody);

    public abstract Rigidbody2D getRigidbody();

    public void setSolid(boolean solid) {
        this.isSolid = solid;
    }

    public boolean isSolid() {
        return isSolid;
    }

    @Override
    public void update(float dt) {
    }

}
