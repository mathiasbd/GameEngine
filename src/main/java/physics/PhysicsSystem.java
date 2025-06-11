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

    public PhysicsSystem(float fixedUpdateDt, Vector2f gravity) {
        this.fr = new ForceRegistry();
        this.gravity = new Gravity(gravity);

        this.rb = new ArrayList<>();
        this.bodies1 = new ArrayList<>();
        this.bodies2 = new ArrayList<>();
        this.collisions = new ArrayList<>();

        this.fixedUpdate = fixedUpdateDt;
    }

    public void update(float dt) {
        fixedUpdate();
    }

    public void fixedUpdate() {
        bodies1.clear();
        bodies2.clear();
        collisions.clear();

        int size = rb.size();
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (i == j) continue;

                Rigidbody2D r1 = rb.get(i);
                Rigidbody2D r2 = rb.get(j);
                Collider c1 = r1.getCollider();
                Collider c2 = r2.getCollider();

                if (c1 != null && c2 != null &&
                        !(r1.getBodyType() == BodyType.STATIC && r2.getBodyType() == BodyType.STATIC)) {

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

        fr.updateForces(fixedUpdate);

        for (int i = 1; i < impulseIterations; i++) {
            for (int j = 0; j < collisions.size(); j++) {
                int jSize = collisions.get(j).getContactPoints().size();
                for (int k = 0; k < jSize; k++) {
                    applyImpulse(bodies1.get(j), bodies2.get(j), collisions.get(j));
                }
            }
        }

        // Positional correction
        for (int i = 0; i < collisions.size(); i++) {
            positionalCorrection(bodies1.get(i), bodies2.get(i), collisions.get(i));
        }


        for (Rigidbody2D body : rb) {
            if (body.getBodyType() != BodyType.STATIC) {
                body.physicsUpdate(fixedUpdate);
            }
        }
    }

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

    private void positionalCorrection(Rigidbody2D r1, Rigidbody2D r2, CollisionManifold m) {
        float penetration = m.getPenetrationDepth();
        if (penetration <= 0f) return;

        Vector2f normal = m.getNormal();

        // Kinematic vs Static → push kinematic out fully & zero its normal velocity
        if (r1.getBodyType() == BodyType.KINEMATIC && r2.getBodyType() == BodyType.STATIC) {
            // move r1 *up* by full penetration
            Vector2f sep = new Vector2f(normal).mul(penetration);
            r1.setPosition(r1.getPosition().sub(sep));

            // zero the downward component so it won't sink next frame
            Vector2f v1 = r1.getLinearVelocity();
            v1.y = Math.max(v1.y, 0f);
            r1.setVelocity(v1);
            return;
        }
        if (r2.getBodyType() == BodyType.KINEMATIC && r1.getBodyType() == BodyType.STATIC) {
            Vector2f sep = new Vector2f(normal).mul(penetration);
            r2.setPosition(r2.getPosition().add(sep));

            Vector2f v2 = r2.getLinearVelocity();
            v2.y = Math.max(v2.y, 0f);
            r2.setVelocity(v2);
            return;
        }

        // Otherwise (dynamic vs dynamic, or dynamic vs kinematic),
        // fall back to your usual mass-weighted correction:
        final float percent = 0.8f;
        final float slop    = 0.01f;
        float corrMag = Math.max(penetration - slop, 0f) * percent;

        float invMass1 = (r1.getBodyType() == BodyType.DYNAMIC) ? r1.getInverseMass() : 0f;
        float invMass2 = (r2.getBodyType() == BodyType.DYNAMIC) ? r2.getInverseMass() : 0f;
        float totalInv = invMass1 + invMass2;
        if (totalInv == 0f) return;

        Vector2f correction = new Vector2f(normal).mul(corrMag / totalInv);

        if (invMass1 > 0f) {
            Vector2f shift1 = new Vector2f(correction).mul(invMass1);
            r1.setPosition(r1.getPosition().sub(shift1));
        }
        if (invMass2 > 0f) {
            Vector2f shift2 = new Vector2f(correction).mul(invMass2);
            r2.setPosition(r2.getPosition().add(shift2));
        }
    }

    public void addRigidbody(Rigidbody2D body) {
        this.rb.add(body);
        if (body.getBodyType() != BodyType.STATIC) {
            this.fr.add(body, gravity);
        }
    }

    public List<Rigidbody2D> getRigidbodies() {
        return rb;
    }

    // Spawning

    public List<CollisionManifold> getGhostCollisions() {
        return ghostCollisions;
    }

    public void removeRigidbody(Rigidbody2D body) {
        this.rb.remove(body);
    }


    public void reset() {
        this.rb.clear();
        this.bodies1.clear();
        this.bodies2.clear();
        this.collisions.clear();
        this.ghostCollisions.clear();
        this.fr.clearAll();
    }


}
