package physics;
import org.joml.Vector2f;
import physics.forces.ForceRegistry;
import physics.forces.Gravity;
import physics.forces.Gravity;
import physics.rigidbody.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;
public class PhysicsSystem {
    private ForceRegistry fr;
    private List<Rigidbody2D> rb;
    private Gravity gravity;
    private float fixedUpdate;

    public PhysicsSystem(float fixedUpdateDt, Vector2f gravity) {
        this.fr = new ForceRegistry();
        this.rb = new ArrayList<>();
        this.gravity = new Gravity(gravity);
        this.fixedUpdate = fixedUpdateDt;
    }
    public void update(float dt) {
        fixedUpdate();
    }

    public void fixedUpdate() {
        fr.updateForces(fixedUpdate);

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
