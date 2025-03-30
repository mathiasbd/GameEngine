package org.example;
import org.lwjgl.opengl.GL;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.Time;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
public class GameEngineManager {

    private WindowManager window;

    private static Scene currentScene;
    private static String currentSceneName;
    private ImGuiLayer imGuiLayer;
    public GameEngineManager(WindowManager window) {
        this.window = window;
        this.imGuiLayer = new ImGuiLayer();
        changeScene("EditorScene");
        loop();
    }

    public void loop() {
        float initialTime = Time.getTime();
        float endTime;
        float deltaTime = -1.0f;
        int frames = 0, updates = 0;

        //Loop runs until windowShouldClose is set to true
        while (!glfwWindowShouldClose(window.getWindow())) {
            //Set the clear color
            glClearColor(0.25f, 0.3f, 0.3f, 1.0f);
            //Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            window.startImGuiFrame();
            imGuiLayer.process();
            window.endImGuiFrame();


            if (deltaTime >= 0) {
                currentScene.update(deltaTime);
                updates++;
            }

            frames++;

            //Swap the color buffers
            glfwSwapBuffers(window.getWindow());

            //Poll for window events (Key events are invoked here)
            glfwPollEvents();

            endTime = Time.getTime();
            deltaTime = endTime-initialTime;
            initialTime = endTime;
        }
    }

    public static void changeScene(String sceneName) {
        switch(sceneName) {
            case "EditorScene":
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                currentSceneName = "EditorScene";
                break;
            case "GameScene":
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                currentSceneName = "GameScene";
                break;
            default:
                System.out.println("Invalid scene name '" + sceneName + "'");
                break;
        }
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }
}
