package util;

import components.SpriteSheet;
import rendering.Shader;
import rendering.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

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

    public static void addSpritesheet(String resourceName, SpriteSheet spriteSheet) {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        } else {
            AssetPool.spriteSheets.replace(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpriteSheet(String resourceName) {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            throw new RuntimeException("SpriteSheet not found: " + resourceName);
        }
        SpriteSheet test = AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Map<String, SpriteSheet> getSpriteSheets() {
        return spriteSheets;
    }
}
