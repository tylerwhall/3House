package se.treehou.openhabconnector.connector.openhab.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.openhabconnector.connector.openhab.request.WidgetMappingData;

public class WidgetMappingDeserializer implements JsonDeserializer<List<WidgetMappingData>> {

    private static final String TAG = "WidgetMappingDeserializer";

    @Override
    public List<WidgetMappingData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List mapping = new ArrayList();

        if(json.isJsonObject()) {
            WidgetMappingData entry = context.deserialize(json.getAsJsonObject(), WidgetMappingData.class);
            mapping.add(entry);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                WidgetMappingData entry = context.deserialize(e, WidgetMappingData.class);
                mapping.add(entry);
            }
        }

        return mapping;
    }
}
