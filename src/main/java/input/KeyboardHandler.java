package input;

import static org.lwjgl.glfw.GLFW.*;

/*
 * KeyboardHandler tracks keyboard key states using GLFW callbacks.
 * Author(s): Ilias & Ahmed
 */
public class KeyboardHandler {
    private static KeyboardHandler instance;
    private boolean[] keyPressed = new boolean[350];

    // Private constructor enforces singleton pattern
    private KeyboardHandler() {
    }


    /*
     * Returns the singleton instance of KeyboardHandler.
     */
    public static KeyboardHandler get() {
        if (instance == null) {
            instance = new KeyboardHandler();
        }
        return instance;
    }

    /*
     * GLFW callback: updates keyPressed array on press/release events.
     * @param window - window handle
     * @param key - GLFW key code
     * @param scancode - platform-specific scancode
     * @param action - GLFW_PRESS or GLFW_RELEASE
     * @param mods - modifier keys bitfield
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;  // mark pressed
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false; // mark released
        }
    }

    /*
     * Checks if the specified key is currently pressed.
     * @param keyCode - GLFW key code to check
     * @return true if pressed, false otherwise
     */
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode >= get().keyPressed.length) {
            return false;
        }
        return get().keyPressed[keyCode];
    }
}
