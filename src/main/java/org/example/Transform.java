package org.example;

import org.joml.Vector2f;

public class Transform {

    public Vector2f position;
    public Vector2f scale;

    public Transform() {
        this.position = new Vector2f();
        this.scale = new Vector2f();
    }

    public Transform(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f();
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform getTransform() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void getTransform(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof Transform)) {return false;}
        return ((Transform) o).position == this.position && ((Transform) o).scale == this.scale;
    }
}
