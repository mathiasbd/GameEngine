package physics;

import org.joml.Vector2f;
import physics.forces.ForceRegistry;
import physics.forces.Gravity;
import physics.primitives.Collider;
import physics.collisions.CollisionManifold;
import physics.collisions.CollisionManager;
import physics.collisions.Rigidbody2D;
import physics.collisions.Rigidbody2D.BodyType;
import util.DTUMath;

import java.util.ArrayList;
import java.util.List;

import static util.DTUMath.cross;

/*
 * PhysicsSystem manages physics simulation: force application, collision detection,
 * impulse resolution, and positional correction for 2D rigid bodies.
 * Author(s): Gabriel, Ahmed, Mathias, Ilias
 */
public class PhysicsSystem {
    private ForceRegistry fr;
    private Gravity gravity;
    private List<Rigidbody2D> rb;
    private List<Rigidbody2D> bodies1;
    private List<Rigidbody2D> bodies2;
    private List<CollisionManifold> collisions;
    private List<CollisionManifold> ghostCollisions = new ArrayList<>();

    private float fixedUpdate;
    private int impulseIterations = 6;

    /*
     * Constructs the PhysicsSystem with a fixed timestep and gravity vector.
     * @param fixedUpdateDt - duration of each fixed update in seconds
     * @param gravity - global gravity force vector
     */
    public PhysicsSystem(float fixedUpdateDt, Vector2f gravity) {
        this.fr = new ForceRegistry();
        this.gravity = new Gravity(gravity);
        this.rb = new ArrayList<>();
        this.bodies1 = new ArrayList<>();
        this.bodies2 = new ArrayList<>();
        this.collisions = new ArrayList<>();
        this.fixedUpdate = fixedUpdateDt;
    }

    /*
     * Called each frame; drives the fixed-rate physics update.
     * @param dt - elapsed time since last frame (unused)
     */
    public void update(float dt) {
        fixedUpdate();  // perform physics step
    }

