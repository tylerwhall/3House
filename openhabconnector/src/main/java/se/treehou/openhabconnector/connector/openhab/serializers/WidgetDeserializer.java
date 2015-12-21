package se.treehou.openhabconnector.connector.openhab.serializers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.openhabconnector.connector.openhab.core.LinkedPage;
import se.treehou.openhabconnector.connector.openhab.core.Widget;
import se.treehou.openhabconnector.connector.openhab.request.ItemData;
import se.treehou.openhabconnector.connector.openhab.request.LinkedPageData;
import se.treehou.openhabconnector.connector.openhab.request.WidgetData;
import se.treehou.openhabconnector.connector.openhab.request.WidgetMappingData;

public class WidgetDeserializer implements JsonDeserializer<List<WidgetData>> {

    private static final String TAG = "WidgetDeserializer";

    private static final String KEY_LINKED_PAGE = "linkedPage";

    private WidgetData deserializeWidget(JsonObject jObject, JsonDeserializationContext context){

        WidgetData widget = new WidgetData();
        widget.setType(jObject.get("type").getAsString());
        widget.setLabel(jObject.get("label").getAsString());
        widget.setWidgetId(jObject.get("widgetId").getAsString());
        widget.setIcon(jObject.get("icon").getAsString());

        if(jObject.has("period")) {
            widget.setPeriod(jObject.get("period").getAsString());
        }

        if(jObject.has("service")) {
            widget.setService(jObject.get("service").getAsString());
        }

        if(jObject.has("url")) {
            widget.setUrl(jObject.get("url").getAsString());
        }

        if(jObject.has("item")){
            ItemData itemData = context.deserialize(jObject.get("item").getAsJsonObject(), ItemData.class);
            widget.setItem(itemData);
        }

        if(jObject.has("minValue")){
            widget.setMinValue(jObject.get("minValue").getAsInt());
        }

        if(jObject.has("maxValue")){
            widget.setMaxValue(jObject.get("maxValue").getAsInt());
        }

        if(jObject.has("step") && jObject.get("step") != null){
            try {
                widget.setStep(jObject.get("step").getAsFloat());
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Cannot parse " + jObject.get("step").getAsString() + " as float.");
            }
        }

        if(jObject.has(KEY_LINKED_PAGE)){
            JsonObject jLinkedPage = jObject.getAsJsonObject(KEY_LINKED_PAGE);
            LinkedPageData linkedPage = context.deserialize(jLinkedPage, LinkedPageData.class);
            widget.setLinkedPage(linkedPage);
        }

        if(jObject.has("widget")) {
            JsonElement jWidgetElement = jObject.get("widget");
            List<WidgetData> widgets = context.deserialize(jWidgetElement, new TypeToken<List<Widget>>() {}.getType());
            widget.setWidget(widgets);
        }
        else if(jObject.has("widgets")) { // openhab2 compat
            JsonElement jWidgetElement = jObject.get("widgets");
            List<WidgetData> widgets = context.deserialize(jWidgetElement, new TypeToken<List<Widget>>() {}.getType());
            widget.setWidget(widgets);
        }

        if(jObject.has("mapping")) {
            JsonElement jMappingElement = jObject.get("mapping");
            List<WidgetMappingData> mapping = context.deserialize(jMappingElement, new TypeToken<List<WidgetMappingData>>(){}.getType());
            widget.setMapping(mapping);
        }

        if(jObject.has("mappings")) {
            JsonElement jMappingElement = jObject.get("mappings");
            List<WidgetMappingData> mapping = context.deserialize(jMappingElement, new TypeToken<List<WidgetMappingData>>(){}.getType());
            widget.setMapping(mapping);
        }


        return widget;
    }

    @Override
    public List<WidgetData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List<WidgetData> widgets = new ArrayList<>();

        if(json.isJsonObject()) {
            WidgetData widget = deserializeWidget(json.getAsJsonObject(), context);
            widgets.add(widget);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                WidgetData widget = deserializeWidget(e.getAsJsonObject(), context);
                widgets.add(widget);
            }
        }

        return widgets;
    }
}
