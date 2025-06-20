package components;

import org.example.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;
/*
 * SpriteRenderer is a renderable component that handles drawing sprites for GameObjects.
 * Author(s):  Ahmed, Mathias, Gabriel, Ilias,
 */
public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;
    private transient boolean isDirty;

    private transient Transform lastTransform;

    /*
     * Default constructor, initializes dirty flag.
     */
    public SpriteRenderer() {
        this.isDirty = true;
    }
    /*
     * Copy constructor, duplicates state from another SpriteRenderer.
     * @param component - existing SpriteRenderer to copy from
     */
    public SpriteRenderer(SpriteRenderer component) {
        this.color = new Vector4f(component.color);
        this.sprite = component.sprite;
        this.isDirty = component.isDirty;
        this.lastTransform = component.lastTransform;
    }

    /*
     * Called when component starts, caches the initial transform state.
     */
    @Override
    public void start() {
        this.lastTransform = gameObject.transform.getTransform();
    }
    /*
     * Called every frame. Detects transform changes to mark the renderer dirty.
     * @param dt - delta time in seconds
     */
    @Override
    public void update(float dt) {
        if(!this.gameObject.transform.equals(this.lastTransform)) {
            this.isDirty = true;
            this.lastTransform = gameObject.transform.getTransform();
        }
    }
    /*
     *  Getters and Setters
     */
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

    public void setDirty() {
        this.isDirty = true;
    }
}
