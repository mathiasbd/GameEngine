package util;

import components.SpriteSheet;
import rendering.Shader;
import rendering.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
 * AssetPool caches and provides access to Shaders, Textures, and SpriteSheets.
 * Prevents redundant loading by reusing previously loaded assets.
 * Author(s):
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    /*
     * Retrieves or loads a Shader composed of the specified vertex and fragment files.
     * @param vertexResourceName - filepath to the vertex shader source
     * @param fragmentResourceName - filepath to the fragment shader source
     * @return the compiled and linked Shader instance
     */
    public static Shader getShader(String vertexResourceName, String fragmentResourceName) {
        File vertexFile = new File(vertexResourceName);
        File fragmentFile = new File(fragmentResourceName);
        String key = vertexFile.getAbsolutePath() + fragmentFile.getAbsolutePath();
        if (shaders.containsKey(key)) {
            return shaders.get(key);
        } else {
            Shader shader = new Shader(vertexResourceName, fragmentResourceName);
            shader.compileAndLinkShaders();
            shaders.put(key, shader);
            return shader;
        }
    }

    /*
     * Retrieves or loads a Texture from the given resource path.
     * @param resourceName - filepath to the image to load as a texture
     * @return the initialized Texture instance
     * @throws RuntimeException - if the texture file does not exist
     */
    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (!file.exists()) {
            throw new RuntimeException("Texture file not found: " + resourceName);
        }
        String key = file.getAbsolutePath();
        if (textures.containsKey(key)) {
            return textures.get(key);
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            textures.put(key, texture);
            return texture;
        }
    }

    /*
     * Adds or replaces a SpriteSheet in the asset cache.
     * @param resourceName - filepath key for the spritesheet
     * @param spriteSheet - the SpriteSheet instance to cache
     */
    public static void addSpritesheet(String resourceName, SpriteSheet spriteSheet) {
        File file = new File(resourceName);
        String key = file.getAbsolutePath();
        if (!spriteSheets.containsKey(key)) {
            spriteSheets.put(key, spriteSheet);
        } else {
            spriteSheets.replace(key, spriteSheet);
        }
    }

    /*
     * Retrieves a cached SpriteSheet by its resource filepath.
     * @param resourceName - filepath key for the spritesheet
     * @return the cached SpriteSheet instance
     * @throws RuntimeException - if no spritesheet is found for the given filepath
     */
    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        String key = file.getAbsolutePath();
        if (!spriteSheets.containsKey(key)) {
            throw new RuntimeException("SpriteSheet not found: " + resourceName);
        }
        return spriteSheets.get(key);
    }

    /*
     * Returns all cached SpriteSheets.
     * @return a map of resource filepaths to SpriteSheet instances
     */
    public static Map<String, SpriteSheet> getSpriteSheets() {
        return spriteSheets;
    }
}
