package physics.collisions;

import components.Component;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;

/*
 * Rigidbody2D adds physics properties and behavior to GameObjects.
 * It supports dynamic, static, and kinematic bodies with forces,
 * velocity integration, collision synchronization, and inertia computation.
 * Author(s): Ilias, Gabriel, Mathias, Ahmed
 */
public class Rigidbody2D extends Component {

    public enum BodyType {
        STATIC,
        DYNAMIC,
        KINEMATIC
    }

    private BodyType bodyType = BodyType.DYNAMIC;
    private Transform rawTransform;
    private Collider collider;
    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;
    private float mass = 1.0f;

    private float restitution = 1.0f;
    private Vector2f forceAcc = new Vector2f();
    private Vector2f linearVelocity = new Vector2f();

    private float angularVelocity = 0.0f;
    private float linearDamping = 0.05f;
    private float angularDamping = 0.15f;
    private boolean fixedRotation = false;

    private float torque = 0.0f;
    private float inertia = 1.0f;
    private float friction = 0.5f;

    @Override
    public void update(float dt) {
        // no-op: physics driven by physicsUpdate
    }

    /*
     * Integrates forces and velocities, updates position and rotation, and syncs transform.
     * @param dt - elapsed time in seconds
     */
    public void physicsUpdate(float dt) {
        // reset damping (can be customized externally)
        linearDamping = 0.05f;
        angularDamping = 0.05f;

        // skip static or infinite-mass bodies
        if (mass == 0.0f || bodyType == BodyType.STATIC) return;

        // integrate linear forces
        Vector2f accel = new Vector2f(forceAcc).mul(getInverseMass());
        linearVelocity.add(new Vector2f(accel).mul(dt));
        linearVelocity.mul(1.0f - linearDamping * dt);  // apply damping

        // integrate position
        position.add(new Vector2f(linearVelocity).mul(dt));

        // integrate angular motion if allowed
        if (!fixedRotation) {
            float angAcc = torque * getInverseInertia();
            angularVelocity += angAcc * dt;
            angularVelocity *= (1.0f - angularDamping * dt);
            rotation += angularVelocity * dt;
        }

        synchCollisionTransforms();
        clearAccumulators();
    }

    /*
     * Synchronizes the GameObject's Transform position with this rigidbody.
     */
    public void synchCollisionTransforms() {
        if (rawTransform != null) {
            rawTransform.position.set(this.position); // update transform
        }
    }

    /*
     * Clears accumulated forces and torque.
     */
    public void clearAccumulators() {
        forceAcc.zero();
        torque = 0.0f;
    }

    /*
     * @return the accumulated torque for this body
     */
    public float getTorque() {
        return torque;
    }

    /*
     * @return true if body has infinite mass (static)
     */
    public boolean hasInfiniteMass() {
        return mass == 0.0f || bodyType == BodyType.STATIC;
    }

    /*
     * @return current position of the rigidbody
     */
    public Vector2f getPosition() {
        return position;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    /*
     * Sets the position directly (teleport).
     * @param position - new position vector
     */
    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    /*
     * Sets the linear velocity.
     * @param velocity - new velocity vector
     */
    public void setVelocity(Vector2f velocity) {
        this.linearVelocity.set(velocity);
    }

    /*
     * @return current linear velocity
     */
    public Vector2f getLinearVelocity() {
        return linearVelocity;
    }

    /*
     * @return current rotation angle in radians
     */
    public float getRotation() {
        return rotation;
    }

    /*
     * Sets the rotation angle.
     * @param rotation - rotation in radians
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /*
     * @return mass of the body
     */
    public float getMass() {
        return mass;
    }

    /*
     * @return friction coefficient
     */
    public float getFriction() {
        return friction;
    }

    /*
     * @return inverse mass (0 if infinite)
     */
    public float getInverseMass() {
        return (mass == 0.0f) ? 0.0f : 1.0f / mass;
    }

    /*
     * @return copy of current force accumulator
     */
    public Vector2f getForceAccumulator() {
        return new Vector2f(forceAcc);
    }

    /*
     * Sets the mass and updates inertia if collider present.
     * @param mass - new mass value
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    /*
     * Adds a force to the accumulator.
     * @param force - force vector to apply
     */
    public void addForce(Vector2f force) {
        forceAcc.add(force);
    }

    /*
     * Associates this rigidbody with a Transform for syncing.
     * @param rawTransform - Transform to update
     */
    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }

    /*
     * Adds torque to accumulate rotational force.
     * @param torque - torque value to add
     */
    public void addTorque(float torque) {
        this.torque += torque;
    }

    /*
     * @return inverse inertia (0 if fixed rotation or infinite)
     */
    public float getInverseInertia() {
        return (inertia == 0.0f) ? 0.0f : 1.0f / inertia;
    }

    /*
     * @return current angular velocity
     */
    public float getAngularVelocity() {
        return angularVelocity;
    }

    /*
     * Sets the angular velocity.
     * @param angularVelocity - new angular velocity
     */
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }


    public void setFixedRotation(boolean x ) {
        this.fixedRotation = x;
    }

    /*
     * Sets the collider and computes inertia based on shape.
     * @param collider - Collider instance (OBB or Circle)
     */
    public void setCollider(Collider collider) {
        this.collider = collider;
        if (bodyType != BodyType.STATIC && collider instanceof OBBCollider obb) {
            inertia = (mass * (obb.getHalfSize().x*2 * obb.getHalfSize().y*2)) / 6; // rectangle inertia
        } else if (bodyType != BodyType.STATIC && collider instanceof Circle circ) {
            inertia = 0.05f * mass * circ.getRadius() * circ.getRadius(); // approximate disk
        }
    }

    /*
     * @return current collider shape
     */
    public Collider getCollider() {
        return collider;
    }

    /*
     * @return restitution coefficient (bounciness)
     */
    public float getRestitution() {
        return restitution;
    }

    /*
     * Sets restitution (bounciness).
     * @param restitution - restitution coefficient
     */
    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    /*
     * @return the BodyType of this rigidbody
     */
    public BodyType getBodyType() {
        return bodyType;
    }

    /*
     * Sets the BodyType (STATIC, DYNAMIC, KINEMATIC).
     * @param bodyType - new body type
     */
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    /*
     * @return moment of inertia
     */
    public float getInertia() {
        return inertia;
    }

    /*
     * @return associated GameObject
     */
    public GameObject getGameObject() {
        return gameObject;
    }
}