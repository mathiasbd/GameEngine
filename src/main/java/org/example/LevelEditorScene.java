package org.example;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 3.0f;


    public LevelEditorScene() {
        System.out.println("Inside the level editing scene");
    }

    @Override
    public void init() {
        //Read vertex and shader file
        //Initialize openGL and link shader
        //Write vertex array for simple triangle/rectangle
    }

    @Override
    public void update(float dt) {
        if (!changingScene && KeyboardHandler.isKeyPressed(KeyEvent.VK_SPACE)) { // Key to change scene
            changingScene = true;
            System.out.println("Changing scene");
        }

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            // Do stuff

        } else if (changingScene) {
            GameEngineManager.changeScene("GameScene"); // Problem making it static
        }
    }
}
