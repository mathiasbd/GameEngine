package org.example;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
public class GameEngineManager {

    WindowManager window;
    public GameEngineManager(WindowManager window) {
        this.window = window;
        loop();
    }

    public void loop() {
        //creates the GLCapabilities instance and makes the openGL bindings available for use
        GL.createCapabilities();
        //Set the clear color
        glClearColor(1.0f, 0.0f, 0.3f, 1.0f);

        float initialTime = Time.getTime();
        float timer = initialTime;

        float limitFPS = 1.0f / 60.0f;
        float deltaTime = 0;

        int frames = 0, updates = 0;
        float currentTime;
        float elapsed;

        //Loop runs until windowShouldClose is set to true
        while (!glfwWindowShouldClose(window.getWindow())) {
            //Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            //Swap the color buffers
            glfwSwapBuffers(window.getWindow());

            currentTime = Time.getTime();
            elapsed = (currentTime - initialTime);
            initialTime = currentTime;

            deltaTime += elapsed / limitFPS;

            while (deltaTime >= 1) {
                //Poll for window events (Key events are invoked here)
                glfwPollEvents();

                updates++;
                deltaTime--;
            }

            frames++;


            if (Time.getTime() - timer > 1.0) {
                timer += 1.0;
                System.out.println("FPS: " + frames + " ticks: " + updates);
                frames = 0;
                updates = 0;
            }

        }
    }
}
