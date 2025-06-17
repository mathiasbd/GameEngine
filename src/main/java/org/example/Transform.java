package org.example;

import org.joml.Vector2f;

/*
 * Transform represents position, scale, and rotation of an entity in 2D space.
 * Provides cloning and accessor methods for safe manipulation.
 * Author(s):
 */
public class Transform {
    public Vector2f position;
    public Vector2f scale;
    public float rotation;

    /*
     * Default constructor: initializes position and scale to zero vectors.
     */
    public Transform() {
        this.position = new Vector2f();
        this.scale = new Vector2f();
    }

    /*
     * Constructs a Transform with given position and zero scale.
     * @param position - initial position vector
     */
    public Transform(Vector2f position) {
        this.position = position;
        this.scale = new Vector2f();
    }

    /*
     * Constructs a Transform with given position and scale.
     * @param position - initial position vector
     * @param scale - initial scale vector
     */
    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    /*
     * Creates and returns a copy of this Transform.
     * @return cloned Transform with same position and scale
     */
    public Transform getTransform() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    /*
     * Copies this Transform's values into the provided Transform.
     * @param to - target Transform to receive position and scale
     */
    public void getTransform(Transform to) {
        to.position.set(this.position);  // copy position values
        to.scale.set(this.scale);        // copy scale values
    }

    /*
     * Updates this Transform's position vector.
     * @param position - new position vector
     */
    public void setPosition(Vector2f position) {
        this.position.set(position);      // safe setter
    }

    /*
     * @return a copy of this Transform's position vector
     */
    public Vector2f getPosition() {
        return new Vector2f(this.position); // return new instance to avoid aliasing
    }

    /*
     * @return current rotation angle in degrees
     */
    public float getRotation() {
        return rotation;
    }

    /*
     * Sets the rotation angle.
     * @param rotation - rotation in degrees
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /*
     * Defines equality based on position and scale components (rotation ignored).
     * @param o - other object to compare
     * @return true if positions and scales match exactly
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Transform)) return false;
        Transform t = (Transform) o;
        return t.position.x == this.position.x &&
                t.position.y == this.position.y &&
                t.scale.x    == this.scale.x    &&
                t.scale.y    == this.scale.y;
    }
}