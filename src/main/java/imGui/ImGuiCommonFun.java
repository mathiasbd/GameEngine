package imGui;

import components.SpriteRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;

import java.util.ArrayList;

public class ImGuiCommonFun {

    public static void menuItem(String label, Runnable action) {
        if (ImGui.menuItem(label)) {
            action.run();
        }
    }

    public static void button(String label, boolean condition, float buttonWidth, Runnable action) {
        if (condition) {
            if (ImGui.button(label, buttonWidth, 0)) {
                action.run();
            }
        } else {
            ImGui.beginDisabled(true);
            ImGui.button(label, buttonWidth, 0);
            ImGui.endDisabled();
        }
    }

    public static void initColumn() {
        float textWidth = ImGui.getWindowWidth() * 0.4f;
        float inputWidth = ImGui.getWindowWidth() * 0.6f;
        ImGui.columns(2, "No Borders", false);
        ImGui.setColumnWidth(0, textWidth);
        ImGui.setColumnWidth(1, inputWidth);
    }

    public static int intDragger(String label, int val) {
        initColumn();
        int[] imInt = {val};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if(ImGui.dragInt("##Int",imInt)) {
            val = imInt[0];
        }
        ImGui.columns(1);
        return val;
    }

    public static float floatDragger(String label, float val, float speed) {
        initColumn();
        float[] imFloat = {val};
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if(ImGui.dragFloat("##Float",imFloat, speed)) {
            val = imFloat[0];
        }
        ImGui.columns(1);
        return val;
    }

    public static boolean checkBox(String label, boolean val) {
        initColumn();
        ImBoolean imBool = new ImBoolean(val);
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if (ImGui.checkbox("##bool", imBool)) {
            val = imBool.get();
        }
        ImGui.columns(1);
        return val;
    }

    public static int intSlider(String label, int value, int min, int max) {
        int[] imInt = {value};
        initColumn();
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if(ImGui.sliderInt("##" + label, imInt, min, max)) {
            value = imInt[0];
        }
        ImGui.columns(1);
        return value;
    }

    public static Vector2f vec2fAdder(String labelX, String labelY, float valueX, float valueY, float step) {
        initColumn();
        ImGui.text(labelX);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImFloat imFloatPosX = new ImFloat(valueX);
        if(ImGui.inputFloat("##" + labelX, imFloatPosX, step)) {
            valueX = imFloatPosX.get();
        }
        ImGui.nextColumn();
        ImGui.text(labelY);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        ImFloat imFloatPosY = new ImFloat(valueY);
        if(ImGui.inputFloat("##" + labelY, imFloatPosY, 5)) {
            valueY = imFloatPosY.get();
        }
        ImGui.columns(1);
        return new Vector2f(valueX, valueY);
    }

    public static Vector4f colorPicker(Vector4f val) {
        float[] color = {val.x,val.y,val.z, val.w};
        int flags = ImGuiColorEditFlags.NoSidePreview | ImGuiColorEditFlags.NoLabel |
                ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs;
        ImGui.pushItemWidth(ImGui.getWindowWidth()-10);
        if(ImGui.colorPicker4("##ColorPickerWidget", color, flags)) {
            val = new Vector4f(color[0], color[1], color[2], color[3]);
        }
        return val;
    }

    public static boolean slider(String label, int[] imInt, int min, int max) {
        ImGui.text(label);
        ImGui.sameLine();
        return ImGui.sliderInt("##" + label, imInt, min, max);
    }
}
