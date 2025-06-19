package physics;

import org.example.GameEngineManager;
import org.example.GameObject;
import org.joml.Vector2f;
import physics.collisions.CollisionManifold;
import physics.collisions.Rigidbody2D;
import physics.primitives.Circle;
import physics.primitives.Collider;
import physics.primitives.OBBCollider;
import physics.raycast.Raycast;
import physics.raycast.RaycastManager;
import physics.raycast.RaycastResult;
import util.DebugDraw;

import java.util.ArrayList;
import java.util.List;

/*
 * Physics2D provides static utility methods for common physics queries,
 * including grounding, wall checks, collision tests, and raycasts.
 * Author(s): Gabriel
 */
public class Physics2D {

    /*
     * Casts downward rays from the Rigidbody to determine if it is grounded.
     * @param rb - the Rigidbody2D to test
     * @param rayLength - maximum length of grounding rays
     * @return true if any ray hits terrain within rayLength
     */
    public static boolean isGrounded(Rigidbody2D rb, float rayLength) {
        Vector2f position = rb.getPosition();
        Collider collider = rb.getCollider();
        float halfsizeX = 0, halfsizeY = 0;

        // determine bounds from collider type
        if (collider instanceof OBBCollider obb) {
            halfsizeX = obb.getHalfSize().x;
            halfsizeY = obb.getHalfSize().y;
        } else if (collider instanceof Circle circle) {
            halfsizeX = circle.getRadius();
            halfsizeY = circle.getRadius();
        } else {
            throw new IllegalStateException("Unsupported collider type for grounding check");
        }

        // origins at left, center, right bottom of collider
        Vector2f baseOrigin = new Vector2f(position.x, position.y);
        Vector2f[] rayOrigins = new Vector2f[] {
                new Vector2f(position.x - halfsizeX, position.y),
                new Vector2f(position.x,            position.y),
                new Vector2f(position.x + halfsizeX, position.y)
        };

        Vector2f rayDir = new Vector2f(0, -1);
        List<Rigidbody2D> bodies = GameEngineManager.getPhysicsSystem().getRigidbodies();

        for (Vector2f origin : rayOrigins) {
            Raycast ray = new Raycast(origin, rayDir);

            for (Rigidbody2D other : bodies) {
                if (other == rb) continue;  // skip self
                Collider otherCol = other.getCollider();
                if (otherCol == null || !otherCol.isSolid()) continue;

                RaycastResult res;
                if (otherCol instanceof OBBCollider obb) {
                    res = RaycastManager.raycastOBB(ray, obb, new RaycastResult());
                } else if (otherCol instanceof Circle circ) {
                    res = RaycastManager.raycastCircle(ray, circ, new RaycastResult());
                } else {
                    continue;
                }

                if (res.isHit() && res.getDistance() > 0 && res.getDistance() < rayLength) {
                    DebugDraw.addLine2D(baseOrigin, res.getPoint());  // visualize ray
                    // check if hit point is at or just below collider bottom
                    if (res.getPoint().y >= position.y - halfsizeY - 0.05f) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * Casts horizontal rays to determine if the Rigidbody is touching a wall.
     * @param rb - the Rigidbody2D to test
     * @param rayLength - maximum length of wall-check rays
     * @return true if any ray hits a collider within rayLength
     */
    public static boolean isTouchingWall(Rigidbody2D rb, float rayLength) {
        Vector2f position = rb.getPosition();
        Collider collider = rb.getCollider();
        float halfsizeX = 0, halfsizeY = 0;

        // determine bounds from collider type
        if (collider instanceof OBBCollider obb) {
            halfsizeX = obb.getHalfSize().x;
            halfsizeY = obb.getHalfSize().y;
        } else if (collider instanceof Circle circle) {
            halfsizeX = circle.getRadius();
            halfsizeY = circle.getRadius();
        } else {
            throw new IllegalStateException("Unsupported collider type for wall check");
        }

        Vector2f baseOrigin = new Vector2f(position.x, position.y);
        // origins at top-center, center, bottom-center of collider
        Vector2f[] origins = new Vector2f[] {
                new Vector2f(position.x, position.y + halfsizeY * 0.5f),
                new Vector2f(position.x, position.y),
                new Vector2f(position.x, position.y - halfsizeY * 0.5f)
        };
        // left and right directions
        Vector2f[] dirs = new Vector2f[] { new Vector2f(-1,0), new Vector2f(1,0) };

        List<Rigidbody2D> bodies = GameEngineManager.getPhysicsSystem().getRigidbodies();
        for (Vector2f origin : origins) {
            for (Vector2f dir : dirs) {
                Raycast ray = new Raycast(origin, dir);

                for (Rigidbody2D other : bodies) {
                    if (other == rb) continue;
                    Collider otherCol = other.getCollider();
                    if (otherCol == null || !otherCol.isSolid()) continue;

                    RaycastResult res;
                    if (otherCol instanceof OBBCollider obb) {
                        res = RaycastManager.raycastOBB(ray, obb, new RaycastResult());
                    } else if (otherCol instanceof Circle circ) {
                        res = RaycastManager.raycastCircle(ray, circ, new RaycastResult());
                    } else {
                        continue;
                    }

                    if (res.isHit() && res.getDistance() > 0 && res.getDistance() < rayLength) {
                        DebugDraw.addLine2D(baseOrigin, res.getPoint());  // visualize ray
                        // check horizontal overlap within threshold
                        if (Math.abs(res.getPoint().x - position.x) <= halfsizeX + 0.05f) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
     * Checks if the target Rigidbody is currently colliding with any objects matching tags.
     * @param target - the Rigidbody2D to test
     * @param withTags - optional tags to filter collisions
     * @return true if a collision with a matching tag is found
     */
    public static boolean isColliding(Rigidbody2D target, String... withTags) {
        boolean solid = target.getCollider() != null && target.getCollider().isSolid();
        List<CollisionManifold> collisions = solid
                ? GameEngineManager.getPhysicsSystem().getCollisions()
                : GameEngineManager.getPhysicsSystem().getGhostCollisions();

        for (CollisionManifold m : collisions) {
            Rigidbody2D a = m.getA();
            Rigidbody2D b = m.getB();
            if (a != target && b != target) continue;

            GameObject other = (a == target) ? b.getGameObject() : a.getGameObject();
            for (String tag : withTags) {
                if (tag.equals(other.getTag())) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Returns a list of GameObjects colliding with the target, filtered by tags.
     * @param target - the Rigidbody2D whose collisions to query
     * @param checkSolid - true to use solid collisions, false for ghost collisions
     * @param withTags - optional tags to filter returned GameObjects
     * @return list of matching colliding GameObjects
     */
    public static List<GameObject> getCollidingObjects(Rigidbody2D target, boolean checkSolid, String... withTags) {
        List<GameObject> results = new ArrayList<>();
        List<CollisionManifold> collisions = checkSolid
                ? GameEngineManager.getPhysicsSystem().getCollisions()
                : GameEngineManager.getPhysicsSystem().getGhostCollisions();

        for (CollisionManifold m : collisions) {
            Rigidbody2D a = m.getA();
            Rigidbody2D b = m.getB();
            if (a != target && b != target) continue;

            GameObject other = (a == target) ? b.getGameObject() : a.getGameObject();
            for (String tag : withTags) {
                if (tag.equals(other.getTag())) {
                    results.add(other);
                    break;
                }
            }
        }
        return results;
    }
}
