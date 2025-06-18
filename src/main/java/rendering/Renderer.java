package rendering;

import components.SpriteRenderer;
import org.example.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Renderer manages GameObjects with SpriteRenderers by adding them to RenderBatches
 * for efficient rendering.
 * Author(s): Ilias
 */
public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    /*
     * Constructs a Renderer with no initial batches.
     */
    public Renderer() {
        this.batches = new ArrayList<>();
    }

    /*
     * Adds a GameObject to rendering if it has a SpriteRenderer component.
     * @param go - GameObject to be rendered
     */
    public void add(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);

        if (sprite != null) {
            addSprite(sprite);
        }
    }

    /*
     * Attempts to add a SpriteRenderer to an existing batch or creates
     * a new RenderBatch if necessary.
     * @param sprite - SpriteRenderer to add to a batch
     */
    private void addSprite(SpriteRenderer sprite) {
        boolean added = false;

        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.getzIndex() == sprite.gameObject.getzIndex()) {
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if (!added && sprite.gameObject.isInScene()) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    /*
     * Removes a SpriteRenderer from its RenderBatch.
     * @param sprite - SpriteRenderer to remove from batches
     */
    public void removeSprite(SpriteRenderer sprite) {
        for (RenderBatch batch : batches) {
            if (batch.hasSprite(sprite)) {
                System.out.println("The sprite was found in the batch renderer");
                batch.removeSprite(sprite);
            }
        }
    }

    /*
     * Calls render on all RenderBatches managed by this Renderer.
     */
    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
