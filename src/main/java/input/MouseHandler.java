package input;

import org.example.Camera;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/*
 * MouseHandler manages mouse input states, including position, scroll, and button presses.
 * It provides callbacks for GLFW events and utility methods to retrieve
 * mouse deltas and transformed coordinates.
 * Author(s): Ilias & Ahmed
 */
public class MouseHandler {
    private static MouseHandler instance;
    private double xScroll, yScroll;
    private double xPos, yPos, lastX, lastY;
    private boolean[] mouseButtonPressed = new boolean[3];
    private boolean isDragging;
    private int windowWidth;
    private int windowHeight;

    /*
     * Private constructor to enforce singleton pattern.
     */
    private MouseHandler() {
    }

    /*
     * GLFW callback for mouse movement: updates positions and dragging state.
     * @param window - the window that received the event
     * @param xpos - the new x-coordinate of the cursor
     * @param ypos - the new y-coordinate of the cursor
     */
    public static void mousePositionCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0]
                || get().mouseButtonPressed[1]
                || get().mouseButtonPressed[2];
    }

    /*
     * GLFW callback for mouse button events: updates button states and dragging flag.
     * @param window - the window that received the event
     * @param button - the mouse button that was pressed or released
     * @param action - GLFW_PRESS or GLFW_RELEASE
     * @param mods - modifier keys bitfield
     */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button < get().mouseButtonPressed.length) {
            if (action == GLFW_PRESS) {
                get().mouseButtonPressed[button] = true;  // mark pressed
                get().isDragging = true;
            } else if (action == GLFW_RELEASE) {
                get().mouseButtonPressed[button] = false; // mark released
                get().isDragging = false;
            }
        }
    }

    /*
     * GLFW callback for scroll events: updates scroll offsets.
     * @param window - the window that received the event
     * @param xoffset - the scroll offset along x-axis
     * @param yoffset - the scroll offset along y-axis
     */
    public static void scrollCallback(long window, double xoffset, double yoffset) {
        get().xScroll = xoffset;
        get().yScroll = yoffset;
    }

    /*
     * Resets scroll offsets and updates last cursor positions at end of frame.
     */
    public static void endFrame() {
        get().xScroll = 0.0;
        get().yScroll = 0.0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    /*
     * Transforms screen coordinates to world space X using camera matrices.
     * @param camera - the Camera with inverse projection/view matrices
     * @return world space x-coordinate
     */
    public static float getOrthoX(Camera camera) {
        float ndc = (getX() / get().windowWidth) * 2 - 1;
        Vector4f tmp = new Vector4f(ndc, 0, 0, 1);
        tmp.mul(camera.getInvProjectionMatric()).mul(camera.getInvViewMatric());
        return tmp.x;
    }

    /*
     * Transforms screen coordinates to world space Y using camera matrices.
     * @param camera - the Camera with inverse projection/view matrices
     * @return world space y-coordinate
     */
    public static float getOrthoY(Camera camera) {
        float ndc = ((get().windowHeight - getY()) / get().windowHeight) * 2 - 1;
        Vector4f tmp = new Vector4f(0, ndc, 0, 1);
        tmp.mul(camera.getInvProjectionMatric()).mul(camera.getInvViewMatric());
        return tmp.y;
    }

    /*
     * Checks if a specific mouse button is down.
     * @param button - index of the mouse button
     * @return true if pressed; false otherwise
     */
    public static boolean isButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button]; // return state
        }
        return false;
    }

    /*
     * @return current cursor x-position as float
     */
    public static float getX() {
        return (float) get().xPos;
    }

    /*
     * @return current cursor y-position as float
     */
    public static float getY() {
        return (float) get().yPos;
    }

    /*
     * Calculates cursor delta on x-axis since last frame.
     * @return delta x as float
     */
    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    /*
     * Calculates cursor delta on y-axis since last frame.
     * @return delta y as float
     */
    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    /*
     * @return scroll offset along x-axis
     */
    public static float getScrollX() {
        return (float) get().xScroll;
    }

    /*
     * @return scroll offset along y-axis
     */
    public static float getScrollY() {
        return (float) get().yScroll;
    }

    /*
     * Returns the singleton instance of MouseHandler.
     * @return the MouseHandler instance
     */
    public static MouseHandler get() {
        if (instance == null) {
            instance = new MouseHandler();
        }
        return instance;
    }

    /*
     * Sets the window width for coordinate normalization.
     * @param width - the width of the GLFW window in pixels
     */
    public static void setWindowWidth(int width) {
        get().windowWidth = width;
    }

    /*
     * Sets the window height for coordinate normalization.
     * @param height the height of the GLFW window in pixels
     */
    public static void setWindowHeight(int height) {
        get().windowHeight = height;
    }
}
