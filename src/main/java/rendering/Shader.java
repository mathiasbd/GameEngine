package rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int vertexId, fragmentId;
    private String vertexSource;
    private String fragmentSource;
    private int shaderProgram;

    private boolean beingUsed = false;
    public Shader(String vertexFilepath, String fragmentFilepath) {
        getShaderSource(vertexFilepath);
        getShaderSource(fragmentFilepath);
    }

    public void getShaderSource(String filepath) {
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([A-Za-z]+)");
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("#version");
            String name = source.substring(index, eol).trim();

            if(name.equals("vertex")) {
                vertexSource = splitString[1];
            } else if(name.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Error loading shader source: No shader called '" + name + "'");
            }

            System.out.println("Succesfully loaded " + name + " shader source");

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void compileShader(int shaderProgramId, String source) {
        glShaderSource(shaderProgramId, source);
        glCompileShader(shaderProgramId);

        int success = glGetShaderi(shaderProgramId, GL_COMPILE_STATUS); // gets shaderinfo
        if (success == GL_FALSE) {
            int len = glGetShaderi(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + shaderProgramId + " shader compilation failed.");
            System.out.println(glGetShaderInfoLog(shaderProgramId, len));
            throw new RuntimeException(shaderProgramId + " shader failed to compile.");
        } else {
            System.out.println(shaderProgramId + " shader compiled successfully.");
        }
    }

    public void compileAndLinkShaders() {
        this.vertexId = glCreateShader(GL_VERTEX_SHADER);
        compileShader(vertexId, vertexSource);
        this.fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        compileShader(fragmentId, fragmentSource);
        // linking the shaders
        this.shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram); // now both shaders are attached and linked together

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

    public void useProgram() {
        //bind shade Program
        if (!beingUsed) {
            glUseProgram(shaderProgram);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String name, Matrix4f mat4) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram(); // just to make sure the shader is being used (not completely necessary)
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat4.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }

    // Following method is similar to uploadMat4f, but for Matrix3f

    public void uploadMat3f(String name, Matrix3f mat3) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        mat3.get(buffer);
        glUniformMatrix3fv(location, false, buffer);
    }

    public void uploadVec4f(String name, Vector4f vec4f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform4f(location, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    // Following methods are similar to uploadVec4f, but for Vector3f and Vector2f

    public void uploadVec3f(String name, Vector3f vec3f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform3f(location, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadVec2f(String name, Vector2f vec2f) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform2f(location, vec2f.x, vec2f.y);
    }

    public void uploadFloat(String name, float value) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform1f(location, value);
    }

    public void uploadInt(String name, int value) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram();
        glUniform1i(location, value);
    }

    // same methods but for vec3 and vec2 and matrix3f

    public void uploadIntArray(String name, int[] values) {
        int location = glGetUniformLocation(shaderProgram, name);
        useProgram(); // Ensure the shader is active
        glUniform1iv(location, values);
    }





    public void uploadTexture(String name, int slot) {
        int location = glGetUniformLocation(shaderProgram, name);
        glGetUniformi(slot, location);
    }

}
