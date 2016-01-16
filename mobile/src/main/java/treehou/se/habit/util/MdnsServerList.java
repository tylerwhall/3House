package treehou.se.habit.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import treehou.se.habit.core.db.ServerDB;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MdnsServerList {

    private static final String TAG = "MdnsServerList";

    private static final String SERVICE_TYPE = "_openhab-server._tcp.";
    private static final String SERVICE_TYPE_SSL = "_openhab-server-ssl._tcp.";

    private NsdManager mNsdManager;
    private MdnsDiscoveryListener httpListener;
    private MdnsDiscoveryListener httpsListener;
    private NsdManager.DiscoveryListener mdiscoveryListener;

    private MdnsServerListener serverListener;

    private ArrayList<ServerDB> servers = new ArrayList<ServerDB>();
    private LinkedList<NsdServiceInfo> unresolvedServers = new LinkedList<NsdServiceInfo>();

    public MdnsServerList(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        httpListener = new MdnsDiscoveryListener();
        httpsListener = new MdnsDiscoveryListener();
    }

    public void startDiscovery(MdnsServerListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;

        serverListener = listener;
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, httpListener);
        mNsdManager.discoverServices(SERVICE_TYPE_SSL, NsdManager.PROTOCOL_DNS_SD, httpsListener);
    }

    public void stopDiscovery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            return;

        mNsdManager.stopServiceDiscovery(httpListener);
        mNsdManager.stopServiceDiscovery(httpsListener);
    }

    public final List<ServerDB> getDiscoveredServers() {
        return Collections.unmodifiableList(servers);
    }

    private class MdnsDiscoveryListener implements NsdManager.DiscoveryListener {
        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "discovery failed. Error " + errorCode);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
            Log.d(TAG, "discovery started");
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.d(TAG, "discovery stopped");
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.d(TAG, "service found " + serviceInfo.toString());
            unresolvedServers.add(serviceInfo);
            tryResolve();

        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {

        }
    }

    private void resolveService(NsdServiceInfo serviceInfo) {
        mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed. " + errorCode + " " + serviceInfo);

                /*
                 * The framework seems to return FAILURE_ALREADY_ACTIVE when
                 * trying to resolve the http and https services for a server
                 * simultaneously. Put these failures in a list to retry when
                 * the current request completes.
                 */
                if (errorCode == NsdManager.FAILURE_ALREADY_ACTIVE &&
                        !unresolvedServers.contains(serviceInfo))
                    unresolvedServers.add(serviceInfo);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {

                String proto = "http://";
                String suffix = "";
                if (serviceInfo.getServiceType().contains("ssl")) {
                    proto = "https://";
                    suffix = " (https)";
                }
                String url = proto +
                        serviceInfo.getHost().getHostName() +
                        ":" +
                        serviceInfo.getPort();
                ServerDB server = new ServerDB();
                server.setName(serviceInfo.getHost().getCanonicalHostName() + suffix);
                server.setLocalUrl(url);
                server.setAutodiscovered(true);
                Log.d(TAG, "Resolve succeeded. " + server.getName() + " " + server.getLocalUrl());

                if (!servers.contains(server)) {
                    servers.add(server);
                    serverListener.serverFound(server);
                }

                unresolvedServers.remove(serviceInfo);
                tryResolve();
            }
        });
    }

    private void tryResolve() {
        try {
            NsdServiceInfo serviceInfo = unresolvedServers.remove();
            resolveService(serviceInfo);
        } catch (NoSuchElementException e) {
            // Empty list
        }

    }

    public interface MdnsServerListener {
        public void serverFound(ServerDB serverDb);
    }
}