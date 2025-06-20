package imGui;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.example.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics.primitives.Circle;
import physics.primitives.OBBCollider;
import physics.collisions.Rigidbody2D;
import scenes.Scene;
import util.AssetPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/*
 * ImGuiHierarchyWindow controls the top part of the left window made with ImGui.
 * It provides methods and utility that lets the user interact with game objects and components
 * Author(s): Mathias
 */
public class ImGuiHierarchyWindow {



    private int objectToEditName = -1;
    private ImString newObjectName = new ImString(64);
    private int selectedObject = -1;
    private int selectedComponent = -1;
    private int objectToEditFields = -1;


    private List<GameObject> gameObjects;
    private Scene currentScene;

    private ImGuiFileManager imGuiFileManager = new ImGuiFileManager("Scripts", ImGuiTreeNodeFlags.DefaultOpen,
            this::createComponentFromFile, "");


    private GameObject goScript = null;

    /*
     * Distributes the work to other functions
     * @param Scene - the current scene we are in
     */
    public void showContent(Scene currentScene) {
        this.currentScene = currentScene;
        //Check if data is loaded and then call functions
        if(currentScene.isDataLoaded()) {
            objectMenu(); //Menu when right clicking empty space
            showObject(); //Shows objects in the hierarchy
            //Only runs if you want to edit an object
            if(objectToEditFields!=-1 ) {
                showFields(objectToEditFields);
            }
            //Only runs if you want to add a script
            if(goScript != null) {
                addScript(goScript);
            }
        }
    }

    /*
     * This is the menu which is shown when right-clicking an empty space
     */
    private void objectMenu() {
        //Create a menu where right-clicked
        if(ImGui.beginPopupContextWindow("HierarchySettings",ImGuiPopupFlags.MouseButtonRight)) {
            //Menu item with lambda function
            ImGuiCommonFun.menuItem("Add Object", () -> currentScene.addGameObjectToScene(new GameObject("Unnamed")));
            ImGui.endPopup();
        }
    }

    /*
     * Shows each object and allows for interaction with each
     */
    private void showObject() {
        //Define variables before loop
        this.gameObjects = currentScene.getGameObjects();
        boolean treeNode;
        boolean selectedCondition;
        int flags;
        //Loop through each game object
        for(int i=0; i<gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            //Define condition for flag
            selectedCondition = selectedObject == i && objectToEditName != i;

            //Push ID
            ImGui.pushID(i);
            //Get flag and add style
            flags = addSelectedStyle(selectedCondition);
            //Add additional specific flag
            if(go.getComponents().isEmpty()) {
                flags |= ImGuiTreeNodeFlags.Leaf;
            }
            //Call the treenode
            treeNode = ImGui.treeNodeEx("##treeObject_" + i, flags);
            //Allow for drag and drop to scene
            if(ImGui.beginDragDropSource()) {
                currentScene.getDragDropper().setDragging(true);
                currentScene.getDragDropper().setDraggedObject(go);
                ImGui.text("Dragging " + go.getName());
                ImGui.endDragDropSource();
            }
            //If style was added pop it
            if(selectedCondition) {
                ImGui.popStyleColor();
            }
            //If game object is clicked define which one
            if(ImGui.isItemClicked()) {
                selectedObject = i;
                selectedComponent = -1;
            }
            //Calls interactions possible with game object
            this.objectSettings(go, i);
            ImGui.sameLine();
            //Shows name
            showObjectName(go, i);
            //Shows component if tree opened
            if(treeNode) {
                showComponent(go, i);
                ImGui.treePop();
            }

            ImGui.separator();
            ImGui.popID();
        }
    }

