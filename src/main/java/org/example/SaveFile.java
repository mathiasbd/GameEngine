package org.example;

import components.SpriteSheet;

import java.util.Map;

public class SaveFile {

    public GameObject[] gameObjects;
    public Map<String, SpriteSheet> spriteSheets;
    public SaveFile(GameObject[] gameObjects, Map<String, SpriteSheet> spriteSheets) {
        this.gameObjects = gameObjects;
        this.spriteSheets = spriteSheets;
    }
}
