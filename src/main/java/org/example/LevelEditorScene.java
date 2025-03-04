package org.example;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 3.0f;

    public LevelEditorScene() {
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

        }
        else if (changingScene) {
            // WindowManager.changeScene("EditorScene"); // Problem making it static
        }
    }
}