    /*
     * Shows the name of the object and allows for editing same place
     * @param GameObject - the game object to show name
     * @param int - the object index we are at in the loop
     */
    private void showObjectName(GameObject go, int i) {
        if(objectToEditName == i) {
            //If you are editiing the name it will be replaced by inputText
            if(ImGui.inputText("##edit_" + i, newObjectName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                currentScene.getGameObjects().get(i).setName(newObjectName.get());
                objectToEditName = -1;
            }
        } else {
            //Show regular name
            ImGui.text(go.getName());
        }
    }

    /*
     * Shows components and allows for interaction with components
     * @param GameObject - the game object to show component
     * @param int - the object index we are at in the loop
     */
    private void showComponent(GameObject go, int i) {
        //Define variables before loop
        boolean treeNode;
        boolean selectedCondition;
        int flags;
        int startIndex;
        //Loop through every component
        for(int j=0; j<go.getComponents().size(); j++) {
            //Define component, condition and index
            Component c = go.getComponents().get(j);
            startIndex = c.getClass().getName().lastIndexOf('.');
            selectedCondition = selectedComponent == j && i == selectedObject;

            ImGui.pushID(j);

            //Get the flags and style
            flags = addSelectedStyle(selectedCondition);
            flags |= ImGuiTreeNodeFlags.Leaf;

            //Init the treenode
            treeNode = ImGui.treeNodeEx(c.getClass().getName().substring(startIndex + 1), flags);

            //If the condition was true and style added then pop it from stack
            if(selectedCondition) {
                ImGui.popStyleColor();
            }

            //Call the spriteRendererFunction that implements drag and drop
            spriteRendererFunction(c);

            //If item is clicked choose it as the selected component
            if(ImGui.isItemClicked()) {
                selectedComponent = j;
            }

            //Component right click options  which have not been implemented
            if(ImGui.beginPopupContextItem("ComponentSettings", ImGuiPopupFlags.MouseButtonRight)) {
                ImGuiCommonFun.menuItem("Delete Component", () -> System.out.println("Not implemented yet"));
                ImGui.endPopup();
            }
            //Pop the treenode
            if(treeNode) {
                ImGui.treePop();
            }

            ImGui.popID();
        }
    }

    /*
     * This function adds flags and style to component and game object
     * @param boolean - the condition that triggers the selected style
     * @return int - the flag which is an integer
     */
    private int addSelectedStyle(boolean condition) {
        //Define flag
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick |
                ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.AllowItemOverlap;
        //If condition is true add extra flag and style
        if(condition) {
            flags |= ImGuiTreeNodeFlags.Selected;
            ImVec4 selectedColor = new ImVec4(0.361f, 0.478f, 0.831f, 1.0f);
            int color = ImGui.getColorU32(selectedColor);
            ImGui.pushStyleColor(ImGuiCol.Header, color);  // Apply color for this item
        }
        return flags;
    }

    /*
     * Settings when right-clicking an object
     * @param GameObject - the game object to show options for
     * @param int - the object index we are at in the loop
     */
    private void objectSettings(GameObject go, int i) {
        //If right-clicked
        if(ImGui.beginPopupContextItem("ObjectSettings" + i, ImGuiPopupFlags.MouseButtonRight)) {
            //Begin add component submenu
            if(ImGui.beginMenu("Add component")) {
                //Add spriterenderer and rigidbody component
                ImGuiCommonFun.menuItem("SpriteRenderer", () -> addSpriteRenderer(go, i));
                ImGuiCommonFun.menuItem("RigidBody2D", () -> go.addComponent(new Rigidbody2D()));

                //If rigidbody exist on game object show new menu option
                if(go.getComponent(Rigidbody2D.class)!=null) {
                    if(ImGui.beginMenu("Collider")) {
                        ImGuiCommonFun.menuItem("Square", () -> addOBBCollider(go));
                        ImGuiCommonFun.menuItem("Circle", () -> addCircle(go));
                        ImGui.endMenu();
                    }
                }
                //Add script menu item
                ImGuiCommonFun.menuItem("Add script", () -> addScript(go));
                ImGui.endMenu();
            }
            //Edit fields and edit name menu item
            ImGuiCommonFun.menuItem("Edit fields", () -> showFields(i));
            ImGuiCommonFun.menuItem("Edit name", () -> {
                newObjectName.set(go.getName());
                objectToEditName = i;
            });
            //Delete object menu option
            ImGuiCommonFun.menuItem("Delete Object", () -> deleteGameObject(i));
            ImGui.endPopup();
        }
    }

    /*
     * This function is for checking whether something is dropped on the SpriteRenderer component
     * @param Component - the component we are at in the loop
     */
    private void spriteRendererFunction(Component c){
        //If the component class is spriterenderer begin drag and drop target
        if(c.getClass() == SpriteRenderer.class) {
            if(ImGui.beginDragDropTarget()) {
                //Gets the payload check whether it is null and gets the spritesheet from asset pool
                byte[] payload = ImGui.acceptDragDropPayload("spriteSheet");
                if(payload != null) {
                    String spriteSheetName = new String(payload);
                    SpriteSheet sheet = AssetPool.getSpriteSheet(spriteSheetName);
                    //Checks if sprite is null and has texture and then sets the sprite
                    if (sheet.getSprite(0) != null && sheet.getSprite(0).getTexture() != null) {
                        ((SpriteRenderer) c).setSprite(sheet.getSprite(0));
                        ((SpriteRenderer) c).setDirty();
                        System.out.println("Sprite set from: " + spriteSheetName);
                    } else {
                        System.err.println("Failed to get sprite from: " + spriteSheetName);
                    }
                }
            }
        }
    }

    /*
     * Edit fields window which is shown when you choose to edit a game objects fields
     * @param int - The object we are at in the loop
     */
    private void showFields(int i) {
        //Define object to edit fields and init window
        objectToEditFields = i;
        ImGui.setNextWindowSize(new ImVec2(200, 500), ImGuiCond.Once);
        ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX() + ImGui.getMainViewport().getSizeX() - 200,
                ImGui.getMainViewport().getPosY()), ImGuiCond.Once);
        ImBoolean open = new ImBoolean(true);
        //Begin window and call the imgui function in the game object
        if(ImGui.begin(currentScene.getGameObjects().get(objectToEditFields).getName(), open)) {
            currentScene.getGameObjects().get(objectToEditFields).imGui();
        }
        ImGui.end();
        //If window is closed set object to edit to standard value
        if(!open.get()) {
            objectToEditFields = -1;
        }
    }

    /*
     * Function that runs when you choose add sprite renderer option
     * @param GameObject - The game object the option was chosen for
     * @param int - the index we are at in the loop
     */
    private void addSpriteRenderer(GameObject go, int i) {
        //If there is no spriteRenderer class then make a new and add it
        if(go.getComponent(SpriteRenderer.class) == null) {
            Sprite sprite = new Sprite();
            SpriteRenderer sr = new SpriteRenderer();
            sr.setColor(new Vector4f(1,1,1,1));
            sr.setSprite(sprite);
            currentScene.getGameObjects().get(i).addComponent(sr);
            //If the object is already in the scene manually add the sprite to the renderer
            if (currentScene.getGameObjects().get(i).isInScene()) {
                currentScene.getRenderer().add(go);
            }
        }
    }

    /*
     * Runs when you try to add a square collider
     * @param GameObject - The game object the option was chosen for
     */
    private void addOBBCollider(GameObject go) {
        OBBCollider OBBCollider = new OBBCollider(new Vector2f(5,5));
        OBBCollider.setRigidbody(go.getComponent(Rigidbody2D.class));
        go.addComponent(OBBCollider);
    }
    /*
     * Runs when you try to add a circle collider
     * @param GameObject - The game object the option was chosen for
     */
    private void addCircle(GameObject go) {
        Circle circle = new Circle(5);
        circle.setRigidbody(go.getComponent(Rigidbody2D.class));
        go.addComponent(circle);
    }

    /*
     * Runs when you try to delete a game object
     * @param GameObject - The game object the option was chosen for
     */
    private void deleteGameObject(int i) {
        //If the deleted object is editing fields then reset it
        if(i == objectToEditFields) {
            objectToEditFields = -1;
        }
        //If you are editing something larger than the deleted object adjust the object to edit number
        if(i < objectToEditFields) {
            objectToEditFields-=1;
        }
        currentScene.removeGameObjectFromScene(i);
    }

    /*
     * Runs when you try to add a script
     * @param GameObject - The game object the option was chosen for
     */
    private void addScript(GameObject go) {
        //If first time run then init directory and define goScript
        if(goScript == null) imGuiFileManager.initDirectory("src");
        goScript = go;
        //Init window
        float fileDirectoryWidth = 200;
        float fileDirectoryHeight = 200;
        ImGui.setNextWindowPos(new ImVec2(ImGui.getWindowPosX()+ImGui.getMainViewport().getSizeX()*0.5f,
                ImGui.getWindowPosY()+ImGui.getWindowHeight()*0.5f), ImGuiCond.Once);
        ImGui.setNextWindowSize(new ImVec2(fileDirectoryWidth, fileDirectoryHeight), ImGuiCond.Once);
        //Begin file window and show directory
        ImGui.begin("FileDirectory");
        imGuiFileManager.showContent();
        ImGui.end();
    }

    /*
     * When file in directory is chosen this function is called
     * @param File - the file that is chosen
     */
    public void createComponentFromFile(File file) {
        //Check if file is a .java file
        if(file == null || !file.getName().endsWith(".java")) {
            System.out.println("That file is not valid");
            return;
        }
        //Try the following code
        try {
            // Extract fully qualified class name from file path
            String absolutePath = file.getAbsolutePath().replace("\\", "/");
            String srcRoot = new File("src/main/java").getAbsolutePath().replace("\\", "/");
            //If the extracted path does not start with the src root then return
            if (!absolutePath.startsWith(srcRoot)) {
                System.err.println("Selected file is not inside the src directory.");
                return;
            }
            //Find relative path and replace backslash with dot and remove .java
            String relativePath = absolutePath.substring(srcRoot.length() + 1); // +1 to skip slash
            String className = relativePath.replace("/", ".").replace(".java", "");

            // Load class from className and if it extends component add it as a component
            Class<?> clazz = Class.forName(className);
            if (Component.class.isAssignableFrom(clazz)) {
                goScript.addComponent((Component) clazz.getDeclaredConstructor().newInstance());
                goScript = null;
            } else {
                System.err.println("Class does not extend Component: " + className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
