package physics.forces;

import physics.collisions.Rigidbody2D;

import java.util.ArrayList;
import java.util.List;

public class ForceManager {
    private List<ForceRegistration> reg;

    public ForceManager() {
        this.reg = new ArrayList<>();
    }

    public void add(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        reg.add(fr);
    }

    public void remove(Rigidbody2D rb, ForceGenerator fg) {
        ForceRegistration fr = new ForceRegistration(fg, rb);
        reg.remove(fr);
    }

    public void clearAll() {
        reg.clear();
    }

    public void updateForces(float dt) {
        for (ForceRegistration fr : reg) {
            fr.fg.updateForce(fr.rb, dt);
        }
    }

    public void zeroForces() {
        //ToDo
    }
}