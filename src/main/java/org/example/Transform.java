package org.example;

import org.joml.Vector2f;

public class Transform {

    public Vector2f position;
    public Vector2f scale;
    public float rotation;

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

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public Vector2f getPosition() {
        return new Vector2f(this.position);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof Transform)) {return false;}
        return (((Transform) o).position.x == this.position.x && ((Transform) o).position.y == this.position.y &&
                ((Transform) o).scale.x == this.scale.x && ((Transform) o).scale.y == this.scale.y);
    }
}
