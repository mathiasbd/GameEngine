package org.example;

import input.KeyboardHandler;
import input.MouseHandler;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowManager {

    private long window;
    private int width;
    private int height;
    private String title;


    public WindowManager(int width, int height, String title) {
        //Initialize variables
        this.height = height;
        this.width = width;
        this.title = title;
        //Call the window init method
        init();
    }


    public void init() {
        //Print version
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        //Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize glfw
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Request OpenGL 3.3 Core Profile: these lines are important for mac
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Required on macOS


        //While initializing the window we dont want it visible
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        //Setting the window to rezisable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Creating the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if(window==NULL) {
            throw new RuntimeException("Failed to create window");
        }

        //Setting up the mouse handler and keyboard handler
        glfwSetMouseButtonCallback(window, MouseHandler::mouseButtonCallback);
        glfwSetCursorPosCallback(window, MouseHandler::mousePositionCallback);
        glfwSetScrollCallback(window, MouseHandler::scrollCallback);
        glfwSetKeyCallback(window, KeyboardHandler::keyCallback);


        //Allocating memory to get the screen size and set the window position to the center
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }
        //Making a context for the window
        glfwMakeContextCurrent(window);

        // Load OpenGL functions important for macs
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //VSYNC i think?
        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    //Function that closes the window
    public void closeWindow() {
        glfwSetWindowShouldClose(window, true);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    //Window get method
    public long getWindow() {
        return window;
    }

}
