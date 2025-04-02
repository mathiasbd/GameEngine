package util;

import org.joml.Vector2f;

public class DTUMath {

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) { // Modifies the vector given as argument
        float angleRad = (float) Math.toRadians(angleDeg);
        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;
        float xNew = (x * cos) - y * sin;
        float yNew = (x * sin) + y * cos;
        xNew += origin.x;
        yNew += origin.y;
        vec.x = xNew;
        vec.y = yNew;
    }

    public static boolean compare(float a, float b, float epsilon) { // Custom float comparison that allows for a small error margin (epsilon)
        return Math.abs(a - b) <= epsilon * Math.max(1.0f, Math.max(Math.abs(a), Math.abs(b)));
    }

    public static boolean compare(Vector2f a, Vector2f b, float epsilon) { // Custom vector comparison
        return compare(a.x, b.x, epsilon) && compare(a.y, b.y, epsilon);
    }

    public static boolean compare(float x, float y) { // Custom float comparison with default epsilon
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f a, Vector2f b) { // Custom vector comparison with default epsilon
        return compare(a.x, b.x) && compare(a.y, b.y); // ignore variable name warning
    }
}
