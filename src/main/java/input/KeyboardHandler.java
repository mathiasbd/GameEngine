package input;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler {
    //ensures only one instance of KeyboardHandler exists
    private static KeyboardHandler instance;
    //we chose the array size  350 because GLFW supports around 350 different key codes. The array keeps track of key states
    private boolean  keyPressed[]= new boolean [350];

    private KeyboardHandler(){

    }

    public static KeyboardHandler get(){
        if (KeyboardHandler.instance == null){
            KeyboardHandler.instance = new KeyboardHandler();
        }
        return KeyboardHandler.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        // If a key is pressed, mark it as true
        if (action == GLFW_PRESS){
            get().keyPressed[key]=true;
        }
        // If a key is released, mark it as false
        else if (action == GLFW_RELEASE){
            get().keyPressed[key]=false;
        }
    }
    //Checks if a specific key is currently pressed.
    public static boolean isKeyPressed(int keyCode ){
       return get().keyPressed[keyCode];
    }


}

