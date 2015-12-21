package se.treehou.openhabconnector.connector.openhab;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import se.treehou.openhabconnector.connector.models.Binding;
import se.treehou.openhabconnector.connector.openhab.core.LinkedPage;
import se.treehou.openhabconnector.connector.openhab.request.ItemData;
import se.treehou.openhabconnector.connector.openhab.request.SitemapData;

public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps")
    void listSitemaps(retrofit.Callback<List<SitemapData>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps/{id}")
    void getSitemap(@Path("id") String id, retrofit.Callback<SitemapData> callback);

    @Headers("Accept: application/json")
    @GET("/")
    void getPage(retrofit.Callback<LinkedPage> callback);

    @Headers("Accept: application/json")
    @GET("/rest/bindings")
    void getBindings(retrofit.Callback<List<Binding>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/items/")
    void getItems(retrofit.Callback<List<ItemData>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/items/{id}")
    void getItem(@Path("id") String id, retrofit.Callback<ItemData> callback);

    @Headers({
        "Accept: application/text",
        "Content-Type: text/plain"
    })
    @POST("/rest/items/{id}")
    void sendCommand(@Body String command, @Path("id") String id, retrofit.Callback<Response> callback);

    @Headers({
            "Accept: application/text",
            "Content-Type: text/plain"
    })
    @POST("/rest/items/{id}")
    Response sendCommand(@Body String command, @Path("id") String id);
}
