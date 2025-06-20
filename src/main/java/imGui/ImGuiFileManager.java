package imGui;

import imgui.ImGui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/*
 * This is a helper function that lets you look through a file directory
 * Author(s): Mathias
 */
public class ImGuiFileManager {


    private List<File> folders = new ArrayList<>();
    private List<File> regularFiles = new ArrayList<>();
    private File currentDir = null;

    private String label;
    private int flags;
    private Consumer<File> onSelect;
    private String dragPayloadType;


    /*
     * The constructor for the file manager
     * @param String - The label at the top of the file directory
     * @param int - The flags/settings for the file directory
     * @param Consumer<File> - The function that is called when a file is clicked which passes the file to it
     * @param String - The drag and drop payload type if there is any drag and drop
     */
    public ImGuiFileManager(String label, int flags, Consumer<File> onSelect, String dragPayloadType) {
        this.label = label;
        this.flags = flags;
        this.onSelect = onSelect;
        this.dragPayloadType = dragPayloadType;
    }

    /*
     * The function that initializes the directory placement
     * @param String - The filepath that it should be initialized to
     */
    public void initDirectory(String filepath) {
        currentDir = new File(filepath); // Or wherever you want to start
        File[] files = currentDir.listFiles();
        folders.clear();
        regularFiles.clear();
        if(files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    folders.add(file);
                } else {
                    regularFiles.add(file);
                }
            }
        }
    }

    /*
     * The function that shows the content in the current file directory
     */
    public void showContent() {
        if(ImGui.treeNodeEx(label, flags)) {
            if (ImGui.button("..")) {
                currentDir = currentDir.getParentFile();
                if (currentDir != null) {
                    initDirectory(currentDir.getPath());
                }
            }
            for (int i = 0; i < folders.size(); i++) {
                ImGui.pushID(i);
                if (ImGui.selectable("[Dir] " + folders.get(i).getName())) {
                    currentDir = folders.get(i);
                    initDirectory(folders.get(i).getPath());
                }
                ImGui.popID();
            }
            for(int j = 0; j < regularFiles.size(); j++) {
                ImGui.pushID(j);
                if(ImGui.selectable(regularFiles.get(j).getName())) {
                    fileClicked(regularFiles.get(j));
                }
                if(!dragPayloadType.isEmpty() && ImGui.beginDragDropSource()) {
                    ImGui.setDragDropPayload(dragPayloadType, regularFiles.get(j).getAbsolutePath().getBytes());
                    ImGui.text("Dragging " + regularFiles.get(j).getName());
                    ImGui.endDragDropSource();
                }
                ImGui.popID();
            }
            ImGui.treePop();
        }
    }
    /*
     * The function that is called when a file is clicked
     * @param File - The file clicked
     */
    private void fileClicked(File f) {
        onSelect.accept(f);
    }
}
