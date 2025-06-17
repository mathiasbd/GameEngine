package components;

import imGui.ImGuiCommonFun;
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
                    val = ImGuiCommonFun.colorPicker(val);
                    if(this instanceof SpriteRenderer) {
                        ((SpriteRenderer) this).setColor(val);
                    }
                }

                if(type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    val = ImGuiCommonFun.vec2fAdder(name + "-x", name + "-y", val.x, val.y, 5);
                    field.set(this, val);
                }

                if(type == int.class) {
                    int val = (int)value;
                    val = ImGuiCommonFun.intDragger(name, val);
                    field.set(this, val);
                }
                if(type == float.class) {
                    float val = (float)value;
                    val = ImGuiCommonFun.floatDragger(name, val, 0.1f);
                    field.set(this, val);
                }

                if (type == boolean.class) {
                    boolean val = (boolean) value;
                    val = ImGuiCommonFun.checkBox(name, val);
                    field.set(this, val);
                }

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
