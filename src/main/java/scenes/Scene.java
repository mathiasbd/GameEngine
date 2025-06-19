package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import physics.collisions.Rigidbody2D;
import physics.primitives.AABBCollider;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import rendering.Renderer;
import serializers.ComponentSerializer;
import serializers.GameObjectSerializer;
import serializers.SpriteSheetSerializer;
import util.AssetPool;
import util.DTUMath;
import util.DebugDraw;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Scene is the base class for all game scenes, providing lifecycle methods
 * and utilities for GameObject management, rendering, and persistence.
 * Author(s): Ilias
 */
public abstract class Scene {
    protected Camera camera;
    private boolean isRunning = false;
    protected boolean dataLoaded = false;

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected Renderer renderer = new Renderer();
    protected DragDropper dragDropper = new DragDropper();
    private PhysicsSystem physicsSystem;

    /*
     * Constructs a Scene with default settings.
     */
    public Scene() {
    }

    /*
     * Initializes the scene with a list of GameObjects.
     * @param gameObjects - the list of GameObjects to initialize
     */
    public abstract void init(List<GameObject> gameObjects);

    /*
     * Updates the scene logic based on time elapsed.
     * @param dt - time delta since last frame in seconds
     */
    public abstract void update(float dt);

    /*
     * Starts the scene by registering all GameObjects with the renderer.
     */
    public void start() {
        for (GameObject go : gameObjects) {
            this.renderer.add(go);
        }
        isRunning = true;
    }

    /*
     * Adds a GameObject to the scene. If scene is running, starts and renders it immediately.
     * @param go - GameObject to add to the scene
     */
    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                GameEngineManager.getPhysicsSystem().addRigidbody(rb);
            }
            go.setInScene(true);
            gameObjects.add(go);
            this.renderer.add(go);
            go.start();
        }
        System.out.println("Added " + go.getName() + " to scene");
    }

    /*
     * Removes a GameObject from the scene by reference.
     * @param go - GameObject to remove
     */
    public void removeGameObjectFromScene(GameObject go) {
        gameObjects.remove(go);
    }

    /*
     * Removes a GameObject from the scene by index, detaching its sprite if present.
     * @param i - index of the GameObject in the list
     */
    public void removeGameObjectFromScene(int i) {
        GameObject go = gameObjects.get(i);
        if(go.getComponent(SpriteRenderer.class) != null && go.isInScene()) {
            renderer.removeSprite(go.getComponent(SpriteRenderer.class));
        }
        gameObjects.remove(i);
    }

    /*
     * Saves the current scene state to "data.txt" and exits.
     */
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

    /*
     * Loads scene state from "data.txt" if available, restoring GameObjects and assets.
     */
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

    /*
     * @return gameObjects - the list of GameObjects in the scene
     */
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    /*
     * @return camera - the Camera instance for this scene
     */
    public Camera getCamera() {
        return this.camera;
    }

    /*
     * @return dragDropper - the DragDropper instance for UI drag-and-drop
     */
    public DragDropper getDragDropper() {
        return dragDropper;
    }

    /*
     * @return dataLoaded - true if scene data has been loaded
     */
    public boolean isDataLoaded() {
        return dataLoaded;
    }


    /*
     * Retrieves a GameObject by its unique name.
     * @param name - unique name of the GameObject to find
     * @return GameObject instance or null if not found
     */
    public GameObject getGameObjectByName(String name) {
        for (GameObject go : gameObjects) {
            if (go.getName().equals(name)) {
                return go;
            }
        }
        return null;
    }

    /*
     * Removes a GameObject from the scene, deregistering its Rigidbody if present.
     * @param go - GameObject to remove
     */
    public void removeGameObject(GameObject go) {
        if (go != null && gameObjects.contains(go)) {
            SpriteRenderer sr = go.getComponent(SpriteRenderer.class);
            if (sr != null && go.isInScene()) {
                renderer.removeSprite(sr);
            }

            Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
            if (rb != null) {
                GameEngineManager.getPhysicsSystem().removeRigidbody(rb);
            }

            gameObjects.remove(go);
            go.setInScene(false);
        }
    }


    /*
     * @return renderer - the Renderer instance managing draw calls
     */
    public Renderer getRenderer() {
        return renderer;
    }
    public void drawCollider(Collider collider) {
        switch (collider) {
            case Circle circle -> {
                Vector2f center = circle.getCenter();
                float radius = circle.getRadius();
                Rigidbody2D rb = circle.getRigidbody();
                float rotation = rb.getRotation();

                DebugDraw.addCircle(center, radius, new Vector3f(1, 0, 0), 1);

                Vector2f endpoint = new Vector2f(0, radius);
                DTUMath.rotate(endpoint, rotation, new Vector2f());
                endpoint.add(center);

                DebugDraw.addLine2D(center, endpoint, new Vector3f(1, 0, 0), 1);
            }
            case OBBCollider obb -> {
                Vector2f center = obb.getRigidbody().getPosition();
                Vector2f dimensions = obb.getHalfSize().mul(2, new Vector2f());
                float rotation = obb.getRigidbody().getRotation();
                DebugDraw.addBox(center, dimensions, rotation, new Vector3f(1, 0, 0), 1);
            }
            case AABBCollider aabb -> {
                Vector2f center = aabb.getRigidbody().getPosition();
                Vector2f dimensions = aabb.getHalfSize().mul(2, new Vector2f());
                DebugDraw.addBox(center, dimensions, 0, new Vector3f(1, 0, 0), 1);
            }
            default -> System.err.println("Unknown collider type");
        }
    }
}
