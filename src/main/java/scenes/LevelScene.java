package scenes;

import input.MouseHandler;
import org.example.GameEngineManager;
import scenes.Scene;

public class LevelScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 3.0f;

    public LevelScene() {
        System.out.println("Inside the game scene");
    }

    @Override
    public void init() {
        // When play button is pressed, loop over gameobjects and add to physics
    }

    @Override
    public void update(float dt) {

        if (!changingScene && MouseHandler.isButtonDown(0)) { // Key to change scene
            changingScene = true;
            System.out.println("Changing scene");
        }

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            // Do stuff

        } else if (changingScene) {
            GameEngineManager.changeScene("EditorScene"); // Problem making it static
        }
    }
}
