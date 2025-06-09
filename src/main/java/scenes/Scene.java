package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import components.Component;
import components.SpriteRenderer;
import components.SpriteSheet;
import imGui.DragDropper;
import org.example.Camera;
import org.example.GameEngineManager;
import org.example.GameObject;
import org.example.SaveFile;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.PhysicsSystem;
import physics.rigidbody.Rigidbody2D;
import rendering.Renderer;
import serializers.ComponentSerializer;
import serializers.GameObjectSerializer;
import serializers.SpriteSheetSerializer;
import util.AssetPool;
import util.DebugDraw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected boolean dataLoaded = false;

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Renderer renderer = new Renderer();
    protected DragDropper dragDropper = new DragDropper();
    private PhysicsSystem physicsSystem;
    public Scene() {
    }

    public abstract void init(List<GameObject> gameObjects);

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
        System.out.println("Added " + go.getName() + " to scene");
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
                .registerTypeAdapter(SpriteSheet.class, new SpriteSheetSerializer())
                .create();
        SaveFile saveFile = new SaveFile(this.gameObjects.toArray(new GameObject[0]), AssetPool.getSpriteSheets());
        try {
            FileWriter file = new FileWriter("data.txt");
            file.write(gson.toJson(saveFile));
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Saved on exit");
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentSerializer())
                .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
                .registerTypeAdapter(SpriteSheet.class, new SpriteSheetSerializer())
                .create();
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("data.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isEmpty()) {
            SaveFile saveFile = gson.fromJson(inFile, SaveFile.class);
            if (saveFile.spriteSheets != null) {
                for (Map.Entry<String, SpriteSheet> entry : saveFile.spriteSheets.entrySet()) {
                    String resolvedPath = entry.getValue().getTexture().getFilepath();
                    AssetPool.addSpritesheet(resolvedPath, entry.getValue());
                }
            }
            for(GameObject go : saveFile.gameObjects) {
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
    public void addGameObject(GameObject go) {
        if (go != null && !gameObjects.contains(go)) {
            gameObjects.add(go);

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                GameEngineManager.getPhysicsSystem().addRigidbody(rb);
            }

            go.setInScene(true);
            go.start();
        }
    }

    public void removeGameObject(GameObject go) {
        if (go != null && gameObjects.contains(go)) {
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);


            gameObjects.remove(go);
            go.setInScene(false);
        }
    }
}
