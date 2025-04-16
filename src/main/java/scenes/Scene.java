package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import components.Component;
import components.SpriteRenderer;
import imGui.DragDropper;
import org.example.Camera;
import org.example.GameObject;
import rendering.Renderer;
import serializers.ComponentSerializer;
import serializers.GameObjectSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected boolean dataLoaded = false;

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Renderer renderer = new Renderer();
    protected DragDropper dragDropper = new DragDropper();

    public Scene() {
    }

    public abstract void init();

    public abstract void update(float dt);

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        }else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public void removeGameObjectFromScene(GameObject go) {
        gameObjects.remove(go);
    }
    public void removeGameObjectFromScene(int i) {
        GameObject go = gameObjects.get(i);
        if(go.getComponent(SpriteRenderer.class) != null && go.isInScene()) {
            renderer.removeSprite(go.getComponent(SpriteRenderer.class));
        }
        gameObjects.remove(i);
    }

    public void saveExit() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();
        try {
            FileWriter file = new FileWriter("data.txt");
            file.write(gson.toJson(this.gameObjects));
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("data.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isEmpty()) {
            GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
            for(GameObject go : gameObjects) {
                addGameObjectToScene(go);
            }
            this.dataLoaded = true;
        }



    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Camera getCamera() {
        return this.camera;
    }
    public DragDropper getDragDropper() {
        return dragDropper;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }
}
