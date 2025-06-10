package physics.forces;
import physics.collisions.Rigidbody2D;

public interface ForceGenerator {
    void updateForce(Rigidbody2D rb, float dt);
}