    /*
     * Performs collision detection, force integration, impulse resolution,
     * positional correction, and applies velocity updates to bodies.
     */
    public void fixedUpdate() {
        // clear previous frame data
        bodies1.clear();
        bodies2.clear();
        collisions.clear();
        ghostCollisions.clear();

        int size = rb.size();
        // broad-phase N^2 collision detection
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Rigidbody2D r1 = rb.get(i);
                Rigidbody2D r2 = rb.get(j);
                Collider c1 = r1.getCollider();
                Collider c2 = r2.getCollider();
                // skip null or two-statics
                if (c1 != null && c2 != null && !(r1.getBodyType() == BodyType.STATIC && r2.getBodyType() == BodyType.STATIC)) {
                    CollisionManifold result = CollisionManager.findCollisionFeatures(c1, c2);
                    if (result != null && result.isColliding()) {
                        result.setBodies(r1, r2);
                        if (c1.isSolid() && c2.isSolid()) {
                            bodies1.add(r1);
                            bodies2.add(r2);
                            collisions.add(result);
                        } else {
                            ghostCollisions.add(result);
                        }
                    }
                }
            }
        }

        // apply global forces like gravity
        fr.updateForces(fixedUpdate);

        // iterative impulse resolution for solid contacts
        for (int iter = 1; iter < impulseIterations; iter++) {
            for (int j = 0; j < collisions.size(); j++) {
                CollisionManifold m = collisions.get(j);
                for (Vector2f contact : m.getContactPoints()) {
                    applyImpulse(bodies1.get(j), bodies2.get(j), m);
                }
            }
        }

        // positional correction to prevent sinking
        for (int i = 0; i < collisions.size(); i++) {
            positionalCorrection(bodies1.get(i), bodies2.get(i), collisions.get(i));
        }

        // integrate velocities into positions
        for (Rigidbody2D body : rb) {
            if (body.getBodyType() == BodyType.DYNAMIC) {
                body.physicsUpdate(fixedUpdate);
            }
        }
    }

    /*
     * Resolves collision impulses and friction between two bodies.
     * @param r1 - first rigidbody
     * @param r2 - second rigidbody
     * @param m - collision manifold containing contact normal and points
     */
    private void applyImpulse(Rigidbody2D r1, Rigidbody2D r2, CollisionManifold m) {
        boolean imm1 = (r1.getBodyType() != BodyType.DYNAMIC);
        boolean imm2 = (r2.getBodyType() != BodyType.DYNAMIC);
        if (imm1 && imm2) return;

        float invMass1 = r1.getInverseMass();
        float invMass2 = r2.getInverseMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0.0f) return;

        Vector2f relativeVelocity = new Vector2f(r2.getLinearVelocity()).sub(r1.getLinearVelocity());
        Vector2f relativeNormal = new Vector2f(m.getNormal()).normalize();
        if (relativeVelocity.dot(relativeNormal) > 0.0f) return;

        float e = Math.min(r1.getRestitution(), r2.getRestitution());
        float j = -(1.0f + e) * relativeVelocity.dot(relativeNormal) / invMassSum;
        Vector2f impulse = new Vector2f(relativeNormal).mul(j);

        // Friction
        Vector2f tangent = new Vector2f(relativeVelocity)
                .sub(new Vector2f(relativeNormal).mul(relativeVelocity.dot(relativeNormal)));

        if (tangent.lengthSquared() > 0.001f) {
            tangent.normalize();

            float impulseAlongTangent = -relativeVelocity.dot(tangent);
            impulseAlongTangent /= invMassSum;

            float mu = (r1.getFriction() + r2.getFriction()) * 0.1f;
            float maxFriction = j * mu;
            impulseAlongTangent = Math.max(-maxFriction, Math.min(impulseAlongTangent, maxFriction));

            Vector2f frictionImpulse = new Vector2f(tangent).mul(impulseAlongTangent);

            if (r1.getBodyType() == BodyType.DYNAMIC) {
                r1.setVelocity(r1.getLinearVelocity().sub(new Vector2f(frictionImpulse).mul(invMass1)));
            }
            if (r2.getBodyType() == BodyType.DYNAMIC) {
                r2.setVelocity(r2.getLinearVelocity().add(new Vector2f(frictionImpulse).mul(invMass2)));
            }
        }

        List<Vector2f> vecMPoint1 = new ArrayList<>();
        List<Vector2f> vecMPoint2 = new ArrayList<>();
        for (Vector2f mPoint : m.getContactPoints()) {
            vecMPoint1.add(new Vector2f(mPoint).sub(r1.getPosition()));
            vecMPoint2.add(new Vector2f(mPoint).sub(r2.getPosition()));
        }


        if (!vecMPoint1.isEmpty() && j != 0.0f) {
            float friction = (r1.getFriction() + r2.getFriction())*0.5f;
            r1.addTorque(-friction*r1.getAngularVelocity());
            r2.addTorque(-friction*r2.getAngularVelocity());
//            System.out.println("Impulse: " + impulse + " vecMPoint: " + vecMPoint1.get(0));
            float angularMoment1 = 0.0f;
            float torque1 = 0.0f;
            int count1 = 0;
            for(Vector2f vecMPoint : vecMPoint1) {
                torque1 += DTUMath.cross(vecMPoint, r1.getForceAccumulator());
                angularMoment1 += DTUMath.cross(vecMPoint, impulse);
                count1++;
            }
            float angularMoment2 = 0.0f;
            float torque2 = 0.0f;
            int count2 = 0;
            for(Vector2f vecMPoint : vecMPoint2) {
                torque2 += DTUMath.cross(vecMPoint, r2.getForceAccumulator());
                angularMoment2 += DTUMath.cross(vecMPoint, impulse);
                count2++;
            }

            // Only apply to dynamic bodies
            if (r1.getBodyType() == BodyType.DYNAMIC) {
                r1.addTorque(-torque1*8);
                float angularVelocity1 = (angularMoment1 / r1.getInertia());
                r1.setVelocity(new Vector2f(r1.getLinearVelocity()).sub(new Vector2f(impulse).mul(invMass1)));
                r1.setAngularVelocity(r1.getAngularVelocity()-angularVelocity1*4);
            }
            if (r2.getBodyType() == BodyType.DYNAMIC) {
                r2.addTorque(-torque2*8);
                float angularVelocity2 = (angularMoment2 / r2.getInertia());
                r2.setVelocity(new Vector2f(r2.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass2)));
                r2.setAngularVelocity(r2.getAngularVelocity()+angularVelocity2*4);
            }
        }
    }

    /*
     * Corrects body positions based on penetration depth to reduce overlap.
     * @param r1 - first rigidbody
     * @param r2 - second rigidbody
     * @param m - collision manifold with penetration info
     */
    private void positionalCorrection(Rigidbody2D r1, Rigidbody2D r2, CollisionManifold m) {
        float penetration = m.getPenetrationDepth();
        if (penetration <= 0) return;
        Vector2f normal = m.getNormal();

        // special handling for kinematic vs static bodies
        if (r1.getBodyType() == BodyType.KINEMATIC && r2.getBodyType() == BodyType.STATIC) {
            r1.setPosition(r1.getPosition().sub(new Vector2f(normal).mul(penetration)));
            Vector2f v1 = r1.getLinearVelocity();
            v1.y = Math.max(v1.y, 0);
            r1.setVelocity(v1);
            return;
        }
        if (r2.getBodyType() == BodyType.KINEMATIC && r1.getBodyType() == BodyType.STATIC) {
            r2.setPosition(r2.getPosition().add(new Vector2f(normal).mul(penetration)));
            Vector2f v2 = r2.getLinearVelocity();
            v2.y = Math.max(v2.y, 0);
            r2.setVelocity(v2);
            return;
        }

        // mass-weighted correction for dynamic bodies
        final float percent = 0.8f, slop = 0.01f;
        float corr = Math.max(penetration - slop, 0) * percent;
        float im1 = (r1.getBodyType() == BodyType.DYNAMIC) ? r1.getInverseMass() : 0;
        float im2 = (r2.getBodyType() == BodyType.DYNAMIC) ? r2.getInverseMass() : 0;
        float totalInv = im1 + im2;
        if (totalInv == 0) return;
        Vector2f correction = new Vector2f(normal).mul(corr / totalInv);
        if (im1 > 0) r1.setPosition(r1.getPosition().sub(new Vector2f(correction).mul(im1)));
        if (im2 > 0) r2.setPosition(r2.getPosition().add(new Vector2f(correction).mul(im2)));
    }

    /*
     * Adds a Rigidbody to the simulation and registers gravity if dynamic.
     * @param body - the Rigidbody2D to add
     */
    public void addRigidbody(Rigidbody2D body) {
        rb.add(body);
        if (body.getBodyType() == BodyType.DYNAMIC) {
            fr.add(body, gravity);
        }
    }

    /*
     * @return list of all rigidbodies in the simulation
     */
    public List<Rigidbody2D> getRigidbodies() {
        return rb;
    }

    /*
     * @return list of collision manifolds for solid contacts
     */
    public List<CollisionManifold> getCollisions() {
        return collisions;
    }

    /*
     * @return list of collision manifolds for non-solid (ghost) contacts
     */
    public List<CollisionManifold> getGhostCollisions() {
        return ghostCollisions;
    }

    /*
     * Removes a Rigidbody from the simulation and force registry.
     * @param body - the Rigidbody2D to remove
     */
    public void removeRigidbody(Rigidbody2D body) {
        rb.remove(body);
    }

    /*
     * Resets the simulation by clearing all bodies, collisions, and forces.
     */
    public void reset() {
        rb.clear();
        bodies1.clear();
        bodies2.clear();
        collisions.clear();
        ghostCollisions.clear();
        fr.clearAll();
    }
}
