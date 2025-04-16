package org.example;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, invProjectionMatrix, invViewMatrix;
    private Vector2f position;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.invProjectionMatrix = new Matrix4f();
        this.invViewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f* 21.0f, 0.0f, 100.0f);
        projectionMatrix.invert(invProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f), cameraUp);
        this.viewMatrix.invert(invViewMatrix);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInvProjectionMatric() {
        return invProjectionMatrix;
    }

    public Matrix4f getInvViewMatric() {
        return invViewMatrix;
    }
}
