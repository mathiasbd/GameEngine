package imGui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import org.example.GameEngineManager;
import org.example.GameObject;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

public class ImGuiLayer {
    private Scene currentScene;
    private GameEngineManager gameEngineManager;
    private List<ImGuiCommonFun> imGuiWindows = new ArrayList<>();
    private ImVec2 contentSize;
    private int objectToEditFields = -1;

    private List<GameObject> gameObjects;

    private ImGuiHierarchyWindow imGuiHierarchyWindow = new ImGuiHierarchyWindow();
    private ImGuiAssetWindow imGuiAssetWindow = new ImGuiAssetWindow();

    public ImGuiLayer() {
        imGuiAssetWindow.initDirectory("assets");
    }
    public void process(Scene currentScene) {
        this.currentScene = currentScene;
        this.gameObjects = currentScene.getGameObjects();
        ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX(), ImGui.getMainViewport().getPosY()), ImGuiCond.Once);
        ImGui.setNextWindowSize(new ImVec2(200, ImGui.getMainViewport().getSizeY()), ImGuiCond.Once);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Objects And Assets");
        ImGui.popStyleVar();
        ImGui.beginChild("ObjectHierarchy", ImGui.getContentRegionAvailX(),
                (ImGui.getContentRegionMaxY()-ImGui.getWindowContentRegionMinY())*0.6f, true);
        imGuiHierarchyWindow.showContent(currentScene);
        ImGui.endChild();
        ImGui.beginChild("Assets", ImGui.getContentRegionAvailX(),
                ImGui.getContentRegionAvailY(), true);
        imGuiAssetWindow.showContent(currentScene);
        ImGui.endChild();
        ImGui.end();
    }
}
