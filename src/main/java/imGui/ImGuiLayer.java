package imGui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.flag.ImGuiItemFlags;
import org.example.GameEngineManager;
import org.example.GameObject;
import scenes.LevelEditorScene;
import scenes.LevelScene;
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

        // TOOLBAR WINDOW
        float toolbarHeight = 30.0f;
        ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX(), ImGui.getMainViewport().getPosY()), ImGuiCond.Always);
        ImGui.setNextWindowSize(new ImVec2(ImGui.getMainViewport().getSizeX(), toolbarHeight), ImGuiCond.Always);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4, 4);
        ImGui.begin("Toolbar", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoBringToFrontOnFocus);

        boolean isInEditor = GameEngineManager.getCurrentSceneName().equals("EditorScene");
        boolean isInGame = GameEngineManager.getCurrentSceneName().equals("GameScene");

        float spacing = 10f;
        float buttonWidth = 60f;
        float totalWidth = buttonWidth * 2 + spacing;
        float cursorX = (ImGui.getContentRegionAvailX() - totalWidth) / 2.0f;
        ImGui.setCursorPosX(cursorX);

        if (!isInGame) {
            if (ImGui.button("Play", buttonWidth, 0)) {
                GameEngineManager.changeScene("GameScene", new ArrayList<>(currentScene.getGameObjects()));
            }
        } else {
            ImGui.beginDisabled(true);
            ImGui.button("Play", buttonWidth, 0);
            ImGui.endDisabled();
        }

        ImGui.sameLine();

        if (!isInEditor) {
            if (ImGui.button("Stop", buttonWidth, 0)) {
                if (isInGame) {
                    GameEngineManager.getPhysicsSystem().reset();
                    GameEngineManager.changeScene("EditorScene", new ArrayList<>());
                }
            }
        } else {
            ImGui.beginDisabled(true);
            ImGui.button("Stop", buttonWidth, 0);
            ImGui.endDisabled();
        }

        ImGui.end();
        ImGui.popStyleVar(3);

        // SIDEBAR WINDOW
        if(currentScene.getClass()== LevelEditorScene.class) {
            float sidebarPosX = ImGui.getMainViewport().getPosX();
            float sidebarPosY = ImGui.getMainViewport().getPosY() + toolbarHeight;
            float sidebarWidth = 200.0f;
            float sidebarHeight = ImGui.getMainViewport().getSizeY() - toolbarHeight;

            ImGui.setNextWindowPos(new ImVec2(sidebarPosX, sidebarPosY), ImGuiCond.Once);
            ImGui.setNextWindowSize(new ImVec2(sidebarWidth, sidebarHeight), ImGuiCond.Once);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

            ImGui.begin("Objects And Assets");

            ImGui.popStyleVar();

            ImGui.beginChild("ObjectHierarchy", ImGui.getContentRegionAvailX(),
                    ImGui.getContentRegionAvailY() * 0.6f, true);
            imGuiHierarchyWindow.showContent(currentScene);
            ImGui.endChild();

            ImGui.beginChild("Assets", ImGui.getContentRegionAvailX(),
                    ImGui.getContentRegionAvailY(), true);
            imGuiAssetWindow.showContent(currentScene);
            ImGui.endChild();

            ImGui.end();
        }


    }
}