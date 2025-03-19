package components;

import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private boolean firstTime = true;

    private Vector4f color;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
    }

    // this method is used to start the sprite
    @Override
    public void start() {

    }
    // this method is used to update the sprite
    @Override
    public void update(float dt) {
    }

    public Vector4f getColor() {
        return color;
    }
}
