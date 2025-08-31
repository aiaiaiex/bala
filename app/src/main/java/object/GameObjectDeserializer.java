package object;

import java.lang.reflect.Type;
import org.joml.Vector2f;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import component.Component;
import component.Transform;
import physics.Box2DCollider;
import physics.CircleCollider;
import setting.EngineSettings;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);

            if (c instanceof CircleCollider && !EngineSettings.USE_CIRCLE_COLLIDER) {
                Box2DCollider boxCollider = new Box2DCollider();
                boxCollider.setSize(
                        new Vector2f(EngineSettings.GRID_WIDTH, EngineSettings.GRID_HEIGHT));

                go.addComponent(boxCollider);
            } else if (c instanceof Box2DCollider && EngineSettings.USE_CIRCLE_COLLIDER) {
                CircleCollider circleCollider = new CircleCollider();
                circleCollider.setRadius(EngineSettings.GRID_WIDTH / 2);

                go.addComponent(circleCollider);
            } else {
                go.addComponent(c);
            }
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
