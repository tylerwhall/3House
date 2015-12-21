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

import se.treehou.openhabconnector.connector.openhab.request.SitemapData;

public class SitemapListDeserializer implements JsonDeserializer<List<SitemapData>> {

    private static final String TAG = "SitemapListDeserializer";

    @Override
    public List<SitemapData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        Log.d(TAG, "Deserializing: " + json);

        List<SitemapData> sitemaps = new ArrayList<>();
        if(json.isJsonObject()) {
            Log.d(TAG, "Deserializing single sitemap");
            JsonObject jSitemaps = json.getAsJsonObject();
            if(jSitemaps.has("sitemap")) {
                if(jSitemaps.get("sitemap").isJsonObject()) {
                    JsonElement jSitemap = jSitemaps.get("sitemap");
                    sitemaps.add(context.<SitemapData>deserialize(jSitemap, SitemapData.class));
                    return sitemaps;
                }
                else {
                    JsonArray jSitemap = jSitemaps.get("sitemap").getAsJsonArray();
                    return context.deserialize(jSitemap, new TypeToken<List<SitemapData>>() {}.getType());
                }
            }
            else {
                SitemapData sitemap = context.deserialize(json, SitemapData.class);
                sitemaps.add(sitemap);
            }
        }else if(json.isJsonArray()){
            Log.d(TAG, "Deserializing multiple sitemap");
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                SitemapData sitemap = context.deserialize(e, SitemapData.class);
                sitemaps.add(sitemap);
            }
        }

        return sitemaps;
    }
}
