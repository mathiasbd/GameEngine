package org.example;
import components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;



    public GameObject(String Name) {
        this.name = name;
        this.components = new ArrayList<Component>();
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
        for (int i=0; i< components.size(); i++) {
            components.get(i).start();
        }
    }
}
