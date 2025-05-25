package physics.rigidbody;

import components.Component;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.Collider;


public class Rigidbody2D extends Component {
    private Transform rawTransform;
    private Collider collider;

    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;
    private float mass =0f;
    private float inverseMass = 0f;
    private boolean hasGravity = false;

    private float restitution = 1.0f;
    private Vector2f forceAcc= new Vector2f();
    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity = 0.0f;
    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;

    private boolean fixedRotation = false;

    @Override
    public void update(float dt) {

    }
    public void physicsUpdate(float dt) {
        if (this.mass == 0.0f) return;

        // Calculate linear velocity
        Vector2f acceleration = new Vector2f(forceAcc).mul(this.inverseMass);
        linearVelocity.add(acceleration.mul(dt));

        // Update the linear position
        System.out.println("Updating position with linear velocity: " + linearVelocity);
        System.out.println("Current position: " + this.position);
        this.position.add(new Vector2f(linearVelocity).mul(dt));
        System.out.println("New position: " + this.position);

        synchCollisionTransforms();
        clearAccumulators();
    }

    public void synchCollisionTransforms() {
        if (rawTransform != null) {
            rawTransform.position.set(this.position);
        }
    }

    public void clearAccumulators() {
        this.forceAcc.zero();
    }

    public boolean hasInfiniteMass() {
        return this.mass == 0.0f;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setVelocity(Vector2f velocity) {
        this.linearVelocity.set(velocity);
    }

    public Vector2f getLinearVelocity() {
        return this.linearVelocity;
    }
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getMass() {
        return mass;
    }

    public float getInverseMass() {
        return inverseMass;
    }
    public Vector2f getForceAccumulator() {
        return new Vector2f(forceAcc);
    }
    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0.0f) {
            this.inverseMass = 1.0f / this.mass;
        }
    }
    public void addForce(Vector2f force) {
        this.forceAcc.add(force);
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }
    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public Collider getCollider() {
        return this.collider;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }
    public float getRestitution() {
        return restitution;
    }

    public void setHasGravity(boolean value) {
        this.hasGravity = value;
    }

    public boolean getHasGravity() {
        return hasGravity;
    }
}
