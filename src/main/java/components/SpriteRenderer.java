package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;

public class SpriteRenderer extends Component {

    private boolean firstTime = true;

    private Vector4f color;
    //A list to store texture coordinates
    private Vector2f[] texCoords;
    private Texture texture;


    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.texture = null;
    }
    public SpriteRenderer(Texture texture) {
        this.color = new Vector4f(1,1,1,1);
        this.texture = texture;
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
        return this.texture;
    }

    public Vector2f[] getTexCoords(){
        Vector2f[] texCoords = {
                new Vector2f(1,1),
                new Vector2f(1,0),
                new Vector2f(0,0),
                new Vector2f(0,1)
        };
        return texCoords;
    }
}
