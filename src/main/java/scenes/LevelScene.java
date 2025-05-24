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

    }

    @Override
    public void update(float dt) {
    }
}
