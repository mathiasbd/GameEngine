package rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/*
 * Shader loads, compiles, and manages an OpenGL shader program composed of a
 * vertex and fragment shader.
 * Author(s): Mathias
 */
public class Shader {
    private int vertexId, fragmentId;
    private String vertexSource;
    private String fragmentSource;
    private int shaderProgram;

    private boolean beingUsed = false;

    /*
     * Constructs a Shader by loading vertex and fragment shader sources from files.
     * @param vertexFilepath - path to vertex shader file
     * @param fragmentFilepath - path to fragment shader file
     */
    public Shader(String vertexFilepath, String fragmentFilepath) {
        getShaderSource(vertexFilepath);
        getShaderSource(fragmentFilepath);
    }

    /*
     * Reads shader source from a file, splits by "#type" tags, and assigns
     * the source to vertexSource or fragmentSource based on the shader type.
     * @param filepath - path to the shader file
     */
    public void getShaderSource(String filepath) {
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([A-Za-z]+)");
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("#version");
            String name = source.substring(index, eol).trim();

            if (name.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (name.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Error loading shader source: No shader called '" + name + "'");
            }

            System.out.println("Successfully loaded " + name + " shader source");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Compiles a shader of a given type and checks for compilation errors.
     * @param shaderProgramId - OpenGL shader ID
     * @param source - GLSL source code to compile
     */
    public void compileShader(int shaderProgramId, String source) {
        glShaderSource(shaderProgramId, source);
        glCompileShader(shaderProgramId);

        int success = glGetShaderi(shaderProgramId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + shaderProgramId + " shader compilation failed.");
            System.out.println(glGetShaderInfoLog(shaderProgramId, len));
            throw new RuntimeException(shaderProgramId + " shader failed to compile.");
        } else {
            System.out.println(shaderProgramId + " shader compiled successfully.");
        }
    }

    /*
     * Creates vertex and fragment shaders, compiles them, links into a shader program,
     * and checks for linking errors.
     */
    public void compileAndLinkShaders() {
        this.vertexId = glCreateShader(GL_VERTEX_SHADER);
        compileShader(vertexId, vertexSource);
        this.fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        compileShader(fragmentId, fragmentSource);

        this.shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        int success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: shader linking failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            throw new RuntimeException("Shader linking failed.");
        } else {
            System.out.println("Shaders linked successfully.");
        }
    }

    /*
     * Activates the shader program for rendering use.
     */
    public void useProgram() {
        if (!beingUsed) {
            glUseProgram(shaderProgram);
            beingUsed = true;
        }
    }

    /*
     * Deactivates the current shader program.
     */
    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    /*
     * Uploads a 4x4 matrix uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param mat4 - Matrix4f instance to upload
     */
    public void uploadMat4f(String name, Matrix4f mat4) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat4.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }

    /*
     * Uploads a 3x3 matrix uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param mat3 - Matrix3f instance to upload
     */
    public void uploadMat3f(String name, Matrix3f mat3) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        mat3.get(buffer);
        glUniformMatrix3fv(location, false, buffer);
    }

    /*
     * Uploads a vec4 uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param vec4f - Vector4f instance to upload
     */
    public void uploadVec4f(String name, Vector4f vec4f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform4f(location, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    /*
     * Uploads a vec3 uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param vec3f - Vector3f instance to upload
     */
    public void uploadVec3f(String name, Vector3f vec3f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform3f(location, vec3f.x, vec3f.y, vec3f.z);
    }

    /*
     * Uploads a vec2 uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param vec2f - Vector2f instance to upload
     */
    public void uploadVec2f(String name, Vector2f vec2f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform2f(location, vec2f.x, vec2f.y);
    }

    /*
     * Uploads a float uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param value - float value to upload
     */
    public void uploadFloat(String name, float value) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform1f(location, value);
    }

    /*
     * Uploads an int uniform to the shader program.
     * @param name - uniform variable name in shader
     * @param value - int value to upload
     */
    public void uploadInt(String name, int value) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform1i(location, value);
    }

    /*
     * Uploads an array of ints to the shader program.
     * @param name - uniform array name in shader
     * @param values - array of int values to upload
     */
    public void uploadIntArray(String name, int[] values) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform1iv(location, values);
    }

    /*
     * Binds a texture unit to a sampler uniform in the shader.
     * @param name - sampler uniform name in shader
     * @param slot - texture slot index to bind
     */
    public void uploadTexture(String name, int slot) {
        int location = glGetUniformLocation(shaderProgram, name);
        glGetUniformi(slot, location);
    }
}
