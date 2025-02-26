package org.example;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

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

        double initialTime = glfwGetTime();
        double timer = initialTime;
        double limitFPS = 1.0 / 60.0;
        double deltaTime = 0.0;
        int frames = 0, updates = 0;
        double currentTime;
        double elapsed;

        //Loop runs until windowShouldClose is set to true
        while (!glfwWindowShouldClose(window.getWindow())) {
            //Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            //Swap the color buffers
            glfwSwapBuffers(window.getWindow());

            currentTime = glfwGetTime();
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


            if (glfwGetTime() - timer > 1) {
                timer += 1.0;
                System.out.println("FPS: " + frames + " ticks: " + updates);
                frames = 0;
                updates = 0;
            }

        }
    }
}
