package physics;
import org.joml.Vector2f;
import physics.forces.ForceRegistry;
import physics.forces.Gravity;
import physics.forces.Gravity;
import physics.primitives.Collider;
import physics.rigidbody.CollisionManifold;
import physics.rigidbody.Collisions;
import physics.rigidbody.Rigidbody2D;

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
        // Find any collisions
        int size = rb.size();
        for (int i=0; i < size; i++) {
            for (int j=i; j < size; j++) {
                if (i == j) continue;

                CollisionManifold result = new CollisionManifold();
                Rigidbody2D r1 = rb.get(i);
                Rigidbody2D r2 = rb.get(j);
                Collider c1 = r1.getCollider();
                Collider c2 = r2.getCollider();

                if (c1 != null && c2 != null && !(r1.hasInfiniteMass() && r2.hasInfiniteMass())) {
                    result = Collisions.findCollisionFeatures(c1, c2);
                }

                if (result != null && result.isColliding()) {
                    bodies1.add(r1);
                    bodies2.add(r2);
                    collisions.add(result);
                }
            }
        }
        fr.updateForces(fixedUpdate);

        // Resolve the collisions
        for (int i=1; i < impulseIterations; i++) {
            for (int j=0; j < collisions.size(); j++) {
                int jSize = collisions.get(j).getContactPoints().size();
                for (int k=0; k < jSize; k++) {
                    Rigidbody2D r1 = bodies1.get(j);
                    Rigidbody2D r2 = bodies2.get(j);
                    applyImpulse(r1, r2, collisions.get(j));
                }

            }
        }

        // Update the velocities of all rigidbodies
        for (int i=0; i < rb.size(); i++) {
            rb.get(i).physicsUpdate(fixedUpdate);
        }
    }

    private void applyImpulse(Rigidbody2D r1, Rigidbody2D r2, CollisionManifold m) {
        float invMass1 = r1.getInverseMass();
        float invMass2 = r2.getInverseMass();
        float invMassSum = invMass1 + invMass2;
        if (invMassSum == 0.0f) return;

        Vector2f relativeVelocity = new Vector2f(r2.getLinearVelocity()).sub(r1.getLinearVelocity());
        Vector2f relativeNormal = new Vector2f(m.getNormal()).normalize();
        if (relativeVelocity.dot(relativeNormal) > 0.0f) return; // moving away from each other

        float e = Math.min(r1.getRestitution(), r2.getRestitution()); // Not fully realistic, but gives a good result
        float numerator = -(1.0f + e) * relativeVelocity.dot(relativeNormal);
        float j = numerator / invMassSum;
        if (m.getContactPoints().size() > 0 && j != 0.0f) {
            // Apply the impulse to the contact points evenly (not realistic but gets the job done)
            Vector2f impulse = new Vector2f(relativeNormal).mul(j);
            r1.setVelocity(
                    new Vector2f(r1.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass1).mul(-1.0f)));
            r2.setVelocity(
                    new Vector2f(r2.getLinearVelocity()).add(new Vector2f(impulse).mul(invMass2)));
        }
    }

    public void addRigidbody(Rigidbody2D body) {
        this.rb.add(body);
        if (body.getHasGravity()) {
            this.fr.add(body, gravity);
        }
    }
}
