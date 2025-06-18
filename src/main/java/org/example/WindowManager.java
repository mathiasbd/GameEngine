package org.example;

import imgui.*;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import input.KeyboardHandler;
import input.MouseHandler;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
 * WindowManager handles GLFW window creation, ImGui setup, and the render loop context.
 * It provides initialization, frame lifecycle, and cleanup methods.
 * Author(s): Ahmed & Ilias & Mathias & Gabriel
 */
public class WindowManager {
    private long window;
    private int width;
    private int height;
    private int windowPosX = 0;
    private int windowPosY = 0;
    private String title;

    private ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    /*
     * Constructs the WindowManager with specified dimensions and title, and initializes subsystems.
     * @param width - the width of the GLFW window in pixels
     * @param height - the height of the GLFW window in pixels
     * @param title - the window title string
     */
    public WindowManager(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        init();
    }

    /*
     * Initializes the GLFW window and ImGui context.
     */
    public void init() {
        initWindow();
        initImGui();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 330");
    }

    /*
     * Sets up the GLFW window, OpenGL context, input callbacks, and initial OpenGL state.
     */
    public void initWindow() {
        // configure error callback to print to stderr
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW library
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // configure GLFW context settings
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);   // keep window hidden until ready
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // allow window resizing

        // create the GLFW window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        // update mouse handler with current window dimensions
        MouseHandler.setWindowWidth(width);
        MouseHandler.setWindowHeight(height);

        // register input callbacks
        glfwSetKeyCallback(window, KeyboardHandler::keyCallback);
        glfwSetMouseButtonCallback(window, MouseHandler::mouseButtonCallback);
        glfwSetCursorPosCallback(window, MouseHandler::mousePositionCallback);
        glfwSetScrollCallback(window, MouseHandler::scrollCallback);
        // window position and resize callbacks
        glfwSetWindowPosCallback(window, this::windowPosCallback);
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);
        // adjust viewport on framebuffer size changes
        glfwSetFramebufferSizeCallback(window, (win, w, h) -> glViewport(0, 0, w, h));

        // center window on screen
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            windowPosX = (vidmode.width() - pWidth.get(0)) / 2;
            windowPosY = (vidmode.height() - pHeight.get(0)) / 2;
            glfwSetWindowPos(window, windowPosX, windowPosY);
        }

        // make the OpenGL context current for this window
        glfwMakeContextCurrent(window);
        // initialize OpenGL bindings
        GL.createCapabilities();

        // enable alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // enable v-sync
        glfwSwapInterval(1);
        // display the window
        glfwShowWindow(window);
    }

    /*
     * Shuts down ImGui and destroys the GLFW window and context.
     */
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

    /*
     * Initializes the ImGui context and configures style.
     */
    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        addStyle();
    }

    /*
     * Applies custom styling to ImGui elements.
     */
    private void addStyle() {
        ImGuiStyle style = ImGui.getStyle();
        style.setFramePadding(new ImVec2(0, 2));
        style.setFrameRounding(2);
    }

    /*
     * Begins a new ImGui frame for rendering.
     */
    public void startImGuiFrame() {
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    /*
     * Renders ImGui draw data and handles multiple viewports.
     */
    public void endImGuiFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            long backupContext = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupContext); // restore context
        }
    }

    /*
     * Callback for window position changes.
     * @param window - the GLFW window handle
     * @param x - new X position of the window
     * @param y - new Y position of the window
     */
    private void windowPosCallback(long window, int x, int y) {
        this.windowPosX = x;
        this.windowPosY = y;
    }

    /*
     * Callback for window resizing events.
     * @param window - the GLFW window handle
     * @param width - new width of the window
     * @param height - new height of the window
     */
    private void windowSizeCallback(long window, int width, int height) {
        this.width = width;
        this.height = height;
        MouseHandler.setWindowWidth(width);
        MouseHandler.setWindowHeight(height);
        GameEngineManager.windowResize();
    }

    /*
     * @return the GLFW window handle
     */
    public long getWindow() {
        return window;
    }

    /*
     * @return the X position of the window on screen
     */
    public int getWindowPosX() {
        return windowPosX;
    }

    /*
     * @return the Y position of the window on screen
     */
    public int getWindowPosY() {
        return windowPosY;
    }

    /*
     * @return the width of the window in pixels
     */
    public int getWidth() {
        return width;
    }

    /*
     * @return the height of the window in pixels
     */
    public int getHeight() {
        return height;
    }
}
