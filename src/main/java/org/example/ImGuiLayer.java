package org.example;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import scenes.Scene;

import java.util.List;

public class ImGuiLayer {
    private int clickedGameObject = -1;
    private List<GameObject> gameObjects;
    public void process(Scene currentScene) {
        ImGui.begin("Oralee", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("GameObjects");
        gameObjects = currentScene.getGameObjects();
        for(int i = 0; i < gameObjects.size(); i++) {
            if(ImGui.button(gameObjects.get(i).getName())) {
                clickedGameObject = i;
            }
        }
        if(clickedGameObject != -1) {
            showObjectInfo();
        }
        ImGui.end();
    }

    private void showObjectInfo() {
        ImGui.begin(gameObjects.get(clickedGameObject).getName(), ImGuiWindowFlags.AlwaysAutoResize);
        Transform transform = gameObjects.get(clickedGameObject).transform;
        ImGui.labelText("x: " + transform.position.x + " y: " + transform.position.y, "Position");
        ImGui.labelText("x: " + transform.scale.x + " y: " + transform.scale.y, "Scale");
        ImGui.end();
    }
}
