package util;

import org.joml.Vector2f;

/*
 * DTUMath provides utility math operations such as vector rotation, cross products,
 * and comparisons with an epsilon tolerance.
 * Author(s):
 */
public class DTUMath {

    /*
     * Rotates a vector around a given origin by a specified angle in degrees.
     * Modifies the vector given as argument.
     * @param vec - the vector to rotate; its components will be modified
     * @param angleDeg - rotation angle in degrees
     * @param origin - the pivot point for rotation
     */
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

    /*
     * Computes the scalar cross product of two 2D vectors.
     * @param a - the first vector
     * @param b - the second vector
     * @return the scalar cross product (a.x * b.y - a.y * b.x)
     */
    public static float cross(Vector2f a, Vector2f b) {
        return a.x * b.y - a.y * b.x;
    }

    /*
     * Computes the cross product of a vector and a scalar, returning a perpendicular vector.
     * @param v - the input vector
     * @param a - the scalar value
     * @return a new Vector2f representing the cross result (-a * v.y, a * v.x)
     */
    public static Vector2f cross(Vector2f v, float a) {
        return new Vector2f(-a * v.y, a * v.x);
    }

    /*
     * Computes the cross product of a scalar and a vector, returning a perpendicular vector.
     * @param a - the scalar value
     * @param v - the input vector
     * @return a new Vector2f representing the cross result (a * v.y, -a * v.x)
     */
    public static Vector2f cross(float a, Vector2f v) {
        return new Vector2f(a * v.y, -a * v.x);
    }

    /*
     * Compares two floats within a relative error margin.
     * @param a - the first float value
     * @param b - the second float value
     * @param epsilon - allowable relative error factor
     * @return true if |a - b| <= epsilon * max(1, |a|, |b|)
     */
    public static boolean compare(float a, float b, float epsilon) { // Custom float comparison that allows for a small error margin (epsilon)
        return Math.abs(a - b) <= epsilon * Math.max(1.0f, Math.max(Math.abs(a), Math.abs(b)));
    }

    /*
     * Compares two vectors component-wise using a default epsilon.
     * @param a - the first vector
     * @param b - the second vector
     * @return true if each component is equal within the default float epsilon
     */
    public static boolean compare(Vector2f a, Vector2f b) { // Custom vector comparison with default epsilon
        return compare(a.x, b.x) && compare(a.y, b.y); // ignore variable name warning
    }

    /*
     * Compares two floats using the default machine epsilon.
     * @param a - the first float value
     * @param b - the second float value
     * @return true if |a - b| <= Float.MIN_VALUE * max(1, |a|, |b|)
     */
    public static boolean compare(float a, float b) { // Custom float comparison with default epsilon
        return Math.abs(a - b) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(a), Math.abs(b)));
    }

}
