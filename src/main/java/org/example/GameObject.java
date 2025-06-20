package org.example;
import components.Component;
import imGui.ImGuiCommonFun;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
/*
 * GameObject represents an entity in the game world.
 * Each GameObject holds its own Transform, list of Components, tag, z-index, and scene state.
 * Author(s): Gabriel, Ilias, Ahmed, Mathias
 */

public class GameObject {
    private String name;
    private List<Component> components;
    private int zIndex;
    private boolean inScene;

    public Transform transform;
    public String tag;
    /*
     * Constructor with name only, creates default Transform.
     * @param name - name of the GameObject
     */
    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<Component>();
        this.transform = new Transform();
        this.zIndex = 0;
        this.inScene = true;
        this.tag = "None";
    }

    /*
     * Constructor with Transform, z-index and scene state.
     * @param name - name of the GameObject
     * @param transform - initial Transform
     * @param zIndex - rendering order
     * @param inScene - whether in scene
     */
    public GameObject(String name, Transform transform, int zIndex, boolean inScene) {
        this.name = name;
        this.components = new ArrayList<Component>();
        this.transform = transform;
        this.zIndex = zIndex;
        this.inScene = inScene;
        this.tag = "None";
    }

    /*
     * Full constructor including tag.
     * @param name - name of the GameObject
     * @param transform - initial Transform
     * @param zIndex - rendering order
     * @param inScene - whether in scene
     * @param tag - user-defined tag
     */
    public GameObject(String name, Transform transform, int zIndex, boolean inScene, String tag) {
        this.name = name;
        this.components = new ArrayList<Component>();
        this.transform = transform;
        this.zIndex = zIndex;
        this.inScene = inScene;
        this.tag = tag;
    }
    /*
     * Retrieves a component.
     * @param componentClass - class of the desired component
     * @return matching Component instance or null if not found
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component";
                }
            }
        }
        return null;
    }

    /*
     * Removes a component of the specified type.
     * @param componentClass - class of the component to remove
     */

    public <t extends Component> void removeComponent(Class <t> componentClass) {
        for (int i=0; i< components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(components.getClass())) {
                components.remove(i);
                return;
            }
        }
    }
    /*
     * Adds a new component to the GameObject.
     * @param c - component to add
     */
    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;
    }
    /*
     * Updates all components each frame.
     * @param dt - delta time in seconds
     */
    public void update(float dt) {
        for (int i=0; i< components.size(); i++) {
            components.get(i).update(dt);
        }
    }
    /*
     * Starts all components (called once on scene load).
     */
    public void start() {
        if(inScene) {
            for (int i=0; i< components.size(); i++) {
                components.get(i).start();
            }
        }
    }
    /* Getter and Setter methods */
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getzIndex() {
        return this.zIndex;
    }

    public List<Component> getComponents() {
        return components;
    }

    public boolean isInScene() {
        return inScene;
    }

    public void setInScene(boolean inScene) {
        this.inScene = inScene;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }
    /*
     * Gameobjects Equality check based on name, transform, zIndex, and components.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {return false;}
        if(!(obj instanceof GameObject)) {return false;}
        for(Component c : this.components) {
            if(((GameObject) obj).getComponent(c.getClass()) == null) {
                return false;
            }
        }
        if(this.name.equals(((GameObject) obj).getName()) &&
            this.transform == ((GameObject) obj).transform &&
            this.zIndex == ((GameObject) obj).getzIndex()) {
            return true;
        }
        return false;
    }
    /*
     * Displays GameObject properties using ImGui for debugging & editor support.
     */
    public void imGui() {
        this.zIndex = ImGuiCommonFun.intSlider("Z-Index", zIndex, -5, 5);

        if(this.transform != null) {
            transform.position = ImGuiCommonFun.vec2fAdder("Position x", "Position y", transform.position.x, transform.position.y, 5);
            transform.scale = ImGuiCommonFun.vec2fAdder("Scale x", "Scale y", transform.scale.x, transform.scale.y, 5);
        }

        for(Component c : components) {
            ImGui.separator();
            ImGui.text(c.getClass().getName().substring(c.getClass().getName().lastIndexOf('.')+1));
            c.imGui();

        }
    }
}
