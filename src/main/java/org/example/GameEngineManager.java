package org.example;
import imGui.ImGuiLayer;
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
    public GameEngineManager(WindowManager window) {
        this.window = window;
        changeScene("EditorScene");
        loop();
    }

    public void loop() {
        float initialTime = Time.getTime();
        float endTime;
        float deltaTime = -1.0f;
        int frames = 0, updates = 0;

        currentScene.load();

        //Loop runs until windowShouldClose is set to true
        while (!glfwWindowShouldClose(window.getWindow())) {
            //Set the clear color
            glClearColor(0.25f, 0.3f, 0.3f, 1.0f);
            //Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            if (deltaTime >= 0) {
                window.startImGuiFrame();
                currentScene.update(deltaTime);
                window.endImGuiFrame();
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
        currentScene.saveExit();
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

    public int getWindowPosX() {
        return window.getWindowPosX();
    }

    public int getWindowPosY() {
        return window.getWindowPosY();
    }

    public int getWindowWidth() {
        return window.getWidth();
    }

    public int getWindowHeight() {
        return window.getHeight();
    }
}
