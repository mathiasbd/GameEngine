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

    private List<Rigidbody2D> rb;
    private List<Rigidbody2D> bodies1;
    private List<Rigidbody2D> bodies2;
    private List<CollisionManifold> collisions;

    private Gravity gravity;
    private float fixedUpdate;
    private int impulseIterations = 6;

    public PhysicsSystem(float fixedUpdateDt, Vector2f gravity) {
        this.fr = new ForceRegistry();
        this.rb = new ArrayList<>();
        this.bodies1 = new ArrayList<>();
        this.bodies2 = new ArrayList<>();
        this.gravity = new Gravity(gravity);
        this.fixedUpdate = fixedUpdateDt;
    }
    public void update(float dt) {
        fixedUpdate();
    }

    public void fixedUpdate() {
        fr.updateForces(fixedUpdate);
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
                ;

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

        // Update the velocities of all rigidbodies
        for (int i=0; i < rb.size(); i++) {
            rb.get(i).physicsUpdate(fixedUpdate);
        }
    }

    public void addRigidbody(Rigidbody2D body) {
        this.rb.add(body);
        this.fr.add(body, gravity);
    }

}
