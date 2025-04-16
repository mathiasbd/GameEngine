package components;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import org.example.GameObject;
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
                if(isPrivate) {
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();
                if(type == int.class) {
                    int val = (int)value;
                    int[] imInt = {val};
                    ImGui.text(name);
                    ImGui.sameLine();
                    if(ImGui.dragInt("",imInt)) {
                        field.set(this, imInt[0]);
                    }
                }

                if(type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] color = {val.x,val.y,val.z, val.w};
                    int flags = ImGuiColorEditFlags.NoSidePreview | ImGuiColorEditFlags.NoLabel |
                            ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.NoInputs;
                    ImGui.pushItemWidth(ImGui.getWindowWidth()-10);
                    if(ImGui.colorPicker4("##ColorPickerWidget", color, flags)) {
                        Vector4f newVal = new Vector4f(color[0], color[1], color[2], color[3]);
                        field.set(this, newVal);
                    }
                }

                if(isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
