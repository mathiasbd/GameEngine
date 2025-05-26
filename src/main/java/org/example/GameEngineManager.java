package org.example;
import imGui.ImGuiLayer;
import org.joml.Vector2f;
import physics.PhysicsSystem;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.DebugDraw;
import util.Time;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
public class GameEngineManager {

    private WindowManager window;

    private static Scene currentScene;
    private static String currentSceneName;
    private static PhysicsSystem physicsSystem;
    public GameEngineManager(WindowManager window) {
        this.window = window;
        physicsSystem = new PhysicsSystem(0.016f, new Vector2f(0.0f, -9.82f)); // 60 FPS and gravity
        changeScene("EditorScene", new ArrayList<>());
        loop();
    }

    public void loop() {
        glDisable(GL_DEPTH_TEST);
        float initialTime = Time.getTime();
        float endTime;
        float deltaTime = -1.0f;
        int frames = 0, updates = 0;

        //Loop runs until windowShouldClose is set to true
        while (!glfwWindowShouldClose(window.getWindow())) {

            DebugDraw.beginFrame();
            //Set the clear color
            glClearColor(0.25f, 0.3f, 0.3f, 1.0f);
            //Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (deltaTime >= 0) {
                window.startImGuiFrame();
                //Update the physics system
                physicsSystem.update(deltaTime);
                DebugDraw.drawLines();
                //Update the current scene
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
        if(currentScene.getClass()==LevelEditorScene.class) {
            currentScene.saveExit();
        }
    }

    public static void changeScene(String sceneName, List<GameObject> gameObjects) {
        System.out.println("Changing scene to: " + sceneName);
        switch(sceneName) {
            case "EditorScene":
                currentScene = new LevelEditorScene();
                currentScene.init(gameObjects);
                currentScene.load();
                currentScene.start();
                currentSceneName = "EditorScene";
                break;
            case "GameScene":
                currentScene.saveExit();
                currentScene = new LevelScene();
                currentScene.init(gameObjects);
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

    public static String getCurrentSceneName() {
        return currentSceneName;
    }

    public static void windowResize() {
        //getCurrentScene().getCamera().adjustProjection();
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

    public static PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }
}
