package se.treehou.openhabconnector.connector.openhab.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import se.treehou.openhabconnector.connector.openhab.request.ItemData;
import se.treehou.openhabconnector.connector.openhab.request.StateDescriptionData;

public class ItemDeserializer implements JsonDeserializer<ItemData>, JsonSerializer<ItemData> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public ItemData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        ItemData item = new ItemData();

        JsonObject jObject = json.getAsJsonObject();
        if(jObject.has("type")) {
            item.setType(jObject.get("type").getAsString());
        }

        if(jObject.has("name")) {
            item.setName(jObject.get("name").getAsString());
        }

        if(jObject.has("state")) {
            item.setState(jObject.get("state").getAsString());
        }

        if(jObject.has("link")) {
            item.setLink(jObject.get("link").getAsString());
        }

        if(jObject.has("stateDescription")) {
            item.setStateDescription(context.<StateDescriptionData>deserialize(jObject.get("stateDescription"), StateDescriptionData.class));
        }

        return item;
    }

    @Override
    public JsonElement serialize(ItemData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", src.getType());
        object.addProperty("name", src.getName());
        object.addProperty("state", src.getState());
        object.addProperty("link", src.getLink());

        if(src.getStateDescription() != null) {
            object.add("stateDescription", context.serialize(src.getStateDescription(), StateDescriptionData.class));
        }

        return object;
    }
}

