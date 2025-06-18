package org.example;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/*
 * Camera encapsulates projection and view matrices for rendering.
 * It supports orthographic projection and updates view matrix based on position.
 * Author(s): Ilias
 */
public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, invProjectionMatrix, invViewMatrix;
    private Vector2f position;

    /*
     * Constructs a Camera at the given position and initializes matrices.
     * @param position - 2D position of the camera in world space
     */
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.invProjectionMatrix = new Matrix4f();
        this.invViewMatrix = new Matrix4f();
        adjustProjection();
    }

    /*
     * Sets up an orthographic projection and computes its inverse.
     */
    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(
                0.0f, 32.0f * 40.0f,  // left, right bounds
                0.0f, 32.0f * 21.0f,  // bottom, top bounds
                0.0f, 100.0f         // near, far planes
        );
        projectionMatrix.invert(invProjectionMatrix); // compute inverse for unprojection
    }

    /*
     * Computes the view matrix based on camera position and direction, and its inverse.
     * @return the view matrix
     */
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp
        );
        viewMatrix.invert(invViewMatrix); // inverse view for unprojection
        return viewMatrix;
    }

    /*
     * @return the current projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /*
     * @return the inverse of the projection matrix
     */
    public Matrix4f getInvProjectionMatric() {
        return invProjectionMatrix;
    }

    /*
     * @return the inverse of the view matrix
     */
    public Matrix4f getInvViewMatric() {
        return invViewMatrix;
    }
}
