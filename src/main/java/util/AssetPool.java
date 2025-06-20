package util;

import components.SpriteSheet;
import rendering.Shader;
import rendering.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/*
 * AssetPool manages shared resources (shaders, textures, sprite sheets) to avoid reloading assets.
 * It ensures that each asset is loaded only once and reused across the game engine.
 * Author(s): Mathias
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new LinkedHashMap<>();
    /*
     * Retrieves a compiled Shader from the pool, or loads and compiles it if not yet loaded.
     * @param vertexResourceName - file path to vertex shader
     * @param fragmentResourceName - file path to fragment shader
     * @return compiled Shader object
     */
    public static Shader getShader(String vertexResourceName, String fragmentResourceName) {
        File vertexFile = new File(vertexResourceName);
        File fragmentFile = new File(fragmentResourceName);
        if(shaders.containsKey(vertexFile.getAbsolutePath()+fragmentFile.getAbsolutePath())) {
            return shaders.get(vertexFile.getAbsolutePath()+fragmentFile.getAbsolutePath());
        } else {
            Shader shader = new Shader(vertexResourceName,fragmentResourceName);
            shader.compileAndLinkShaders();
            shaders.put(vertexFile.getAbsolutePath()+fragmentFile.getAbsolutePath(), shader);
            return shader;
        }
    }
    /*
     * Retrieves a Texture from the pool, or loads it if not yet loaded.
     * Throws exception if the texture file does not exist.
     * @param resourceName - file path to texture image
     * @return loaded Texture object
     */
    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        //handling error
        if (!file.exists()) {
            throw new RuntimeException("Texture file not found: " + resourceName);
        }
        if(textures.containsKey(file.getAbsolutePath())) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
    /*
     * Adds a SpriteSheet to the pool. If one already exists for this path, it replaces it.
     * @param resourceName - file path to spritesheet texture
     * @param spriteSheet - loaded SpriteSheet object
     */
    public static void addSpritesheet(String resourceName, SpriteSheet spriteSheet) {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        } else {
            AssetPool.spriteSheets.replace(file.getAbsolutePath(), spriteSheet);
        }
    }
    /*
     * Retrieves a SpriteSheet from the pool.
     * Throws exception if no SpriteSheet has been loaded for the provided path.
     * @param resourceName - file path to spritesheet texture
     * @return loaded SpriteSheet object
     */
    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            throw new RuntimeException("SpriteSheet not found: " + resourceName);
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
    /*
     * Returns all loaded SpriteSheets currently managed by the pool.
     * @return map of loaded SpriteSheets
     */
    public static Map<String, SpriteSheet> getSpriteSheets() {

        return spriteSheets;
    }
}