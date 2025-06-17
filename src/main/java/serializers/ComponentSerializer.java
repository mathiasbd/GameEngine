package serializers;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

/*
 * ComponentSerializer handles JSON serialization and deserialization of Component instances
 * by embedding type metadata and delegating to Gson context for property processing.
 * Author(s):
 */
public class ComponentSerializer implements JsonSerializer<Component>, JsonDeserializer<Component> {

    /*
     * Deserializes a JsonElement into a Component instance by reading its type metadata
     * and properties, then delegating to Gson context for instantiation.
     * @param jsonElement - JSON element containing type and properties of the component
     * @param typeOfT - the expected type (ignored, uses metadata instead)
     * @param context - Gson context for deserialization of the properties element
     * @return Component - deserialized Component instance of the specified type
     * @throws JsonParseException - if the type class is not found or parsing fails
     */
    @Override
    public Component deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Class not found: " + type, e);
        }
    }

    /*
     * Serializes a Component instance into a JsonElement by recording its canonical
     * class name and serializing its properties via Gson context.
     * @param component - Component instance to serialize
     * @param type - runtime type of the element (ignored)
     * @param context - Gson context for serialization of the component properties
     * @return JsonElement - JSON object with "type" and "properties" fields
     */
    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("properties", context.serialize(component, component.getClass()));
        return result;
    }
}
