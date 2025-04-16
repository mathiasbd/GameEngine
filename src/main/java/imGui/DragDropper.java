package imGui;

import org.example.GameObject;

public class DragDropper {
    private boolean isDragging = false;
    private GameObject draggedObject = null;

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public void setDraggedObject(GameObject draggedObject) {
        this.draggedObject = draggedObject;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public GameObject getDraggedObject() {
        return draggedObject;
    }
}
