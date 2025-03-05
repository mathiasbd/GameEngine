package org.example;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseHandler {
    private static MouseHandler instance;
    private double xScroll, yScroll;
    private double xPos, yPos, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private MouseHandler() {
        this.xScroll = 0.0;
        this.yScroll = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseHandler get() {
        if(MouseHandler.instance == null) {
            MouseHandler.instance = new MouseHandler();
        }
        return instance;
    }

    public static void mousePositionCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];

    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
                get().isDragging = true;
            }
        } else if(action == GLFW_RELEASE) {
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void scrollCallback(long window, double xoffset, double yoffset) {
        get().xScroll = xoffset;
        get().yScroll = yoffset;
    }

    public static void endFrame() {
        get().xScroll = 0.0;
        get().yScroll = 0.0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getDx() {
        return (float)(get().lastX-get().xPos);
    }

    public static float getDy() {
        return (float)(get().lastY-get().yPos);
    }

    public static float getScrollX() {
        return (float)get().xScroll;
    }

    public static float getScrollY() {
        return (float)get().yScroll;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean isButtonDown(int button) {
        if(button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }
        return false;
    }
}
