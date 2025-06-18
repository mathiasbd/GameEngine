package serializers;

import com.google.gson.*;
import components.Component;
import components.SpriteSheet;
import org.example.GameObject;
import org.example.Transform;
import rendering.Texture;
import util.AssetPool;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Paths;

/*
 * SpriteSheetSerializer handles serialization and deserialization of SpriteSheet objects to/from JSON.
 * It utilizes Gson’s JsonSerializer and JsonDeserializer interfaces.
 * Author(s): Mathias
 */
public class SpriteSheetSerializer implements JsonSerializer<SpriteSheet>, JsonDeserializer<SpriteSheet> {

    /*
     * Serializes a SpriteSheet into its JSON representation.
     * @param src - the SpriteSheet to serialize
     * @param typeOfSrc - the type of the source object
     * @param context - the JsonSerializationContext for nested serialization
     * @return a JsonElement representing the serialized SpriteSheet
     */
    @Override
    public JsonElement serialize(SpriteSheet src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add("texture", context.serialize(src.getTexture()));
        json.addProperty("spriteWidth", src.getSpriteWidth());
        json.addProperty("spriteHeight", src.getSpriteHeight());
        json.addProperty("numSprites", src.getNumSprites());
        json.addProperty("xSpacing", src.getXSpacing());
        json.addProperty("ySpacing", src.getYSpacing());
        json.addProperty("startX", src.getStartX());
        // Note: `sprites` intentionally excluded
        return json;
    }

    /*
     * Deserializes a JsonElement into a SpriteSheet instance.
     * @param jsonElement - the JSON data to deserialize
     * @param type - the target type of the deserialization
     * @param context - the JsonDeserializationContext for nested deserialization
     * @return the deserialized SpriteSheet
     * @throws JsonParseException - if the JSON is malformed or required fields are missing
     */
    @Override
    public SpriteSheet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Texture texture = context.deserialize(jsonObject.get("texture"), Texture.class);

        String basePath = System.getProperty("user.dir"); // Project root directory
        String resolvedPath = Paths.get(basePath, texture.getFilepath()).toString();

        System.out.println("Loading texture from: " + resolvedPath);
        texture.setFilepath(resolvedPath);
        texture.init(texture.getFilepath());

        int spriteWidth = context.deserialize(jsonObject.get("spriteWidth"), int.class);
        int spriteHeight = context.deserialize(jsonObject.get("spriteHeight"), int.class);
        int numSprites = context.deserialize(jsonObject.get("numSprites"), int.class);
        int xSpacing = context.deserialize(jsonObject.get("xSpacing"), int.class);
        int ySpacing = context.deserialize(jsonObject.get("ySpacing"), int.class);
        int startX = context.deserialize(jsonObject.get("startX"), int.class);
        SpriteSheet spriteSheet = new SpriteSheet(texture, spriteWidth, spriteHeight, numSprites, xSpacing, ySpacing, startX);
        return spriteSheet;
    }
}
