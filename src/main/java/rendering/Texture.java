package rendering;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/*
 * Texture loads and manages OpenGL textures from image files.
 * Author(s):
 */
public class Texture {
    private String filepath;
    private int texID;
    private int width;
    private int height;

    /*
     * Constructs an empty Texture.
     */
    public Texture() {

    }

    /*
     * Initializes the texture by loading image data from a file,
     * generating an OpenGL texture ID, binding it, setting parameters,
     * and uploading the pixel data to the GPU.
     * @param filepath - path to the image file to load
     */
    public void init(String filepath) {
        // Resolve relative path to absolute path

        // Generate texture id and bind it on the GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Create buffer
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        System.out.println("Loading texture from: " + filepath);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
                System.out.println("Texture loaded successfully: " + filepath);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                System.out.println("Texture loaded successfully: " + filepath);
            } else {
                throw new RuntimeException("Error: Picture is not RGB or RGBA");
            }
        } else {
            throw new RuntimeException("Error: Could not load the texture image at " + filepath);
        }

        stbi_image_free(image);
    }

    /*
     * Sets the file path associated with this texture without loading it.
     * @param filepath - the file path to assign to this texture
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /*
     * Binds this texture in the current OpenGL context.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    /*
     * Unbinds any texture from the current OpenGL context.
     */
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /*
     * @return width - the width of the texture in pixels
     */
    public int getWidth() {
        return width;
    }

    /*
     * @return height - the height of the texture in pixels
     */
    public int getHeight() {
        return height;
    }

    /*
     * @return texID - the OpenGL-generated texture ID
     */
    public int getTexID() {
        return texID;
    }

    /*
     * @return filepath - the file path this texture was loaded from
     */
    public String getFilepath() {
        return filepath;
    }
}
