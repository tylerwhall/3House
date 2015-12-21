package se.treehou.openhabconnector.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import se.treehou.openhabconnector.connector.openhab.core.LinkedPage;
import se.treehou.openhabconnector.connector.openhab.core.Sitemap;
import se.treehou.openhabconnector.connector.openhab.core.Widget;
import se.treehou.openhabconnector.connector.openhab.request.ItemData;
import se.treehou.openhabconnector.connector.openhab.request.StateDescriptionData;
import se.treehou.openhabconnector.connector.openhab.request.WidgetMappingData;
import se.treehou.openhabconnector.connector.openhab.serializers.ItemDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.ItemListDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.ItemStateDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.LinkedPageDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.SitemapListDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.WidgetDeserializer;
import se.treehou.openhabconnector.connector.openhab.serializers.WidgetMappingDeserializer;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget>>() {}.getType(), new WidgetDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Sitemap>>() {}.getType(), new SitemapListDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<WidgetMappingData>>() {}.getType(), new WidgetMappingDeserializer());
            gsonBuilder.registerTypeAdapter(LinkedPage.class, new LinkedPageDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<ItemData>>() {}.getType(), new ItemListDeserializer());
            gsonBuilder.registerTypeAdapter(ItemData.class, new ItemDeserializer());
            gsonBuilder.registerTypeAdapter(StateDescriptionData.class, new ItemStateDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
