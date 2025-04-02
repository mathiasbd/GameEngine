package components;

import org.joml.Vector2f;
import rendering.Texture;

public class Sprite {

    private Texture texture;
    private Vector2f[] texCoords = new Vector2f[] {
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector2f(0, 0),
                    new Vector2f(0, 1)
    };

    public Sprite() {
        this.texture = null;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }
}
