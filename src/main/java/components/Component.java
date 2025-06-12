package components;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiDragDropFlags;
import imgui.internal.flag.ImGuiTextFlags;
import imgui.type.ImBoolean;
import org.example.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    public transient GameObject gameObject = null;

    public void start() {

    }

    public abstract void update(float dt);

    public void imGui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field : fields) {
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isPrivate) {
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                if(value == null || isTransient) continue;
                String name = field.getName();
                ImGui.pushID(name);
                if(type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] color = {val.x,val.y,val.z, val.w};
                    int flags = ImGuiColorEditFlags.NoSidePreview | ImGuiColorEditFlags.NoLabel |
                            ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs;
                    ImGui.pushItemWidth(ImGui.getWindowWidth()-10);
                    if(ImGui.colorPicker4("##ColorPickerWidget", color, flags)) {
                        Vector4f newVal = new Vector4f(color[0], color[1], color[2], color[3]);
                        if(this instanceof SpriteRenderer) {
                            ((SpriteRenderer) this).setColor(newVal);
                        }
                    }
                }
                float textWidth = ImGui.getWindowWidth() * 0.4f;
                float inputWidth = ImGui.getWindowWidth() * 0.6f;
                ImGui.columns(2, "No Borders", false);
                ImGui.setColumnWidth(0, textWidth);
                ImGui.setColumnWidth(1, inputWidth);
                if(type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    float[] vec2 = {val.x, val.y};

                    ImGui.text(name);
                    ImGui.nextColumn();
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    if (ImGui.dragFloat2("##" + name, vec2)) {
                        val.x = vec2[0];
                        val.y = vec2[1];
                    }

                    ImGui.nextColumn();
                }
                if(type == int.class) {
                    int val = (int)value;
                    int[] imInt = {val};
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    ImGui.text(name);
                    ImGui.nextColumn();
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    if(ImGui.dragInt("##Int",imInt)) {
                        field.set(this, imInt[0]);
                    }
                    ImGui.nextColumn();
                }
                if(type == float.class) {
                    float val = (float)value;
                    float[] imFloat = {val};
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    ImGui.text(name);
                    ImGui.nextColumn();
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    if(ImGui.dragFloat("##Float",imFloat, 0.1f)) {
                        field.set(this, imFloat[0]);
                    }
                    ImGui.nextColumn();
                }

                if (type == boolean.class) {
                    boolean currentValue = (boolean) field.get(this);
                    ImBoolean imBool = new ImBoolean(currentValue);
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    ImGui.text(name);
                    ImGui.nextColumn();
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    if (ImGui.checkbox("##bool", imBool)) {
                        field.set(this, imBool.get());
                    }
                    ImGui.nextColumn();
                }
                ImGui.columns(1);
                if(isPrivate) {
                    field.setAccessible(false);
                }
                ImGui.popID();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
