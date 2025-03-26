package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;

public class SpriteRenderer extends Component {

    private boolean firstTime = true;
    private Vector4f color;
    private Sprite sprite;


    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
    }
    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1,1,1,1);
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

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords(){
        return sprite.getTexCoords();
    }
}
