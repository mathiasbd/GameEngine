package imGui;

import imgui.ImGui;
import scenes.Scene;

public class ImGuiCommonFun {

    public boolean button(String label, String hover) {
        if(ImGui.button(label)) {
            return true;
        }
        if(ImGui.isItemHovered()) {
            ImGui.setTooltip(hover);
        }
        return false;
    }
}
