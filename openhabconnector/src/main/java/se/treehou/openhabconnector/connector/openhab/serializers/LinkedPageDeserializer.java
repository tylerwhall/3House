package se.treehou.openhabconnector.connector.openhab.serializers;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import se.treehou.openhabconnector.connector.openhab.core.LinkedPage;
import se.treehou.openhabconnector.connector.openhab.request.LinkedPageData;
import se.treehou.openhabconnector.connector.openhab.request.WidgetData;

public class LinkedPageDeserializer implements JsonDeserializer<LinkedPageData> {

    private static final String TAG = "LinkedPageDeserializer";

    @Override
    public LinkedPageData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if(json.isJsonObject()) {
            JsonObject jObject = json.getAsJsonObject();

            LinkedPageData linkedPage = new LinkedPageData();

            if(jObject.has("id")) {
                linkedPage.setId(jObject.get("id").getAsString());
            }

            if(jObject.has("title")) {
                linkedPage.setTitle(jObject.get("title").getAsString());
            }

            if(jObject.has("link")) {
                linkedPage.setLink(jObject.get("link").getAsString());
            }

            if(jObject.has("leaf")) {
                linkedPage.setLeaf(jObject.get("leaf").getAsBoolean());
            }

            Log.d(TAG, "No problems go on " + jObject.has("widgets") + " " + jObject.isJsonObject());

            JsonElement jWidgets = null;
            if(jObject.has("widgets")) {
                jWidgets = jObject.get("widgets");
            } else if(jObject.has("widget")) {
                jWidgets = jObject.get("widget");
            }

            if(jWidgets != null) {
                linkedPage.setWidgets(context.<List<WidgetData>>deserialize(jWidgets, new TypeToken<List<WidgetData>>() {}.getType()));
            }

            return linkedPage;
        }

        Log.d(TAG, "Failed to parse correctly " + json);
        return null;
    }
}
