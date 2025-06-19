package util;

import org.example.GameEngineManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.primitives.Line2D;
import rendering.Shader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/*
 * DebugDraw provides static methods to draw debug primitives (lines, boxes, circles) using OpenGL.
 * It accumulates Line2D objects each frame and handles their buffering and rendering.
 * Author(s): Gabriel, Ilias, Ahmed, Mathias
 */
public class DebugDraw {

    private static boolean enabled = false;
    private static final int MAX_LINES = 1000;
    private static List<Line2D> line2DS = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/lineVertex.glsl", "assets/shaders/lineFragment.glsl");

    private static int vaoID, vboID;
    private static boolean started = false;

    /*
     * Initializes OpenGL buffers and vertex arrays for debug drawing.
     */
    public static void start() {
        // generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // generate the vbo and buffer some data
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // enable the vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(10.0f);
    }

    /*
     * Prepares for a new frame, updating and removing expired lines.
     */
    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        for (int i = 0; i < line2DS.size(); i++) {
            if (line2DS.get(i).beginFrame() < 0) { // if found dead line, remove it and decrement i so we don't skip the next line
                line2DS.remove(i);
                i--;
            }
        }
    }

    /*
     * Draws all accumulated debug lines using the shader, projection, and view matrices.
     */
    public static void drawLines() {
        if (line2DS.size() <= 0) return;
        //System.out.println("Drawing " + lines.size() + " lines");
        int index = 0;
        for (Line2D l : line2DS) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? l.getFrom() : l.getTo();
                Vector3f color = l.getColor();

                // load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = 0.0f; // z position

                // load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;

                index += 6; // move to the next vertex
            }
        }
        // update the vbo with the new data
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, line2DS.size() * 6 * 2));

        // draw the lines
        shader.useProgram();
        shader.uploadMat4f("uProjection", GameEngineManager.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", GameEngineManager.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, line2DS.size() * 2);

        // disable Location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // unbind shader
        shader.detach();
    }

    /*
     * Adds a line between two points with default color (green) and lifetime (1).
     * @param from - the start position of the line
     * @param to - the end position of the line
     */
    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    /*
     * Adds a line between two points with the specified color and default lifetime (1).
     * @param from - the start position of the line
     * @param to - the end position of the line
     * @param color - the RGB color of the line
     */
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    /*
     * Adds a line between two points with the specified color and lifetime.
     * @param from - the start position of the line
     * @param to - the end position of the line
     * @param color - the RGB color of the line
     * @param lifetime - how many frames the line should persist
     */
    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if (!enabled) return;
        if (line2DS.size() >= MAX_LINES) {
            System.out.println("Max lines reached, not adding line");
            return;
        }
        DebugDraw.line2DS.add(new Line2D(from, to, color, lifetime));
    }

    /*
     * Adds a rectangle box centered at a point with default color and lifetime.
     * @param center - the center position of the box
     * @param dimensions - width and height of the box
     * @param rotation - rotation angle in radians
     */
    public static void addBox(Vector2f center, Vector2f dimensions, float rotation) {
        addBox(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
    }

    /*
     * Adds a rectangle box centered at a point with the specified color and default lifetime.
     * @param center - the center position of the box
     * @param dimensions - width and height of the box
     * @param rotation - rotation angle in radians
     * @param color - the RGB color of the box lines
     */
    public static void addBox(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
        addBox(center, dimensions, rotation, color, 1);
    }

    /*
     * Adds a rectangle box centered at a point with the specified color and lifetime.
     * @param center - the center position of the box
     * @param dimensions - width and height of the box
     * @param rotation - rotation angle in radians
     * @param color - the RGB color of the box lines
     * @param lifetime - how many frames the box should persist
     */
    public static void addBox(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2));
        Vector2f[] points = new Vector2f[4];
        points[0] = new Vector2f(min.x, min.y);
        points[1] = new Vector2f(min.x, max.y);
        points[2] = new Vector2f(max.x, max.y);
        points[3] = new Vector2f(max.x, min.y);

        if (rotation != 0.0f) {
            for (Vector2f point : points) {
                DTUMath.rotate(point, rotation, center);
            }
        }

        addLine2D(points[0], points[1], color, lifetime);
        addLine2D(points[1], points[2], color, lifetime);
        addLine2D(points[2], points[3], color, lifetime);
        addLine2D(points[3], points[0], color, lifetime);
    }

    /*
     * Adds a circle centered at a point with default color and lifetime.
     * @param center - the center position of the circle
     * @param radius - radius of the circle
     */
    public static void addCircle(Vector2f center, float radius) {
        addCircle(center, radius, new Vector3f(0, 1, 0), 1);
    }

    /*
     * Adds a circle centered at a point with the specified color and default lifetime.
     * @param center - the center position of the circle
     * @param radius - radius of the circle
     * @param color - the RGB color of the circle lines
     */
    public static void addCircle(Vector2f center, float radius, Vector3f color) {
        addCircle(center, radius, color, 1);
    }

    /*
     * Adds a circle centered at a point with the specified color and lifetime.
     * @param center - the center position of the circle
     * @param radius - radius of the circle
     * @param color - the RGB color of the circle lines
     * @param lifetime - how many frames the circle should persist
     */
    public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime) {
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            DTUMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                addLine2D(points[i - 1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length - 1], points[0], color, lifetime);
    }

    /** Returns whether debug drawing is enabled. */
    public static boolean isEnabled() {
        return enabled;
    }

    /** Enable or disable debug drawing. */
    public static void setEnabled(boolean e) {
        enabled = e;
    }

    /** Flip the current state. */
    public static void toggle() {
        enabled = !enabled;
    }
}
