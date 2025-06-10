package physics.collisions;

import components.Component;
import org.example.GameObject;
import org.example.Transform;
import org.joml.Vector2f;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;

public class Rigidbody2D extends Component {


    public enum BodyType {
        STATIC,
        DYNAMIC,
        KINEMATIC,
        NO_IMPULSE
    }

    private BodyType bodyType = BodyType.DYNAMIC;
    private Transform rawTransform;
    private Collider collider;

    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;
    private float mass = 1f;

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
    public void update(float dt) {}

    public void physicsUpdate(float dt) {
        linearDamping = 0.05f; // temporary value, can be set externally
        angularDamping = 0.05f; // temporary value, can be set externally

        if (this.mass == 0.0f || bodyType == BodyType.STATIC) return;

        Vector2f acceleration = new Vector2f(forceAcc).mul(getInverseMass());
        linearVelocity.add(acceleration.mul(dt));
        linearVelocity.mul(1.0f - linearDamping * dt);

        this.position.add(new Vector2f(linearVelocity).mul(dt));

        // Angular motion
        if (!fixedRotation) {
            float angularAcceleration = torque * getInverseInertia();
            if(angularAcceleration != 0.0f) {
//                System.out.println(angularAcceleration);
            }
            angularVelocity += angularAcceleration * dt;
            angularVelocity *= (1.0f - angularDamping * dt);
            rotation += angularVelocity * dt;
        }
        
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
        this.torque = 0.0f;

    }

    public float getTorque() {
        return torque;
    }

    public boolean hasInfiniteMass() {
        return this.mass == 0.0f || bodyType == BodyType.STATIC;
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

    public float getFriction() {
        return friction;
    }

    public void setTorque(float torque) {
        this.torque = torque;
    }

    public float getInverseMass() {
        if (mass == 0.0f) {
            return 0.0f;
        }
        return 1.0f / mass;
    }

    public Vector2f getForceAccumulator() {
        return new Vector2f(forceAcc);
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (this.mass != 0.0f) {
            //this.inverseMass = 1.0f / this.mass;
        }
    }

    public void addForce(Vector2f force) {
        this.forceAcc.add(force);
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }

    public void addTorque(float torque) {
        this.torque += torque;
    }

    public float getInverseInertia() {
        if (inertia == 0.0f) {
            return 0.0f;
        }
        return 1.0f / inertia;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
        if(collider instanceof OBBCollider && bodyType != BodyType.STATIC) {
            inertia = (mass*(((OBBCollider) collider).getHalfSize().x*2*((OBBCollider) collider).getHalfSize().y*2))/6;
        }
        else if (collider instanceof Circle circle && bodyType != BodyType.STATIC) {
            float radius = circle.getRadius();
            inertia = 0.05f * mass * radius * radius;
        }
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

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public float getInertia() {
        return inertia;
    }

    public GameObject getGameObject() {
        return this.gameObject;
    }

}