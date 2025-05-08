package org.example;

import components.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class SaveFile {

    public GameObject[] gameObjects;
    public Map<String, SpriteSheet> spriteSheets;
    public SaveFile(GameObject[] gameObjects, Map<String, SpriteSheet> spriteSheets) {
        this.gameObjects = gameObjects;
        for (Map.Entry<String, SpriteSheet> entry : spriteSheets.entrySet()) {
            String key = entry.getKey();
            SpriteSheet value = entry.getValue();

            //Cut key string so it begins with GameEngine
            String newKey = key.substring(key.indexOf("GameEngine") + 10);
            value.getTexture().setFilepath(newKey);
            this.spriteSheets = new HashMap<String, SpriteSheet>();
            this.spriteSheets.put(newKey, value);
        }
    }
}
