package rendering;

import components.SpriteRenderer;
import org.example.GameObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);

        if(sprite != null) {
            addSprite(sprite);
        }
    }

    private void addSprite(SpriteRenderer sprite) {
        boolean added = false;

        for(RenderBatch batch : batches) {
            if(batch.hasRoom() && batch.getzIndex() == sprite.gameObject.getzIndex()) {
                Texture tex = sprite.getTexture();
                if(tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if(!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        for(RenderBatch batch : batches) {
            batch.render();
        }
    }
}
