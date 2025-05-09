package util;

import org.example.GameEngineManager;
import org.example.WindowManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.primitives.Line2D;
import rendering.Shader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static imgui.internal.flag.ImGuiDataAuthority.Window;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static final int MAX_LINES = 1000;
    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/lineVertex.glsl", "assets/shaders/lineFragment.glsl");

    private static int vaoID, vboID;
    private static boolean started = false;

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

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) { // if found dead line, remove it and decrement i so we don't skip the next line
                lines.remove(i);
                i--;
            }
        }
    }

    public static void drawLines() {
        if (lines.size() <= 0) return;
        //System.out.println("Drawing " + lines.size() + " lines");
        int index = 0;
        for (Line2D l : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? l.getFrom() : l.getTo();
                Vector3f color = l.getColor();
                System.out.println("from: " + l.getFrom() + " to: " + l.getTo());

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
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // draw the lines
        shader.useProgram();
        shader.uploadMat4f("uProjection", GameEngineManager.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", GameEngineManager.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size()*2);

        // disable Location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // unbind shader
        shader.detach();
    }

    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if (lines.size() >= MAX_LINES) {
            System.out.println("Max lines reached, not adding line");
            return;
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
    }

}
