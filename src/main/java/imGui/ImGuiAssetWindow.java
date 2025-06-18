package imGui;

import components.SpriteSheet;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import rendering.Texture;
import scenes.Scene;
import util.AssetPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImGuiAssetWindow {
    /*
     * ImGuiAssetWindow controls the bottom part of the left window made with ImGui.
     * It provides methods and utility that lets the user find assets and drag and
     * drop them to an asset pool section
     * Author(s): Mathias
     */

    private File previewFile = null;
    private String spriteToAdjust = null;

    private int spriteWidth = 20;
    private int spriteHeight = 20;
    private int xSpacing = 2;
    private int ySpacing = 2;
    private int numSprites = 4;
    private int xStart = 5;

    private ImGuiFileManager imGuiFileManager = new ImGuiFileManager("Assets", ImGuiTreeNodeFlags.DefaultOpen,
                                                                        this::onSelect, "spriteSheetPath");

    /*
     * Initializes the directory for the file manager
     */
    public void init() {
        imGuiFileManager.initDirectory("assets");
    }

    /*
     * This is the main function that is called from the ImGuiLayer
     * and distributes the work by calling other methods
     * @param Scene - the current scene we are in
     */
    public void showContent(Scene currentScene) {
        //Begin asset pool child window
        ImGui.beginChild("AssetPool", ImGui.getContentRegionAvailX(),
                (ImGui.getContentRegionMaxY()-ImGui.getWindowContentRegionMinY())*0.5f, true);
        ImGui.text("AssetPool");
        ImGui.separator();

        //Define the position and size of the child before placing widgets
        ImVec2 pos = ImGui.getCursorPos();
        ImVec2 size = ImGui.getContentRegionAvail();

        //Setting a dummy box so the drag and drop target is the whole box
        ImGui.dummy(size);
        dragDropTarget();

        //Set the cursor pos back where it started and get the spriteSheets
        ImGui.setCursorPos(pos);
        retrieveSpriteSheets();

        ImGui.endChild();
        //Begin the file directory child and show the content of the file viewer
        ImGui.beginChild("FileDirectory", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());
        imGuiFileManager.showContent();
        ImGui.endChild();
        //Show preview window
        renderPreviewWindow();
        //Adjust spriteSheet
        adjustSpriteSheet();
    }

    /*
     * This tells what to do when something is dragged into the asset pool box
     */
    private void dragDropTarget() {
        if(ImGui.beginDragDropTarget()) {
            byte[] payload = ImGui.acceptDragDropPayload("spriteSheetPath");
            if(payload != null) {
                try {
                    //Gets the dropped file path and checks if the asset pool already contains it
                    String droppedFilePath = new String(payload);
                    if(!AssetPool.getSpriteSheets().containsKey(droppedFilePath)) {
                        //Adds the spriteSheet to the asset pool
                        System.out.println(droppedFilePath);
                        AssetPool.addSpritesheet(droppedFilePath,
                                new SpriteSheet(AssetPool.getTexture(droppedFilePath), 80, 34, 4, 46, 94, 27));
                        System.out.println("Added " + droppedFilePath);
                    }
                    System.out.println(droppedFilePath + " already added");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ImGui.endDragDropTarget();
        }
    }

    /*
     * This shows the spriteSheets inside the asset pool and tells it
     * that it is a drag drop source that can be dragged to a game object
     */
    private void retrieveSpriteSheets() {
        //Gets the spritesheet hashmap
        Map<String, SpriteSheet> spriteSheets = AssetPool.getSpriteSheets();
        int loop = 0;
        //Loops through the hashmap and shows each
        for(Map.Entry<String, SpriteSheet> entry : spriteSheets.entrySet()) {
            ImGui.pushID(loop);
            int index = entry.getKey().lastIndexOf("\\");
            String name = entry.getKey().substring(index+1);
            if(ImGui.selectable(name)) {
                spriteToAdjust = entry.getKey();
            }
            //Begins drag and drop source which can be dragged to SpriteRenderer class in object hierarchy
            if(ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("spriteSheet", entry.getKey().getBytes());
                ImGui.text("Dragging " + entry.getKey());
                ImGui.endDragDropSource();
            }
            ImGui.popID();
            loop++;
        }
    }

    /*
     * Shows a preview of the file chosen if it is a picture
     */
    private void renderPreviewWindow() {
        if(previewFile == null) {
            return;
        }
        //Init window and show texture
        ImGui.setNextWindowSize(new ImVec2(512, 128), ImGuiCond.Once);
        ImBoolean open = new ImBoolean(true);
        ImGui.begin("Asset picture", open);
        Texture texture = AssetPool.getTexture(previewFile.getPath());
        ImGui.image(texture.getTexID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), 0, 1, 1, 0);
        ImGui.end();
        //If closed preview file is set to null
        if(!open.get()) {
            previewFile = null;
        }
    }

    /*
     * When a file is selected it is set to be previewed
     * @param File - the file that is selected
     */
    private void onSelect(File f) {
        //Checks whether the file is a picture of png or jpg
        if(f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) {
            previewFile = f;
        }
    }

    /*
     * This function shows a Spritesheet or picture chosen in the asset pool box
     * and lets the user adjust lines to get the right dimensions for the spriteSheet or picture
     */
    private void adjustSpriteSheet() {
        String selectedAsset = spriteToAdjust;
        if(selectedAsset == null) {
            return;
        }
        Texture texture = AssetPool.getTexture(selectedAsset);
        ImGui.setNextWindowSize(new ImVec2(texture.getWidth() + 16,
                texture.getHeight() + 100));
        ImBoolean open = new ImBoolean(true);
        ImGui.begin("Adjust spriteSheet", open);
        ImGui.beginChild("picture", texture.getWidth(), texture.getHeight(), true);
        float imageWidth = ImGui.getContentRegionAvailX();
        float imageHeight = ImGui.getContentRegionAvailY();
        ImVec2 pos = ImGui.getCursorScreenPos();
        ImGui.image(texture.getTexID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), 0, 1, 1,0);
        ImGui.endChild();
        ImGui.beginChild("Sliders", texture.getWidth()*0.5f, 75, false);
        spriteWidth = ImGuiCommonFun.intSlider("Sprite Width", spriteWidth, 1, 500);
        spriteHeight = ImGuiCommonFun.intSlider("Sprite Height", spriteHeight, 1, 500);
        xSpacing = ImGuiCommonFun.intSlider("x-spacing", xSpacing, 0,100);
        ImGui.endChild();
        ImGui.sameLine();
        ImGui.beginChild("Sliders2", texture.getWidth()*0.5f, 75, false);
        numSprites = ImGuiCommonFun.intSlider("Num sprites", numSprites, 1, 20);
        ySpacing = ImGuiCommonFun.intSlider("y-Start", ySpacing, 0, 100);
        xStart = ImGuiCommonFun.intSlider("x-Start", xStart, 0,100);
        ImGui.endChild();

        float scaleX = imageWidth / (float) texture.getWidth();
        float scaleY = imageHeight / (float) texture.getHeight();
        for(int i = 0; i <= numSprites ; i++) {
            float x1 = pos.x + (xStart + i*spriteWidth + i*xSpacing) * scaleX;
            float y1 = pos.y;
            float y2 = pos.y + texture.getHeight() * scaleY;
            if(i % 2 == 0) {
                ImGui.getWindowDrawList().addLine(x1, y1,x1,y2,
                        ImColor.rgb(255,0,0), 1.0f);
            } else {
                ImGui.getWindowDrawList().addLine(x1, y1,x1,y2,
                        ImColor.rgb(0,255,0), 1.0f);
            }
            if(i > 0) {
                float x2 = pos.x + (xStart + i*spriteWidth + (i-1)*xSpacing) * scaleX;
                if(i % 2 == 0) {
                    ImGui.getWindowDrawList().addLine(x2, y1,x2,y2,
                            ImColor.rgb(0,255,0), 1.0f);
                } else {
                    ImGui.getWindowDrawList().addLine(x2, y1,x2,y2,
                            ImColor.rgb(255,0,0), 1.0f);
                }

            }
        }
        ImGui.getWindowDrawList().addLine(pos.x, pos.y+ySpacing*scaleY,ImGui.getItemRectMaxX(),pos.y+ySpacing*scaleY,
                ImColor.rgb(0,0,255), 1.0f);
        ImGui.getWindowDrawList().addLine(pos.x, pos.y+(ySpacing+spriteHeight)*scaleY,ImGui.getItemRectMaxX(),pos.y+(ySpacing+spriteHeight)*scaleY,
                ImColor.rgb(0,0,255), 1.0f);

        ImGui.end();
        if(!open.get()) {
            if(AssetPool.getSpriteSheets().containsKey(selectedAsset)) {
                System.out.println(AssetPool.getTexture(selectedAsset).getTexID());
                System.out.println(selectedAsset);
                AssetPool.addSpritesheet(selectedAsset,
                        new SpriteSheet(AssetPool.getTexture(selectedAsset), spriteWidth, spriteHeight, numSprites, xSpacing, ySpacing, xStart));
                System.out.println("Added " + AssetPool.getSpriteSheet(selectedAsset).getTexture());
            }
            spriteToAdjust = null;
        }
    }
}
