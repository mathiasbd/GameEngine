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

    //Distributes the work to other functions
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

    //This is the menu which is shown when right-clicking an empty space
    private void objectMenu() {
        //Create a menu where right-clicked
        if(ImGui.beginPopupContextWindow("HierarchySettings",ImGuiPopupFlags.MouseButtonRight)) {
            //Menu item with lambda function
            ImGuiCommonFun.menuItem("Add Object", () -> currentScene.addGameObjectToScene(new GameObject("Unnamed")));
            ImGui.endPopup();
        }
    }
    //Shows each object and allows for interaction with each
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

    private void showObjectName(GameObject go, int i) {
        if(objectToEditName == i) {
            if(ImGui.inputText("##edit_" + i, newObjectName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                currentScene.getGameObjects().get(i).setName(newObjectName.get());
                objectToEditName = -1;
            }
        } else {
            ImGui.text(go.getName());
        }
    }

    private void showComponent(GameObject go, int i) {
        boolean treeNode;
        boolean selectedCondition;
        int flags;
        int startIndex;
        for(int j=0; j<go.getComponents().size(); j++) {

            Component c = go.getComponents().get(j);
            startIndex = c.getClass().getName().lastIndexOf('.');
            selectedCondition = selectedComponent == j && i == selectedObject;

            ImGui.pushID(j);

            flags = addSelectedStyle(selectedCondition);
            flags |= ImGuiTreeNodeFlags.Leaf;

            treeNode = ImGui.treeNodeEx(c.getClass().getName().substring(startIndex + 1), flags);

            if(selectedCondition) {
                ImGui.popStyleColor();
            }

            spriteRendererFunction(c);

            if(ImGui.isItemClicked()) {
                selectedComponent = j;
            }

            if(ImGui.beginPopupContextItem("ComponentSettings", ImGuiPopupFlags.MouseButtonRight)) {
                ImGuiCommonFun.menuItem("Delete Component", () -> System.out.println("Not implemented yet"));
                ImGui.endPopup();
            }

            if(treeNode) {
                ImGui.treePop();
            }

            ImGui.popID();
        }
    }

    private int addSelectedStyle(boolean condition) {
        int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick |
                ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.AllowItemOverlap;
        if(condition) {
            flags |= ImGuiTreeNodeFlags.Selected;
            ImVec4 selectedColor = new ImVec4(0.361f, 0.478f, 0.831f, 1.0f);
            int color = ImGui.getColorU32(selectedColor);
            ImGui.pushStyleColor(ImGuiCol.Header, color);  // Apply color for this item
        }
        return flags;
    }

    private void objectSettings(GameObject go, int i) {
        if(ImGui.beginPopupContextItem("ObjectSettings" + i, ImGuiPopupFlags.MouseButtonRight)) {
            if(ImGui.beginMenu("Add component")) {
                ImGuiCommonFun.menuItem("SpriteRenderer", () -> addSpriteRenderer(go, i));
                ImGuiCommonFun.menuItem("RigidBody2D", () -> go.addComponent(new Rigidbody2D()));

                if(go.getComponent(Rigidbody2D.class)!=null) {
                    if(ImGui.beginMenu("Collider")) {
                        ImGuiCommonFun.menuItem("Square", () -> addOBBCollider(go));
                        ImGuiCommonFun.menuItem("Circle", () -> addCircle(go));
                        ImGui.endMenu();
                    }
                }

                ImGuiCommonFun.menuItem("Add script", () -> addScript(go));
                ImGui.endMenu();
            }
            ImGuiCommonFun.menuItem("Edit fields", () -> showFields(i));
            ImGuiCommonFun.menuItem("Edit name", () -> {
                newObjectName.set(go.getName());
                objectToEditName = i;
            });
            ImGuiCommonFun.menuItem("Delete Object", () -> deleteGameObject(i));
            ImGui.endPopup();
        }
    }



    private void spriteRendererFunction(Component c){
        if(c.getClass() == SpriteRenderer.class) {
            if(ImGui.beginDragDropTarget()) {
                byte[] payload = ImGui.acceptDragDropPayload("spriteSheet");
                if(payload != null) {
                    String spriteSheetName = new String(payload);
                    SpriteSheet sheet = AssetPool.getSpriteSheet(spriteSheetName);
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

    private void showFields(int i) {
        objectToEditFields = i;
        ImGui.setNextWindowSize(new ImVec2(200, 500), ImGuiCond.Once);
        ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX() + ImGui.getMainViewport().getSizeX() - 200,
                ImGui.getMainViewport().getPosY()), ImGuiCond.Once);
        ImBoolean open = new ImBoolean(true);
        if(ImGui.begin(currentScene.getGameObjects().get(objectToEditFields).getName(), open)) {
            currentScene.getGameObjects().get(objectToEditFields).imGui();
        }
        ImGui.end();
        if(!open.get()) {
            objectToEditFields = -1;
        }
    }


    private void addSpriteRenderer(GameObject go, int i) {
        if(go.getComponent(SpriteRenderer.class) == null) {
            Sprite sprite = new Sprite();
            SpriteRenderer sr = new SpriteRenderer();
            sr.setColor(new Vector4f(0,0,0,1));
            sr.setSprite(sprite);
            currentScene.getGameObjects().get(i).addComponent(sr);
            if(currentScene.getGameObjects().get(i).isInScene()) {
                currentScene.getRenderer().add(currentScene.getGameObjects().get(selectedObject));
            }
        }
    }

    private void addOBBCollider(GameObject go) {
        OBBCollider OBBCollider = new OBBCollider(new Vector2f(5,5));
        OBBCollider.setRigidbody(go.getComponent(Rigidbody2D.class));
        go.addComponent(OBBCollider);
    }

    private void addCircle(GameObject go) {
        Circle circle = new Circle(5);
        circle.setRigidbody(go.getComponent(Rigidbody2D.class));
        go.addComponent(circle);
    }

    private void deleteGameObject(int i) {
        if(i == objectToEditFields) {
            objectToEditFields = -1;
        }
        if(i < objectToEditFields) {
            objectToEditFields-=1;
        }
        currentScene.removeGameObjectFromScene(i);
    }


    private void addScript(GameObject go) {
        if(goScript == null) imGuiFileManager.initDirectory("src");
        goScript = go;
        float fileDirectoryWidth = 200;
        float fileDirectoryHeight = 200;
        ImGui.setNextWindowPos(new ImVec2(ImGui.getWindowPosX()+ImGui.getMainViewport().getSizeX()*0.5f,
                ImGui.getWindowPosY()+ImGui.getWindowHeight()*0.5f), ImGuiCond.Once);
        ImGui.setNextWindowSize(new ImVec2(fileDirectoryWidth, fileDirectoryHeight), ImGuiCond.Once);
        ImGui.begin("FileDirectory");
        imGuiFileManager.showContent();
        ImGui.end();
    }

    public void createComponentFromFile(File file) {
        if(file == null || !file.getName().endsWith(".java")) {
            System.out.println("That file is not valid");
            return;
        }
        try {
            // Extract fully qualified class name from file path
            String absolutePath = file.getAbsolutePath().replace("\\", "/");
            String srcRoot = new File("src/main/java").getAbsolutePath().replace("\\", "/");

            if (!absolutePath.startsWith(srcRoot)) {
                System.err.println("Selected file is not inside the src directory.");
                return;
            }

            String relativePath = absolutePath.substring(srcRoot.length() + 1); // +1 to skip slash
            String className = relativePath.replace("/", ".").replace(".java", "");

            // Load class
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
