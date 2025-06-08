package TestGame;
public class PlayerController extends Component {
    public float walkSpeed = 2.0f;
    public float jumpImpulse = 3.0f;

    public transient boolean isGrounded = false;
    private transient Rigidbody2D rb;

    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
    }

    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) { // Move Right
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) { // Move Left
        } else { // Idle
        }
    }
}