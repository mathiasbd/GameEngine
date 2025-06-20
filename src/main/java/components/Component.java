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
/*
 * Component is the base abstract class for all behaviors that can be attached to GameObjects.
 * Author(s): Gabriel, Ilias, Ahmed, Mathias
 */
public abstract class Component {

    public transient GameObject gameObject = null;
    /*
     * Called once when the component is first initialized in the scene.
     * Override for custom startup logic.
     */
    public void start() {

    }
    /*
     * Called every frame to update component behavior.
     * @param dt - delta time in seconds
     */
    public abstract void update(float dt);
    /*
     *  generates ImGui UI controls dynamically for  component's fields.
     */
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

                if (type.isEnum()) {
                    Object enumValue = field.get(this);
                    if (enumValue != null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) type;
                        Enum<?> updated = ImGuiCommonFun.enumSelector(name, (Enum<?>) enumValue, enumClass);
                        field.set(this, updated);
                    }
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
