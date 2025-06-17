package serializers;

import com.google.gson.*;
import components.Component;
import org.example.GameObject;
import org.example.Transform;

import java.lang.reflect.Type;

/*
 * GameObjectSerializer handles serialization and deserialization of GameObject instances to/from JSON.
 * Utilizes Gson’s JsonSerializer and JsonDeserializer interfaces to convert GameObject properties and components.
 * Author(s):
 */
public class GameObjectSerializer implements JsonSerializer<GameObject>, JsonDeserializer<GameObject> {

    /*
     * Serializes a GameObject to its JSON representation.
     * @param go - the GameObject to serialize
     * @param typeOfSrc - the type of the source object
     * @param context - the JsonSerializationContext for nested serialization
     * @return a JsonElement representing the serialized GameObject
     */
    @Override
    public JsonElement serialize(GameObject go, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", go.getName());
        obj.add("transform", context.serialize(go.getTransform()));
        obj.addProperty("zIndex", go.getzIndex());
        obj.addProperty("inScene", go.isInScene());
        obj.addProperty("tag", go.getTag());

        JsonArray comps = new JsonArray();
        for (Component c : go.getComponents()) {
            comps.add(context.serialize(c, Component.class));
        }
        obj.add("components", comps);
        return obj;
    }

    /*
     * Deserializes a JsonElement into a GameObject instance.
     * @param jsonElement - the JSON data to deserialize
     * @param typeOfT - the target type of the deserialization
     * @param context - the JsonDeserializationContext for nested deserialization
     * @return the deserialized GameObject
     * @throws JsonParseException - if the JSON is malformed or required fields are missing
     */
    @Override
    public GameObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = jsonObject.get("zIndex").getAsInt();
        boolean inScene = jsonObject.get("inScene").getAsBoolean();
        String tag = jsonObject.has("tag") ? jsonObject.get("tag").getAsString() : "None";

        GameObject go = new GameObject(name, transform, zIndex, inScene, tag);

        JsonArray components = jsonObject.getAsJsonArray("components");
        for (JsonElement elem : components) {
            Component c = context.deserialize(elem, Component.class);
            go.addComponent(c);
        }
        return go;
    }
}
