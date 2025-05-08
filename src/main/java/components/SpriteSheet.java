package components;

import org.joml.Vector2f;
import rendering.Texture;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {
    private Texture texture;
    private List<Sprite> sprites = new ArrayList<>();
    private int spriteWidth;
    private int spriteHeight;
    private int numSprites;
    private int xSpacing;
    private int ySpacing;
    private int startX;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int xSpacing, int ySpacing, int startX) {
        this.texture = texture;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.numSprites = numSprites;
        this.xSpacing = xSpacing;
        this.ySpacing = ySpacing;
        this.startX = startX;

        int currentX = startX;
        int currentY = texture.getHeight() - spriteHeight - ySpacing; //Start at the top left corner
        for (int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] texCoords = new Vector2f[] {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };
            Sprite spr = new Sprite();
            spr.setTexCoords(texCoords);
            spr.setTexture(this.texture);
            sprites.add(spr);
            currentX += spriteWidth + xSpacing;
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + ySpacing;
            } // if its finished with a row, moves to the next row
        }
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }

    public Texture getTexture() {
        return texture;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getNumSprites() {
        return numSprites;
    }

    public int getXSpacing() {
        return xSpacing;
    }

    public int getYSpacing() {
        return ySpacing;
    }

    public int getStartX() {
        return startX;
    }
}
