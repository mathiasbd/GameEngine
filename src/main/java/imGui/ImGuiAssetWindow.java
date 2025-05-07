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
    private ImGuiCommonFun imGuiCommonFun = new ImGuiCommonFun();
    private List<File> folders = new ArrayList<>();
    private List<File> regularFiles = new ArrayList<>();
    private File currentDir = null;
    private File selectedFile = null;
    private String selectedAsset = null;

    private int spriteWidth = 20;
    private int spriteHeight = 20;
    private int xSpacing = 2;
    private int ySpacing = 2;
    private int numSprites = 4;
    private int xStart = 5;

    public void initDirectory(String filepath) {
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

    public void showContent(Scene currentScene) {
        ImGui.beginChild("AssetPool", ImGui.getContentRegionAvailX(),
                (ImGui.getContentRegionMaxY()-ImGui.getWindowContentRegionMinY())*0.5f, true);
        ImGui.text("AssetPool");
        ImGui.separator();
        ImVec2 pos = ImGui.getCursorPos();
        ImVec2 size = ImGui.getContentRegionAvail();
        ImGui.dummy(size);
        if(ImGui.beginDragDropTarget()) {
            byte[] payload = ImGui.acceptDragDropPayload("spriteSheetPath");
            if(payload != null) {
                try {
                    String droppedFilePath = new String(payload);
                    if(!AssetPool.getSpriteSheets().containsKey(droppedFilePath)) {
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
        ImGui.setCursorPos(pos);
        Map<String, SpriteSheet> spriteSheets = AssetPool.getSpriteSheets();
        int loop = 0;
        for(Map.Entry<String, SpriteSheet> entry : spriteSheets.entrySet()) {
            ImGui.pushID(loop);
            int index = entry.getKey().lastIndexOf("\\");
            String name = entry.getKey().substring(index+1);
            if(ImGui.selectable(name)) {
                selectedAsset = entry.getKey();
            }
            if(ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("spriteSheet", entry.getKey().getBytes());
                ImGui.text("Dragging " + entry.getKey());
                ImGui.endDragDropSource();
            }
            ImGui.popID();
            loop++;
        }
        ImGui.endChild();
        ImGui.beginChild("FileDirectory", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());
        int flags = ImGuiTreeNodeFlags.DefaultOpen;
        if(ImGui.treeNodeEx("Assets", flags)) {
            if(ImGui.button("..")) {
                currentDir = currentDir.getParentFile();
                if(currentDir != null) {
                    initDirectory(currentDir.getPath());
                }
            }
            for(int i = 0; i <folders.size(); i++) {
                ImGui.pushID(i);
                if(ImGui.selectable("[Dir] " + folders.get(i).getName())) {
                    currentDir = folders.get(i);
                    initDirectory(folders.get(i).getPath());
                }
                ImGui.popID();
            }

            for(int j = 0; j < regularFiles.size(); j++) {
                ImGui.pushID(j);
                if(ImGui.selectable(regularFiles.get(j).getName())) {
                    selectedFile = regularFiles.get(j);
                }
                if(ImGui.beginDragDropSource()) {
                    ImGui.setDragDropPayload("spriteSheetPath", regularFiles.get(j).getAbsolutePath().getBytes());
                    ImGui.text("Dragging " + regularFiles.get(j).getName());
                    ImGui.endDragDropSource();
                }
                ImGui.popID();
            }
            ImGui.treePop();
            if(selectedFile != null) {
                if(selectedFile.getName().endsWith(".png")) {
                    ImGui.setNextWindowSize(new ImVec2(512, 128), ImGuiCond.Once);
                    ImBoolean open = new ImBoolean(true);
                    ImGui.begin("Asset picture", open);
                    Texture texture = AssetPool.getTexture(selectedFile.getPath());
                    ImGui.image(texture.getTexID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), 0, 1, 1, 0);
                    ImGui.end();
                    if(!open.get()) {
                        selectedFile = null;
                    }
                }
            }
            if(selectedAsset != null) {
                Texture texture = AssetPool.getTexture(selectedAsset);
                ImGui.setNextWindowSize(new ImVec2(texture.getWidth() + 16,
                        texture.getHeight() + 100));
                ImBoolean open = new ImBoolean(true);
                ImGui.begin("Adjust spriteSheet", open);
                ImGui.beginChild("picture", texture.getWidth(), texture.getHeight(), true);
                float imageWidth = ImGui.getContentRegionAvailX();
                float imageHeight = ImGui.getContentRegionAvailY();
                pos = ImGui.getCursorScreenPos();
                ImGui.image(texture.getTexID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), 0, 1, 1,0);
                ImGui.endChild();
                int[] imIntWidth = {spriteWidth};
                int[] imIntHeight = {spriteHeight};
                int[] imIntXSpacing = {xSpacing};
                int[] imIntYSpacing = {ySpacing};
                int[] imIntNumSprites = {numSprites};
                int[] imIntXStart = {xStart};
                ImGui.columns(2, "No borders", false);
                ImGui.setColumnWidth(0, ImGui.getWindowWidth()*0.5f);
                ImGui.setColumnWidth(1, ImGui.getWindowWidth()*0.5f);
                if(ImGuiCommonFun.slider("Sprite Width", imIntWidth, 0, 100)) {
                    this.spriteWidth = imIntWidth[0];
                }
                ImGui.nextColumn();
                if(ImGuiCommonFun.slider("Sprite Height", imIntHeight, 0, 100)) {
                    this.spriteHeight = imIntHeight[0];
                }
                ImGui.nextColumn();
                if(ImGuiCommonFun.slider("x-spacing", imIntXSpacing, 0, 100)) {
                    this.xSpacing = imIntXSpacing[0];
                }
                ImGui.nextColumn();
                if(ImGuiCommonFun.slider("y-spacing", imIntYSpacing, 0, 100)) {
                    this.ySpacing = imIntYSpacing[0];
                }
                ImGui.nextColumn();
                if(ImGuiCommonFun.slider("Num sprites", imIntNumSprites, 0, 20)) {
                    this.numSprites = imIntNumSprites[0];
                }
                ImGui.nextColumn();
                if(ImGuiCommonFun.slider("x-Start", imIntXStart, 0, 100)) {
                    this.xStart = imIntXStart[0];
                }
                ImGui.columns(1);
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
                    selectedAsset = null;
                }
            }
        }
        ImGui.endChild();
    }
}
