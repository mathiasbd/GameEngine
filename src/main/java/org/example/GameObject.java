package org.example;
import components.Component;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    private int zIndex;
    private boolean inScene;

    public Transform transform;



    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<Component>();
        this.transform = new Transform();
        this.zIndex = 0;
        this.inScene = false;
    }

    public GameObject(String name, Transform transform, int zIndex, boolean inScene) {
        this.name = name;
        this.components = new ArrayList<Component>();
        this.transform = transform;
        this.zIndex = zIndex;
        this.inScene = inScene;
    }

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

    public <t extends Component> void removeComponent(Class <t> componentClass) {
        for (int i=0; i< components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(components.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i=0; i< components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start() {
        if(inScene) {
            for (int i=0; i< components.size(); i++) {
                components.get(i).start();
            }
        }
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
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

    public void imGui() {
        int[] imInt = {this.zIndex};
        float textWidth = ImGui.getWindowWidth() * 0.4f;
        float inputWidth = ImGui.getWindowWidth() * 0.6f;
        ImGui.columns(2, "No Borders", false);
        ImGui.setColumnWidth(0, textWidth);
        ImGui.setColumnWidth(1, inputWidth);
        ImGui.text("Z-index");
        ImGui.nextColumn();
        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
        if(ImGui.sliderInt("##zIndex", imInt, -5, 5)) {
            this.zIndex = imInt[0];
        }
        ImGui.nextColumn();
        if(this.transform != null) {
            ImGui.text("Position x");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImFloat imFloatPosX = new ImFloat(transform.position.x);
            if(ImGui.inputFloat("##posX", imFloatPosX, 5)) {
                transform.position.x = imFloatPosX.get();
            }
            ImGui.nextColumn();
            ImGui.text("position y");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImFloat imFloatPosY = new ImFloat(transform.position.y);
            if(ImGui.inputFloat("##posY", imFloatPosY, 5)) {
                transform.position.y = imFloatPosY.get();
            }
            ImGui.nextColumn();
            ImGui.text("Scale x");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImFloat imFloatScaleX = new ImFloat(transform.scale.x);
            if(ImGui.inputFloat("##ScaleX", imFloatScaleX, 5)) {
                transform.scale.x = imFloatScaleX.get();
            }
            ImGui.nextColumn();
            ImGui.text("Scale y");
            ImGui.nextColumn();
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImFloat imFloatScaleY = new ImFloat(transform.scale.y);
            if(ImGui.inputFloat("##scaleY", imFloatScaleY, 5)) {
                transform.scale.y = imFloatScaleY.get();
            }
        }

        ImGui.columns(1);
        for(Component c : components) {
            c.imGui();
        }
    }
}
