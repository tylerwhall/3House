package se.treehou.openhabconnector.connector.openhab;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import se.treehou.openhabconnector.connector.BasicAuthServiceGenerator;
import se.treehou.openhabconnector.connector.ConnectorUtil;
import se.treehou.openhabconnector.connector.Constants;
import se.treehou.openhabconnector.connector.MyOpenHabService;
import se.treehou.openhabconnector.connector.TrustModifier;
import se.treehou.openhabconnector.connector.models.Binding;
import se.treehou.openhabconnector.connector.openhab.core.LinkedPage;
import se.treehou.openhabconnector.connector.openhab.request.ItemData;
import se.treehou.openhabconnector.connector.openhab.request.ServerData;
import se.treehou.openhabconnector.connector.openhab.request.SitemapData;

public class Communicator {

    private static final String TAG = "Communicator";

    private static final String MY_OPENHAB_URL = "https://my.openhab.org";

    private static Communicator mInstance;
    private Context context;

    private Map<ServerData, Picasso> requestLoaders = new HashMap<>();

    /**
     * Grab single instance of connector.
     *
     * @param context calling context.
     * @return instance of connector.
     */
    public static synchronized Communicator instance(Context context){
        if (mInstance == null) {
            mInstance = new Communicator(context);
        }
        return mInstance;
    }

    private Communicator(Context context){
        this.context = context;
    }


    public static OpenHabService generateOpenHabService(ServerData server){
        return generateOpenHabService(server, server.getUrl());
    }

