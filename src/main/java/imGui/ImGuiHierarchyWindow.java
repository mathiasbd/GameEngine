package imGui;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.example.GameEngineManager;
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

    private GameObject queedGameObject = null;
    private int objectToRemove = -1;
    private int objectToEditName = -1;
    private SpriteRenderer queedSpriteRenderer = null;
    private ImString newObjectName = new ImString(64);
    private int selectedObject = -1;
    private int selectedComponent = -1;
    private int objectToEditFields = -1;
    private boolean showEditFieldsWindow = false;

    private boolean showScriptFiles = false;

    private List<GameObject> gameObjects;
    private Scene currentScene;

    private ImGuiCommonFun imGuiCommonFun = new ImGuiCommonFun();


    private List<File> folders = new ArrayList<>();
    private List<File> regularFiles = new ArrayList<>();
    private File currentDir = null;
    private File selectedFile = null;
    private String selectedAsset = null;
    private GameObject goScript = null;

    public void showContent(Scene currentScene) {
        this.currentScene = currentScene;
        if(currentScene.isDataLoaded()) {
            objectMenu();
            showObject();
            if(objectToEditFields != -1) {
                showFields();
            }
        }
        queedProcess();
    }

    public Component createComponentFromFile(File file) {
        try {
            // Extract fully qualified class name from file path
            String absolutePath = file.getAbsolutePath().replace("\\", "/");
            String srcRoot = new File("src/main/java").getAbsolutePath().replace("\\", "/");

            if (!absolutePath.startsWith(srcRoot)) {
                System.err.println("Selected file is not inside the src directory.");
                return null;
            }

            String relativePath = absolutePath.substring(srcRoot.length() + 1); // +1 to skip slash
            String className = relativePath.replace("/", ".").replace(".java", "");

            // Load class
            Class<?> clazz = Class.forName(className);
            if (Component.class.isAssignableFrom(clazz)) {
                return (Component) clazz.getDeclaredConstructor().newInstance();
            } else {
                System.err.println("Class does not extend Component: " + className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDirectory(String filepath) {
        currentDir = new File(filepath); // Or wherever you want to start
        File[] files = currentDir.listFiles();
        folders.clear();
        regularFiles.clear();
        if(files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    folders.add(file);
                } else {
                    regularFiles.add(file);
                }
            }
        }
    }

    private void queedProcess() {
        if(queedGameObject != null) {
            currentScene.addGameObjectToScene(queedGameObject);
            queedGameObject = null;
        } else if(objectToRemove != -1) {
            if(objectToRemove == objectToEditFields) {
                objectToEditFields = -1;
            }
            if(objectToRemove < objectToEditFields) {
                objectToEditFields-=1;
            }
            currentScene.removeGameObjectFromScene(objectToRemove);
            objectToRemove = -1;
        }
        if(queedSpriteRenderer != null && selectedObject != -1) {
            System.out.println("Trying to add component");
            queedSpriteRenderer.setSprite(new Sprite());
            currentScene.getGameObjects().get(selectedObject).addComponent(queedSpriteRenderer);
            if(currentScene.getGameObjects().get(selectedObject).isInScene()) {
                currentScene.getRenderer().add(currentScene.getGameObjects().get(selectedObject));
            }
            queedSpriteRenderer = null;
        }
    }

    private void objectMenu() {
        if(ImGui.beginPopupContextWindow("HierarchySettings",ImGuiPopupFlags.MouseButtonRight)) {
            if(ImGui.menuItem("Add Object")) {
                queedGameObject = new GameObject("Unnamed");
            }
            ImGui.endPopup();
        }
    }

    private void scriptFunction(){
        if(showScriptFiles) {
            addScript();
            if(selectedFile != null && selectedFile.getName().endsWith(".java")) {
                Component co = createComponentFromFile(selectedFile);
                if(co != null) {
                    goScript.addComponent(co);
                }
                showScriptFiles = false;
                selectedFile = null;
            }
        }
    }

    private void objectSettings(GameObject go, int i) {
        if(ImGui.beginPopupContextItem("ObjectSettings" + i, ImGuiPopupFlags.MouseButtonRight)) {
            if(ImGui.beginMenu("Add component")) {
                if(ImGui.menuItem("SpriteRenderer")) {
                    if(go.getComponent(SpriteRenderer.class) == null) {
                        Sprite sprite = new Sprite();
                        queedSpriteRenderer = new SpriteRenderer();
                        queedSpriteRenderer.setColor(new Vector4f(0,0,0,1));
                        queedSpriteRenderer.setSprite(sprite);
                    }
                }
                if(ImGui.menuItem("RigidBody2D")) {
                    Rigidbody2D rigB2D = new Rigidbody2D();
                    go.addComponent(rigB2D);
                }
                if(go.getComponent(Rigidbody2D.class)!=null) {
                    if(ImGui.beginMenu("Collider")) {
                        if(ImGui.menuItem("Square")) {
                            //System.out.println("Trying to add square shape");
                            OBBCollider OBBCollider = new OBBCollider(new Vector2f(5,5));
                            OBBCollider.setRigidbody(go.getComponent(Rigidbody2D.class));
                            go.addComponent(OBBCollider);
                        }
                        if(ImGui.menuItem("Circle")) {
                            //System.out.println("Trying to add circle shape");
                            Circle circle = new Circle(5);
                            circle.setRigidbody(go.getComponent(Rigidbody2D.class));
                            go.addComponent(circle);

                        }
                        ImGui.endMenu();
                    }
                }
                if(ImGui.menuItem("Add script")) {
                    initDirectory("src");
                    goScript = go;
                    showScriptFiles = true;
                }
                ImGui.endMenu();
            }
            if(ImGui.menuItem("Edit fields")) {
                objectToEditFields = i;
                showEditFieldsWindow = true;
            }
            if(ImGui.menuItem("Edit name")) {
                newObjectName.set(go.getName());
                objectToEditName = i;
            }
            if(ImGui.menuItem("Delete Object")) {
                objectToRemove = i;
            }
            ImGui.endPopup();
        }
    }

    private void showObject() {
        this.scriptFunction();
        this.gameObjects = currentScene.getGameObjects();
        boolean treeNode;
        int flags;
        for(int i=0; i<gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            ImGui.pushID(i);
            flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick |
                        ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.AllowItemOverlap;
            if(go.getComponents().isEmpty()) {
                flags |= ImGuiTreeNodeFlags.Leaf;
            }
            if(selectedObject == i && objectToEditName != i) {
                flags |= ImGuiTreeNodeFlags.Selected;
                ImVec4 selectedColor = new ImVec4(0.361f, 0.478f, 0.831f, 1.0f);
                int color = ImGui.getColorU32(selectedColor);
                ImGui.pushStyleColor(ImGuiCol.Header, color);  // Apply color for this item
            }
            treeNode = ImGui.treeNodeEx("##treeObject_" + i, flags);
            if(ImGui.beginDragDropSource()) {
                currentScene.getDragDropper().setDragging(true);
                currentScene.getDragDropper().setDraggedObject(go);
                ImGui.text("Dragging " + go.getName());
                ImGui.endDragDropSource();
            }
            if(selectedObject == i && objectToEditName != i) {
                ImGui.popStyleColor();
            }
            if(ImGui.isItemClicked()) {
                selectedObject = i;
            }
            this.objectSettings(go, i);
            ImGui.sameLine();
            if(objectToEditName == i) {
                if(ImGui.inputText("##edit_" + i, newObjectName, ImGuiInputTextFlags.EnterReturnsTrue)) {
                    currentScene.getGameObjects().get(i).setName(newObjectName.get());
                    objectToEditName = -1;
                }
            } else {
                ImGui.text(go.getName());
            }
            if(treeNode) {
                showComponent(go, i);
                ImGui.treePop();
            }

            ImGui.separator();
            ImGui.popID();
        }
    }

    private void spriteRendererFunction(Component c){
        if(c.getClass() == SpriteRenderer.class) {
            if(ImGui.beginDragDropTarget()) {
                byte[] payload = ImGui.acceptDragDropPayload("spriteSheet");
                if(payload != null) {
                    String spriteSheetName = new String(payload);
                    SpriteSheet sheet = AssetPool.getSpriteSheet(spriteSheetName);
                    System.out.println(sheet.getTexture().getTexID() + " " + sheet.getSprite(0).getTexture().getTexID());
                    if (sheet != null && sheet.getSprite(0) != null && sheet.getSprite(0).getTexture() != null) {
                        ((SpriteRenderer) c).setSprite(sheet.getSprite(0));
                        ((SpriteRenderer) c).setDirty();
                        System.out.println("Sprite set from: " + spriteSheetName);
                    } else {
                        if(sheet == null) {
                            System.out.println("Sheet is null");
                        } else if(sheet.getSprite(0) == null) {
                            System.out.println("sprite is null");
                        } else {
                            System.out.println("texture is null");
                        }
                        System.err.println("Failed to get sprite from: " + spriteSheetName);
                    }
                }
            }
        }
    }
    private void showComponent(GameObject go, int i) {
        boolean treeNode;
        int flags;
        for(int j=0; j<go.getComponents().size(); j++) {
            Component c = go.getComponents().get(j);
            int startIndex = c.getClass().getName().lastIndexOf('.');
            ImGui.pushID(j);
            flags = ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Framed |
                    ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.Leaf;
            if(selectedComponent == i*gameObjects.size()+j && i == selectedObject) {
                flags |= ImGuiTreeNodeFlags.Selected;
                ImVec4 selectedColor = new ImVec4(0.361f, 0.478f, 0.831f, 1.0f);
                int color = ImGui.getColorU32(selectedColor);
                ImGui.pushStyleColor(ImGuiCol.Header, color);  // Apply color for this item
            }
            treeNode = ImGui.treeNodeEx(c.getClass().getName().substring(startIndex + 1), flags);
            if(selectedComponent == i*gameObjects.size()+j && i == selectedObject) {
                ImGui.popStyleColor();
            }
            this.spriteRendererFunction(c);
            if(ImGui.isItemClicked()) {
                selectedComponent = i*gameObjects.size()+j;
            }
            if(ImGui.beginPopupContextItem("ComponentSettings", ImGuiPopupFlags.MouseButtonRight)) {
                if(ImGui.menuItem("Delete component")) {
                    System.out.println("Not implemented yet");
                }
                ImGui.endPopup();
            }
            if(treeNode) {
                ImGui.treePop();
            }

            ImGui.popID();
        }
    }

    private void showFields() {
        if(objectToEditFields != -1 && showEditFieldsWindow) {
            ImGui.setNextWindowSize(new ImVec2(200, 500), ImGuiCond.Once);
            ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX() + ImGui.getMainViewport().getSizeX() - 200,
                    ImGui.getMainViewport().getPosY()), ImGuiCond.Once);
            ImBoolean open = new ImBoolean(showEditFieldsWindow);
            if(ImGui.begin(currentScene.getGameObjects().get(objectToEditFields).getName(), open)) {
                currentScene.getGameObjects().get(objectToEditFields).imGui();
            } else {
                objectToEditFields = -1;
            }
            ImGui.end();
            showEditFieldsWindow = open.get();
        }
    }


    private void addScript() {
        float fileDirectoryWidth = 200;
        float fileDirectoryHeight = 200;
        ImGui.setNextWindowPos(new ImVec2(ImGui.getWindowPosX()+ImGui.getMainViewport().getSizeX()*0.5f,
                ImGui.getWindowPosY()+ImGui.getWindowHeight()*0.5f), ImGuiCond.Once);
        ImGui.setNextWindowSize(new ImVec2(fileDirectoryWidth, fileDirectoryHeight), ImGuiCond.Once);
        ImGui.begin("FileDirectory");
        int flags = ImGuiTreeNodeFlags.DefaultOpen;
        if(ImGui.treeNodeEx("Scripts", flags)) {
            if (ImGui.button("..")) {
                currentDir = currentDir.getParentFile();
                if (currentDir != null) {
                    initDirectory(currentDir.getPath());
                }
            }
            for (int i = 0; i < folders.size(); i++) {
                ImGui.pushID(i);
                if (ImGui.selectable("[Dir] " + folders.get(i).getName())) {
                    currentDir = folders.get(i);
                    initDirectory(folders.get(i).getPath());
                }
                ImGui.popID();
            }

            for (int j = 0; j < regularFiles.size(); j++) {
                ImGui.pushID(j);
                if (ImGui.selectable(regularFiles.get(j).getName())) {
                    selectedFile = regularFiles.get(j);
                }
                ImGui.popID();
            }
            ImGui.treePop();
        }
        ImGui.end();
    }
}
