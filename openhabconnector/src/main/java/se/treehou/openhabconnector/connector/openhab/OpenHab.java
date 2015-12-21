package se.treehou.openhabconnector.connector.openhab;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Realm;

import org.atmosphere.wasync.Client;
import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.OptionsBuilder;
import org.atmosphere.wasync.RequestBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.treehou.openhabconnector.connector.BasicAuthServiceGenerator;
import se.treehou.openhabconnector.connector.openhab.core.Sitemap;
import se.treehou.openhabconnector.connector.openhab.request.ServerData;
import se.treehou.openhabconnector.connector.openhab.request.SitemapData;

public class OpenHab {

    private static final String TAG = OpenHab.class.getSimpleName();

    private static Context context;

    private OpenHabService ohService;

    private void setAndroidContext(Context context){
        OpenHab.context = context;
    }

    public OpenHab(ServerData server) {
        ohService = generateOpenHabService(server);
    }

    public static OpenHabService generateOpenHabService(ServerData server){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, server.getUrl(), server.getUsername(), server.getPassword());
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
    }

    // TODO extract to separate class
    private AsyncTask<Void, Void, Void> createLongPoller() {
        AsyncTask<Void, Void, Void> longPoller = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {


                Realm realm = null;
                if(server.requiresAuth()){
                    realm = new Realm.RealmBuilder()
                            .setPrincipal(server.getUsername())
                            .setPassword(server.getPassword())
                            .setUsePreemptiveAuth(true)
                            .setScheme(Realm.AuthScheme.BASIC)
                            .build();
                }

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient(
                        new AsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true)
                                .setHostnameVerifier(new TrustModifier.NullHostNameVerifier())
                                .setRealm(realm)
                                .build()
                );

                Client client = ClientFactory.getDefault().newClient();
                OptionsBuilder optBuilder = client.newOptionsBuilder().runtime(asyncHttpClient);

                UUID atmosphereId = UUID.randomUUID();

                RequestBuilder request = client.newRequestBuilder()
                        .method(org.atmosphere.wasync.Request.METHOD.GET)
                        .uri(page.getLink())
                        .header("Accept", "application/json")
                        .header("Accept-Charset", "utf-8")
                        .header("X-Atmosphere-Transport", "long-polling")
                        .header("X-Atmosphere-tracking-id", atmosphereId.toString())
                        .encoder(new Encoder<String, Reader>() {        // Stream the request body
                            @Override
                            public Reader encode(String s) {
                                Log.d(TAG, "RequestBuilder encode");
                                return new StringReader(s);
                            }
                        })
                        .decoder(new Decoder<String, LinkedPage>() {
                            @Override
                            public LinkedPage decode(Event e, String s) {
                                Gson gson = GsonHelper.createGsonBuilder();
                                return gson.fromJson(s, LinkedPage.class);
                            }
                        })
                        .transport(org.atmosphere.wasync.Request.TRANSPORT.LONG_POLLING);                    // Fallback to Long-Polling

                if (server.requiresAuth()){
                    request.header(Constants.HEADER_AUTHENTICATION, ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                }

                pollSocket = client.create(optBuilder.build());
                try {
                    Log.d(TAG, "Socket " + pollSocket + " " + request.uri());
                    pollSocket.on(new Function<LinkedPage>() {
                        @Override
                        public void on(LinkedPage page) {
                            Log.d(TAG, "Socket received");
                            updatePage(page);
                        }
                    })
                            .open(request.build());
                } catch (IOException | ExceptionInInitializerError e) {
                    Log.d(TAG, "Got error " + e);
                }

                Log.d(TAG,"Poller started");

                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                if(pollSocket != null) {
                    pollSocket.close();
                }
            }
        };
        return longPoller;
    }

    public void addSitemapListener(final OHListener<List<Sitemap>> sitemapListener){

        ohService.listSitemaps(new Callback<List<SitemapData>>() {
            @Override
            public void success(List<SitemapData> sitemapDatas, Response response) {
                List<Sitemap> sitemaps = new ArrayList<>();

                for (SitemapData data : sitemapDatas) {
                    sitemaps.add(new Sitemap(data));
                }
                sitemapListener.onChange(sitemaps);
            }

            @Override
            public void failure(RetrofitError error) {
                sitemapListener.onCancel();
            }
        });
        ohService.
    }

    interface OHListener<A> {
        void onChange(A data);
        void onCancel();
    }
}
