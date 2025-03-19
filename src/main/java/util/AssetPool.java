package util;

import rendering.Shader;
import rendering.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

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
        if(textures.containsKey(file.getAbsolutePath())) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(resourceName);
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
