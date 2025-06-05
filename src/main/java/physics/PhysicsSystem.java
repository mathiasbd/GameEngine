package physics;

import org.joml.Vector2f;
import physics.forces.ForceRegistry;
import physics.forces.Gravity;
import physics.primitives.Collider;
import physics.rigidbody.CollisionManifold;
import physics.rigidbody.Collisions;
import physics.rigidbody.Rigidbody2D;
import physics.rigidbody.Rigidbody2D.BodyType;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private ForceRegistry fr;
    private Gravity gravity;

    private List<Rigidbody2D> rb;
    private List<Rigidbody2D> bodies1;
    private List<Rigidbody2D> bodies2;
    private List<CollisionManifold> collisions;

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
                        !(r1.hasInfiniteMass() && r2.hasInfiniteMass()) &&
                        !(r1.getBodyType() == BodyType.STATIC && r2.getBodyType() == BodyType.STATIC)) {

                    CollisionManifold result = Collisions.findCollisionFeatures(c1, c2);
                    if (result != null && result.isColliding()) {
                        for (Vector2f mpoint : result.getContactPoints()) {
                            System.out.println(mpoint);
                            System.out.println("___________________");
                        }
                        System.out.println(result.getNormal());
                        bodies1.add(r1);
                        bodies2.add(r2);
                        collisions.add(result);
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
        if (r1.getBodyType() == BodyType.STATIC && r2.getBodyType() == BodyType.STATIC) return;

        float invMass1 = r1.getInverseMass();
        float invMass2 = r2.getInverseMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0.0f) return;

        Vector2f relativeVelocity = new Vector2f(r2.getLinearVelocity()).sub(r1.getLinearVelocity());
        Vector2f relativeNormal = new Vector2f(m.getNormal()).normalize();
        if (relativeVelocity.dot(relativeNormal) > 0.0f) return;

        float e = Math.min(r1.getRestitution(), r2.getRestitution());
        float numerator = -(1.0f + e) * relativeVelocity.dot(relativeNormal);
        float j = numerator / invMassSum;

        List<Vector2f> vecMPoint1 = new ArrayList<>();
        List<Vector2f> vecMPoint2 = new ArrayList<>();
        for (Vector2f mPoint : m.getContactPoints()) {
            vecMPoint1.add(new Vector2f(mPoint).sub(r1.getPosition()));
            vecMPoint2.add(new Vector2f(mPoint).sub(r2.getPosition()));
        }

        if (!vecMPoint1.isEmpty() && j != 0.0f) {
            float friction = (r1.getFriction() + r2.getFriction())*0.5f;
            r1.setTorque(-friction*r1.getAngularVelocity());
            Vector2f impulse = new Vector2f(relativeNormal).mul(j).mul(-1.0f);
            System.out.println("Impulse: " + impulse + " vecMPoint: " + vecMPoint1.get(0));
            float angularMoment1 = 0.0f;
            int count = 0;
            for(Vector2f vecMPoint : vecMPoint1) {
                angularMoment1 += cross(vecMPoint, impulse);
                count++;
            }
            float avgAngularMoment1 = angularMoment1/count;
            //float angularMoment2 = cross(vecMPoint2.get(0), impulse);
            System.out.println("angular moment: " + angularMoment1);
            System.out.println("Contact points:" + vecMPoint1.size());
            float angularVelocity1 = (avgAngularMoment1 / r1.getInertia());
            //float angularVelocity2 = r2.getAngularVelocity() + angularMoment2/r2.getInertia();
            r1.setVelocity(new Vector2f(r1.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass1)));
            r2.setVelocity(new Vector2f(r2.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass2)).mul(-1.0f));
            r1.setAngularVelocity(angularVelocity1*4);
        }
    }

    private void positionalCorrection(Rigidbody2D r1, Rigidbody2D r2, CollisionManifold m) {
        final float percent = 0.2f;
        final float slop = 0.01f;

        float penetration = m.getPenetrationDepth();
        if (penetration <= slop) return;

        Vector2f correction = new Vector2f(m.getNormal())
                .mul(Math.max(penetration - slop, 0.0f) / (r1.getInverseMass() + r2.getInverseMass()))
                .mul(percent);

        if (!r1.hasInfiniteMass()) {
            r1.setPosition(new Vector2f(r1.getPosition()).sub(new Vector2f(correction).mul(r1.getInverseMass())));
        }
        if (!r2.hasInfiniteMass()) {
            r2.setPosition(new Vector2f(r2.getPosition()).add(new Vector2f(correction).mul(r2.getInverseMass())));
        }
    }


    public void addRigidbody(Rigidbody2D body) {
        this.rb.add(body);
        if (body.getBodyType() != BodyType.STATIC) {
            this.fr.add(body, gravity);
        }
    }


    // used for angular rotation
    public static float cross(Vector2f a, Vector2f b) {
        return a.x * b.y - a.y * b.x;
    }
    public static Vector2f cross(Vector2f v, float a) {
        return new Vector2f(-a * v.y, a * v.x);
    }
    public static Vector2f cross(float a, Vector2f v) {
        return new Vector2f(a * v.y, -a * v.x);
    }

}
