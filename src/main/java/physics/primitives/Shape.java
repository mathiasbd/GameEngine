package physics.primitives;

import physics.rigidbody.Rigidbody2D;

public abstract class Shape {
    protected Rigidbody2D rigidbody;

    public Rigidbody2D getRigidbody() {
        return rigidbody;
    }
    public void setRigidbody(Rigidbody2D rigidbody) {
        this.rigidbody = rigidbody;
    }

    public abstract boolean cast(Raycast ray, RaycastResult rayResult);
}
