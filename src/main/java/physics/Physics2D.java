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

public class Physics2D {
    public static boolean isGrounded(Rigidbody2D rb, float rayLength) {
        Vector2f position = rb.getPosition();
        Collider collider = rb.getCollider();
        float halfsizeX = 0.0f;
        float halfsizeY = 0.0f;

        if (collider instanceof OBBCollider obb) {
            halfsizeX = obb.getHalfSize().x;
            halfsizeY = obb.getHalfSize().y;
        } else if (collider instanceof Circle circle) {
            halfsizeX = circle.getRadius();
            halfsizeY = circle.getRadius();
        } else {
            throw new IllegalStateException("Unsupported collider type for grounding check");
        }

        // Cast 3 rays from left, center, and right
        Vector2f trueOrgin = new Vector2f(position.x, position.y);
        Vector2f[] rayOrigins = new Vector2f[] {
                new Vector2f(position.x - halfsizeX, position.y),
                new Vector2f(position.x, position.y),
                new Vector2f(position.x + halfsizeX, position.y)
        };


        Vector2f rayDirection = new Vector2f(0, -1);
        List<Rigidbody2D> rbs = GameEngineManager.getPhysicsSystem().getRigidbodies();

        for (Vector2f origin : rayOrigins) {
            Raycast ray = new Raycast(origin, rayDirection);

            for (Rigidbody2D otherRb : rbs) {
                if (otherRb == rb) continue;
                Collider otherCollider = otherRb.getCollider();
                if (otherCollider == null) continue;

                RaycastResult result;
                if (otherCollider instanceof OBBCollider obb) {
                    result = RaycastManager.raycastOBB(ray, obb, new RaycastResult());
                } else if (otherCollider instanceof Circle circle) {
                    result = RaycastManager.raycastCircle(ray, circle, new RaycastResult());
                } else {
                    continue;
                }

                if (result.isHit() && result.getDistance() < rayLength && result.getDistance() > 0.0f) {
                    DebugDraw.addLine2D(trueOrgin, result.getPoint());
                    if (result.getPoint().y >= position.y - halfsizeY - 0.05f) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean isTouchingWall(Rigidbody2D rb, float rayLength) {
        Vector2f position = rb.getPosition();
        Collider collider = rb.getCollider();
        float halfsizeX = 0.0f;
        float halfsizeY = 0.0f;

        if (collider instanceof OBBCollider obb) {
            halfsizeX = obb.getHalfSize().x;
            halfsizeY = obb.getHalfSize().y;
        } else if (collider instanceof Circle circle) {
            halfsizeX = circle.getRadius();
            halfsizeY = circle.getRadius();
        } else {
            throw new IllegalStateException("Unsupported collider type for wall check");
        }

        Vector2f trueOrgin = new Vector2f(position.x, position.y);
        Vector2f[] rayOrigins = new Vector2f[] {
                new Vector2f(position.x, position.y + halfsizeY * 0.5f),
                new Vector2f(position.x, position.y),
                new Vector2f(position.x, position.y - halfsizeY * 0.5f)
        };

        Vector2f[] directions = new Vector2f[] {
                new Vector2f(-1, 0),
                new Vector2f(1, 0)
        };

        List<Rigidbody2D> rbs = GameEngineManager.getPhysicsSystem().getRigidbodies();
        for (Vector2f origin : rayOrigins) {
            for (Vector2f dir : directions) {
                Raycast ray = new Raycast(origin, dir);

                for (Rigidbody2D otherRb : rbs) {
                    if (otherRb == rb) continue;
                    Collider otherCollider = otherRb.getCollider();
                    if (otherCollider == null) continue;

                    RaycastResult result;
                    if (otherCollider instanceof OBBCollider obb) {
                        result = RaycastManager.raycastOBB(ray, obb, new RaycastResult());
                    } else if (otherCollider instanceof Circle circle) {
                        result = RaycastManager.raycastCircle(ray, circle, new RaycastResult());
                    } else {
                        continue;
                    }

                    if (result.isHit() && result.getDistance() < rayLength && result.getDistance() > 0.0f) {
                        DebugDraw.addLine2D(trueOrgin, result.getPoint());
                        if (Math.abs(result.getPoint().x - position.x) <= halfsizeX + 0.05f) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isColliding(Rigidbody2D target, String... withTags) {
        boolean solid = (target.getCollider() != null && target.getCollider().isSolid());

        List<CollisionManifold> collisions = solid
                ? GameEngineManager.getPhysicsSystem().getCollisions()
                : GameEngineManager.getPhysicsSystem().getGhostCollisions();

        for (CollisionManifold m : collisions) {
            Rigidbody2D rbA = m.getA();
            Rigidbody2D rbB = m.getB();

            if (rbA != target && rbB != target) continue;

            GameObject other = (rbA == target) ? rbB.getGameObject() : rbA.getGameObject();
            for (String tag : withTags) {
                if (tag.equals(other.getTag())) return true;
            }
        }
        return false;
    }

    public static List<GameObject> getCollidingObjects(Rigidbody2D target, boolean checkSolid, String... withTags) {
        List<GameObject> collidingObjects = new ArrayList<>();

        List<CollisionManifold> collisions = checkSolid
                ? GameEngineManager.getPhysicsSystem().getCollisions()
                : GameEngineManager.getPhysicsSystem().getGhostCollisions();

        for (CollisionManifold m : collisions) {
            Rigidbody2D rbA = m.getA();
            Rigidbody2D rbB = m.getB();

            if (rbA != target && rbB != target) continue;

            GameObject other = (rbA == target) ? rbB.getGameObject() : rbA.getGameObject();
            for (String tag : withTags) {
                if (tag.equals(other.getTag())) {
                    collidingObjects.add(other);
                    break;
                }
            }
        }
        return collidingObjects;
    }
}
