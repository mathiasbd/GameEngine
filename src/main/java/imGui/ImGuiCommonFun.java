package imGui;

import components.SpriteRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;

import java.util.ArrayList;
/*
 * This class helps reduce code by creating functions that generate a widget for the UI
 * Author(s): Mathias
 */
public class ImGuiCommonFun {



    /*
     * Creates an item for a menu
     * @param String - The label that is show on the menu item
     * @param Runnable - A method or lambda function that is run when the item is clicked
     */
    public static void menuItem(String label, Runnable action) {
        if (ImGui.menuItem(label)) {
            action.run();
        }
    }

    /*
     * Creates a button
     * @param String - The label that is shown on the button
     * @param boolean - The condition for the button to not be disabled
     * @param float - The button width
     * @param Runnable - The method or lambda function that is run when the button is clicked
     */
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

    /*
     * This function initializes the standard column format which is used in many widgets
     */
    public static void initColumn() {
        float textWidth = ImGui.getWindowWidth() * 0.4f;
        float inputWidth = ImGui.getWindowWidth() * 0.6f;
        ImGui.columns(2, "No Borders", false);
        ImGui.setColumnWidth(0, textWidth);
        ImGui.setColumnWidth(1, inputWidth);
    }

    /*
     * A dragger for integer
     * @param String - The label shown to the left of the slider
     * @param int - The initial value of the slider
     * @return int - The value of the int dragger
     */
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

    /*
     * A dragger for float
     * @param String - The label shown to the left of the slider
     * @param float - The initial balue of the slider
     * @param float - The speed of increment of the dragger
     * @return float - The value of the float dragger
     */
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

    /*
     * A checkbox
     * @param String - The label shown to the left of the checkbox
     * @param boolean - The initial value of the checkbox
     * @return boolean - The value of the checkbox
     */
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

    /*
     * An integer slider
     * @param String - The initial label shown to the left of the slider
     * @param int - The initial value of the slider
     * @param int - The minimum value of the slider
     * @param int - The maximum value of the slider
     * @return int - The value of the slider
     */
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

    public static int intInput(String label, int value, int step) {
        ImInt imInt = new ImInt(value);
        initColumn();
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if(ImGui.inputInt("##" + label, imInt, step)) {
            value = imInt.get();
        }
        ImGui.columns(1);
        return value;
    }

    /*
     * A widget where you can add to each part of a vector 2f
     * @param String - The label to the left of the x part of the vector
     * @param String - The label to the left of the y part of the vector
     * @param float - The initial value of the x part of the vector
     * @param float - The initial value of the y part of the vector
     * @param float - The increment of each addition or subtraction
     * @return Vector2f - The value of the vector
     */
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

    /*
     * The color picker widget for the SpriteRenderer
     * @param Vector4f - The initial value of the vector 4f
     * @return Vector4f - The final value of the vector 4f
     */
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

    public static Enum<?> enumSelector(String label, Enum<?> currentValue, Class<? extends Enum<?>> enumClass) {
        initColumn();
        ImGui.text(label);
        ImGui.nextColumn();
        ImGui.beginGroup();

        Enum<?>[] constants = enumClass.getEnumConstants();
        Enum<?> selected = currentValue;

        for (Enum<?> constant : constants) {
            boolean isSelected = constant.equals(currentValue);
            ImBoolean selectedWrapper = new ImBoolean(isSelected);

            if (ImGui.checkbox(constant.name(), selectedWrapper)) {
                if (selectedWrapper.get()) {
                    selected = constant;
                }
            }
        }

        ImGui.endGroup();
        ImGui.columns(1);
        return selected;
    }
}
