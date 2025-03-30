package org.example;

import imgui.ImGui;

public class ImGuiLayer {
    private boolean showText = false;
    public void process() {
        ImGui.begin("Oralee");
        if(ImGui.button("This is a test button")) {
            showText = !showText;
        }
        if(showText) {
            ImGui.labelText("Wow it works", "Wow it works2");
        }
        ImGui.end();
    }
}
