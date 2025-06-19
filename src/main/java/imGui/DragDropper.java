package imGui;

import org.example.GameObject;

public class DragDropper {
    /*
     * A helper class for dragging and dropping from ImGui to Scene
     * Author(s): Mathias
     */
    private boolean isDragging = false;
    private GameObject draggedObject = null;

    /*
     * Sets whether the user is dragging
     * @param boolean - The boolean condition that tells whether it is dragging
     */
    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    /*
     * Sets the object draggeed
     * @param GameObject - The GameObject that is dragged
     */
    public void setDraggedObject(GameObject draggedObject) {
        this.draggedObject = draggedObject;
    }
    /*
     * Getter function that tells whether it is dragged
     * @return boolean - isDragging condition
     */
    public boolean isDragging() {
        return isDragging;
    }
    /*
     * Retrieves the dragged GameObject
     * @return GameObject - The dragged GameObject
     */
    public GameObject getDraggedObject() {
        return draggedObject;
    }
}
