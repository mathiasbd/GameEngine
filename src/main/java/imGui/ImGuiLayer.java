package imGui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.example.GameEngineManager;
import scenes.EditorScene;
import scenes.Scene;
import util.DebugDraw;

import java.util.ArrayList;

/*
 * ImGuiLayer handles the GUI rendering for the editor interface.
 * It manages the toolbar, scene hierarchy window, and asset window for the level editor.
 * Author(s): Mathias
 */
public class ImGuiLayer {
    private ImGuiHierarchyWindow imGuiHierarchyWindow = new ImGuiHierarchyWindow();
    private ImGuiAssetWindow imGuiAssetWindow = new ImGuiAssetWindow();
    /*
     * Constructor. Initializes asset window resources.
     */
    public ImGuiLayer() {
        imGuiAssetWindow.init();
    }
    /*
     * Processes ImGui rendering each frame based on the current scene.
     * @param currentScene - the currently active Scene
     */
    public void process(Scene currentScene) {
        //Initialize toolbar height and call toolbar function
        float toolbarHeight = 30.0f;
        toolbar(currentScene, toolbarHeight);

        // SIDEBAR WINDOW
        if(currentScene.getClass() == EditorScene.class) {
            //Init position and size
            float sidebarPosX = ImGui.getMainViewport().getPosX();
            float sidebarPosY = ImGui.getMainViewport().getPosY() + toolbarHeight;
            float sidebarWidth = 200.0f;
            float sidebarHeight = ImGui.getMainViewport().getSizeY() - toolbarHeight;
            ImGui.setNextWindowPos(new ImVec2(sidebarPosX, sidebarPosY), ImGuiCond.Once);
            ImGui.setNextWindowSize(new ImVec2(sidebarWidth, sidebarHeight), ImGuiCond.Once);
            //Push window style
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);

            //Begin objects and asset window
            ImGui.begin("Objects And Assets");

            //Pop the style from stack
            ImGui.popStyleVar();

            //Make object hierarchy child
            ImGui.beginChild("ObjectHierarchy", ImGui.getContentRegionAvailX(),
                    ImGui.getContentRegionAvailY() * 0.6f, true);
            imGuiHierarchyWindow.showContent(currentScene);
            ImGui.endChild();

            //Make Asset child
            ImGui.beginChild("Assets", ImGui.getContentRegionAvailX(),
                    ImGui.getContentRegionAvailY(), true);
            imGuiAssetWindow.showContent(currentScene);
            ImGui.endChild();

            ImGui.end();
        }
    }
    /*
     * Renders the top toolbar window
     * @param currentScene - current scene
     * @param toolbarHeight - height of toolbar
     */
    private void toolbar(Scene currentScene, float toolbarHeight) {
        // TOOLBAR WINDOW
        //Init position size and style
        ImGui.setNextWindowPos(new ImVec2(ImGui.getMainViewport().getPosX(), ImGui.getMainViewport().getPosY()), ImGuiCond.Always);
        ImGui.setNextWindowSize(new ImVec2(ImGui.getMainViewport().getSizeX(), toolbarHeight), ImGuiCond.Always);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 4, 4);
        //Begin toolbar window
        ImGui.begin("Toolbar", ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoBringToFrontOnFocus);
        //Define boolean conditions for buttons
        boolean isInEditor = GameEngineManager.getCurrentSceneName().equals("EditorScene");
        boolean isInGame = GameEngineManager.getCurrentSceneName().equals("GameScene");
        //Define sizes
        float spacing = 10f;
        float buttonWidth = 60f;
        float totalWidth = buttonWidth * 2 + spacing;
        //Define where to place the "cursor"
        float cursorX = (ImGui.getContentRegionAvailX() - totalWidth) / 2.0f;
        ImGui.setCursorPosX(cursorX);
        //Create play button
        ImGuiCommonFun.button("Play", !isInGame, buttonWidth, () ->
                GameEngineManager.changeScene("GameScene", new ArrayList<>(currentScene.getGameObjects())));

        ImGui.sameLine();
        //Create stop button
        ImGuiCommonFun.button("Stop", !isInEditor, buttonWidth, () -> {
            GameEngineManager.getPhysicsSystem().reset();
            GameEngineManager.changeScene("EditorScene", new ArrayList<>());
        });
        ImGui.sameLine();
        float dbgButtonWidth = buttonWidth + 15.0f;
        String dbgLabel = "DebugDraw";
        ImGuiCommonFun.button(dbgLabel, true, dbgButtonWidth, DebugDraw::toggle);

        ImGui.end();
        ImGui.popStyleVar(3);
    }
}