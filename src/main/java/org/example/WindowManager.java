package org.example;

import imgui.*;
import imgui.flag.ImGuiCol;
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

import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class WindowManager {
    private long window;
    private int width;
    private int height;
    private int windowPosX = 0;
    private int windowPosY = 0;
    private String title;

    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();


    public WindowManager(int width, int height, String title) {
        //Initialize variables
        this.height = height;
        this.width = width;
        this.title = title;
        //Call the window init method
        init();
    }

    public void init() {
        initWindow();
        initImGui();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 330");
    }


    public void initWindow() {
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
        //Give moushandler info on width and height
        MouseHandler.setWindowWidth(width);
        MouseHandler.setWindowHeight(height);

        glfwSetKeyCallback(window, KeyboardHandler::keyCallback);

        //Setting up the mouse handler and keyboard handler
        glfwSetMouseButtonCallback(window, MouseHandler::mouseButtonCallback);
        glfwSetCursorPosCallback(window, MouseHandler::mousePositionCallback);
        glfwSetScrollCallback(window, MouseHandler::scrollCallback);
        glfwSetKeyCallback(window, KeyboardHandler::keyCallback);
        glfwSetWindowPosCallback(window, this::windowPosCallback);
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);
        glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            glViewport(0, 0, width, height); // Stretch framebuffer to fit screen
        });



        //Allocating memory to get the screen size and set the window position to the center
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            this.windowPosX = (vidmode.width() - pWidth.get(0)) / 2;
            this.windowPosY = (vidmode.height() - pHeight.get(0)) / 2;
            glfwSetWindowPos(window, windowPosX, windowPosY);
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
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();

        glfwSetWindowShouldClose(window, true);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        addStyle();
    }

    private void addStyle() {
        ImGuiStyle style = ImGui.getStyle();
        style.setFramePadding(new ImVec2(0, 2));
        style.setFrameRounding(2);
//        ImVec4 selectedColor = new ImVec4(0.271f, 0.361f, 0.651f, 1.0f);
//        int selectedColorU32 = ImGui.getColorU32(selectedColor);
//        ImGui.getStyle().setColor(ImGuiCol.Header, selectedColorU32);  // Set selected color
    }

    public void startImGuiFrame() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endImGuiFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // Update and Render additional Platform Windows
        // (Platform functions may change the current OpenGL context, so we save/restore it to make it easier to paste this code elsewhere.
        //  For this specific demo app we could also call glfwMakeContextCurrent(window) directly)
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }
    }

    //Window get method
    public long getWindow() {
        return window;
    }

    private void windowPosCallback(long window, int x, int y) {
        this.windowPosX = x;
        this.windowPosY = y;
    }

    private void windowSizeCallback(long window, int x, int y) {
        this.width = x;
        this.height = y;
        MouseHandler.setWindowWidth(this.width);
        MouseHandler.setWindowHeight(this.height);
        GameEngineManager.windowResize();
    }

    public int getWindowPosX() {
        return this.windowPosX;
    }

    public int getWindowPosY() {
        return this.windowPosY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
