package org.example;

import components.SpriteSheet;
import java.util.HashMap;
import java.util.Map;

/*
 * SaveFile stores serialized game objects and associated sprite sheets.
 * It processes sprite sheet file paths to be relative to the GameEngine root.
 * Author(s): Mathias
 */
public class SaveFile {
    public GameObject[] gameObjects;
    public Map<String, SpriteSheet> spriteSheets;

    /*
     * Constructs a SaveFile with the given game objects and sprite sheets.
     * Processes each sprite sheet's filepath to be relative to the GameEngine directory.
     * @param gameObjects - array of game objects to save
     * @param spriteSheets - map of identifier to SpriteSheet instances
     */
    public SaveFile(GameObject[] gameObjects, Map<String, SpriteSheet> spriteSheets) {
        this.gameObjects = gameObjects;
        this.spriteSheets = new HashMap<>(); // initialize map for processed sheets

        for (Map.Entry<String, SpriteSheet> entry : spriteSheets.entrySet()) {
            String key = entry.getKey();
            SpriteSheet sheet = entry.getValue();

            // derive new key path relative to GameEngine root
            String newKey = key.substring(key.indexOf("GameEngine") + 10);
            sheet.getTexture().setFilepath(newKey); // update texture filepath
            this.spriteSheets.put(newKey, sheet);  // store processed sheet
        }
    }
}