    public static OpenHabService generateOpenHabService(ServerData server, String url){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, url, server.getUsername(), server.getPassword());
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create Openhab2Service ", e);
        }
        return null;
    }

    /**
     * Send command to openhab server
     *
     * @param server server to communicate with.
     * @param item the item to send the command to.
     * @param command the command to send.
     */
    public void command(ServerData server, ItemData item, final String command){
        command(server, item.getName(), command);
    }

    /**
     * Send command to openhab server
     *
     * @param server server to communicate with.
     * @param item the item to send the command to.
     * @param command the command to send.
     */
    public void command(final ServerData server, final String item, final String command){

        final OpenHabService service = generateOpenHabService(server, server.getUrl());
        if(service == null){
            return;
        }
        service.sendCommand(command, item, new retrofit.Callback<Response>() {

            @Override
            public void success(Response body, Response response) {
                Log.d(TAG, "Sent command " + command);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error: " + error.getCause() + " " + error.getUrl());
            }
        });
    }

    private int scrubNumberValue(int number, final int min, final int max){
        return Math.max(Math.min(number, max), min);
    }

    public void incDec(final ServerData server, final ItemData item, final int value, final int min, final int max){
        requestItem(server, item.getName(), new ItemRequestListener() {
            @Override
            public void onSuccess(ItemData newItem) {
                Log.d(TAG, "ItemData state " + newItem.getState() + " " + newItem.getType());
                String state = newItem.getState();
                if (Constants.SUPPORT_INC_DEC.contains(newItem.getType())) {
                    if (Constants.COMMAND_OFF.equals(state) || Constants.COMMAND_UNINITIALIZED.equals(state)) {
                        if (value > 0) {
                            command(server, newItem, String.valueOf(scrubNumberValue(min + value, min, max)));
                        }
                    } else if (Constants.COMMAND_ON.equals(state)) {
                        if (value < 0) {
                            command(server, newItem, String.valueOf(scrubNumberValue(max + value, min, max)));
                        }
                    } else {
                        try {
                            int itemVal = scrubNumberValue(Integer.parseInt(newItem.getState()) + value, min, max);
                            Log.e(TAG, "Sending command " + itemVal + " value " + value);
                            command(server, newItem, String.valueOf(itemVal));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Could not parse state " + newItem.getState(), e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "incDec " + message);
            }
        });
    }

    public Picasso buildPicasso(Context context, final ServerData server){

        if(requestLoaders.containsKey(server)){
            return requestLoaders.get(server);
        }

        OkHttpClient httpClient = TrustModifier.createAcceptAllClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {

                com.squareup.okhttp.Request.Builder newRequest = chain.request().newBuilder();
                if (server.requiresAuth()) {
                    newRequest.header(Constants.HEADER_AUTHENTICATION, ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                }

                return chain.proceed(newRequest.build());
            }
        });

        final Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(httpClient))
                .memoryCache(new LruCache(context))
                .build();

        requestLoaders.put(server, picasso);

        return picasso;
    }

    /**
     * Load image and put result in image view
     *
     * @param server the server credentials.
     * @param imageUrl the url of image.
     * @param imageView the view to put bitmap in.
     * @param useCache set if cache should be used.
     */
    public void loadImage(final ServerData server, final URL imageUrl, final ImageView imageView, boolean useCache){

        Log.d(TAG, "onBitmapLoaded image start " + imageUrl.toString());
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onBitmapLoaded image load success");
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                imageView.setVisibility(View.GONE);
                Log.d(TAG, "onBitmapLoaded image load failed " + imageUrl);
            }
        };

        Picasso picasso = buildPicasso(context, server);
        picasso.load(imageUrl.toString())
            .noFade()
            .placeholder(imageView.getDrawable())
            .into(imageView, callback); // problem when saving null image
    }

    /**
     * Load image and put result in image view
     *
     * @param server the server credentials.
     * @param imageUrl the url of image.
     * @param imageView the view to put bitmap in.
     */
    public void loadImage(final ServerData server, final URL imageUrl, final ImageView imageView){
        loadImage(server, imageUrl, imageView, true);
    }

    /**
     * Get sitemaps from server.
     *
     * @param server server to request sitemaps from.
     * @param listener server callback listener.
     */
    public void requestSitemaps(final ServerData server, final SitemapsRequestListener listener){

        OpenHabService service = generateOpenHabService(server, server.getUrl());
        if(service == null){
            return;
        }

        service.listSitemaps(new retrofit.Callback<List<SitemapData>>() {
            @Override
            public void success(List<SitemapData> sitemaps, Response response) {
                listener.onSuccess(sitemaps);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    /**
     * Request page for from server
     *
     * @param server the credentials of server.
     * @param page the page to fetch.
     * @param responseListener response listener.
     */
    public void requestPage(ServerData server, LinkedPage page, final retrofit.Callback<LinkedPage> responseListener) {
        OpenHabService service = generateOpenHabService(server, page.getLink());
        if(service == null){
            return;
        }

        service.getPage(new retrofit.Callback<LinkedPage>() {
            @Override
            public void success(LinkedPage linkedPage, Response response) {
                Log.d(TAG, "Received page " + response.getUrl());
                responseListener.success(linkedPage, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Received page error " + error.getUrl(), error);
                responseListener.failure(error);
            }
        });
    }

    /**
     * Request items from server.
     *
     * @param server the server to request items from.
     * @param listener callback listener.
     */
    public void requestItems(final ServerData server, final ItemsRequestListener listener){

        OpenHabService service = generateOpenHabService(server, server.getUrl());
        if(service == null){
            return;
        }
        service.getItems(new retrofit.Callback<List<ItemData>>() {
            @Override
            public void success(List<ItemData> items, Response response) {
                for(ItemData item : items){
                    item.setServer(server);
                }
                listener.onSuccess(items);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void requestItem(final ServerData server, final String item, final ItemRequestListener listener){

        OpenHabService service = generateOpenHabService(server, server.getUrl());
        if(service == null){
            return;
        }
        service.getItem(item, new retrofit.Callback<ItemData>() {
            @Override
            public void success(ItemData itemHolder, Response response) {
                listener.onSuccess(itemHolder);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void requestSitemap(final SitemapData sitemap, final SitemapRequestListener listener){

        final ServerData server = sitemap.getServer();

        Uri uri = Uri.parse(sitemap.getLink())
                .buildUpon()
                .path(null)
                .build();

        OpenHabService service = generateOpenHabService(server, uri.toString());
        if(service == null){
            return;
        }
        service.getSitemap(sitemap.getName(), new retrofit.Callback<SitemapData>() {
            @Override
            public void success(SitemapData sitemap, Response response) {
                sitemap.setServer(server);
                listener.onSuccess(sitemap);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void listBindings(final ServerData server, final  retrofit.Callback<List<Binding>> callback){

        OpenHabService service = generateOpenHabService(server, server.getUrl());
        if(service == null) {
            return;
        }
        service.getBindings(new retrofit.Callback<List<Binding>>() {
            @Override
            public void success(List<Binding> bindings, Response response) {
                callback.success(bindings, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public interface ItemRequestListener{
        void onSuccess(ItemData item);
        void onFailure(String message);
    }

    public interface ItemsRequestListener{
        void onSuccess(List<ItemData> items);
        void onFailure(String message);
    }

    public interface SitemapRequestListener{
        void onSuccess(SitemapData sitemap);
        void onFailure(String message);
    }

    public interface SitemapsRequestListener{
        void onSuccess(List<SitemapData> sitemaps);
        void onFailure(String message);
    }

    public static class DummySitemapsRequestListener implements SitemapsRequestListener {
        public void onSuccess(List<SitemapData> sitemaps){}
        public void onFailure(String message){}
    }
}
