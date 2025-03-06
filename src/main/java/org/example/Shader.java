package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {


    private int vertexId, fragmentId;
    private String vertexSource;
    private String fragmentSource;
    private int shaderProgram;
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
        glUseProgram(shaderProgram);
    }

    public void detach() {
        glUseProgram(0);
    }
}
