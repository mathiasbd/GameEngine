package components;

import org.example.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;
    private transient boolean isDirty;

    private transient Transform lastTransform;


    public SpriteRenderer() {
        this.isDirty = true;
    }

    // this method is used to start the sprite
    @Override
    public void start() {
        this.lastTransform = gameObject.transform.getTransform();
    }
    // this method is used to update the sprite
    @Override
    public void update(float dt) {
        if(!this.gameObject.transform.equals(this.lastTransform)) {
            this.isDirty = true;
        }
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

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        this.color = color;
        this.isDirty = true;
    }

    public boolean getIsDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}
