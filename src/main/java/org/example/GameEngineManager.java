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

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;

/*
 * GameEngineManager handles the main game loop, scene management, and window events.
 * It initializes physics, runs the render/update loop, and switches scenes.
 * Author(s):
 */
public class GameEngineManager {
    private WindowManager window;
    private static Scene currentScene;
    private static String currentSceneName;
    private static PhysicsSystem physicsSystem;

    /*
     * Constructs the GameEngineManager with the given WindowManager, sets up physics,
     * loads the initial scene, and starts the game loop.
     * @param window - the WindowManager for handling window and ImGui interactions
     */
    public GameEngineManager(WindowManager window) {
        this.window = window;
        physicsSystem = new PhysicsSystem(0.048f, new Vector2f(0.0f, -9.82f)); // 60 FPS timestep and gravity
        changeScene("EditorScene", new ArrayList<>()); // load initial scene
        loop(); // start game loop
    }

    /*
     * Main game loop: processes input, updates physics and scene, and renders frames until window close.
     */
    public void loop() {
        float initialTime = Time.getTime();
        float endTime;
        float deltaTime = -1.0f;
        int frames = 0, updates = 0;

        while (!glfwWindowShouldClose(window.getWindow())) {
            DebugDraw.beginFrame();
            glClearColor(0.25f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (deltaTime >= 0) {
                window.startImGuiFrame();
                physicsSystem.update(deltaTime);  // update physics simulation
                DebugDraw.drawLines();
                currentScene.update(deltaTime); // update current scene
                window.endImGuiFrame();
                updates++;
            }

            frames++;
            glfwSwapBuffers(window.getWindow()); // present frame
            glfwPollEvents(); // handle OS events

            endTime = Time.getTime();
            deltaTime = endTime - initialTime; // compute frame time
            initialTime = endTime;
        }

        if (currentScene.getClass() == LevelEditorScene.class) {
            currentScene.saveExit(); // save changes on exit if in editor
        }
        window.closeWindow();
    }

    /*
     * Changes the active scene to the specified one, initializing and starting it.
     * @param sceneName - name identifier of the scene ("EditorScene" or "GameScene")
     * @param gameObjects - list of GameObjects to initialize in the new scene
     */
    public static void changeScene(String sceneName, List<GameObject> gameObjects) {
        System.out.println("Changing scene to: " + sceneName);
        switch (sceneName) {
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

    /*
     * Handles window resize events
     */
    public static void windowResize() {
        // getCurrentScene().getCamera().adjustProjection();
    }

    /*
     * @return the current active scene
     */
    public static Scene getCurrentScene() {
        return currentScene;
    }

    /*
     * @return the name of the current scene
     */
    public static String getCurrentSceneName() {
        return currentSceneName;
    }

    /*
     * @return the X position of the window on the screen
     */
    public int getWindowPosX() {
        return window.getWindowPosX();
    }

    /*
     * @return the Y position of the window on the screen
     */
    public int getWindowPosY() {
        return window.getWindowPosY();
    }

    /*
     * @return the window width in pixels
     */
    public int getWindowWidth() {
        return window.getWidth();
    }

    /*
     * @return the window height in pixels
     */
    public int getWindowHeight() {
        return window.getHeight();
    }

    /*
     * @return the PhysicsSystem instance used for simulation
     */
    public static PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }
}
