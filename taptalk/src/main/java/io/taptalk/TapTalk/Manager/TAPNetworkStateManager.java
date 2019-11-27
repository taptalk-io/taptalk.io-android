package io.taptalk.TapTalk.Manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.NOT_CONNECTED;

public class TAPNetworkStateManager {
    private static final String TAG = TAPNetworkStateManager.class.getSimpleName();
    private static TAPNetworkStateManager instance;
    private List<TapTalkNetworkInterface> listeners;

    private TapNetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    //private TapNetworkBroadcastReceiver networkBroadcastReceiver;

    public static TAPNetworkStateManager getInstance() {
        return instance == null ? (instance = new TAPNetworkStateManager()) : instance;
    }

    public TAPNetworkStateManager() {
        listeners = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use ConnectivityManager.NetworkCallback for API 21 and above
            networkCallback = new TapNetworkCallback();
            networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();
        } else {
            // Use BroadcastReceiver for below API 21
            //networkBroadcastReceiver = new TapNetworkBroadcastReceiver();
        }
    }

    public void registerCallback(Context context) {
        Log.e(TAG, "onAppGotoForeground: registerCallback");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != networkCallback) {
            getConnectivityManager(context).registerNetworkCallback(networkRequest, networkCallback);
        } else /*if (TAPConnectionManager.getInstance().getConnectionStatus() == NOT_CONNECTED)*/ {
            // TODO: 27 November 2019
           triggerConnectivityChange();
        }
    }

    public void unregisterCallback(Context context) {
        Log.e(TAG, "onAppGotoBackground: unregisterCallback");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != networkCallback) {
            getConnectivityManager(context).unregisterNetworkCallback(networkCallback);
        }
    }

    public boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (null != connectivityManager &&
                null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) &&
                null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) &&
                (NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() ||
                        NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState())) {
//            Log.e(TAG, "hasNetworkConnection: true");
            return true;
        } else {
//            Toast.makeText(context, "No Network Available.", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "hasNetworkConnection: false");
            return false;
        }
    }

    private ConnectivityManager getConnectivityManager(Context context) {
        return (null != context) ?
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) : null;
    }

    public void addNetworkListener(TapTalkNetworkInterface listener) {
        listeners.add(listener);
    }

    public void removeNetworkListener(TapTalkNetworkInterface listener) {
        listeners.remove(listener);
    }

    public void removeNetworkListenerAt(int index) {
        listeners.remove(index);
    }

    public void clearNetworkListener() {
        listeners.clear();
    }

    private void triggerConnectivityChange() {
        if (TAPNetworkStateManager.getInstance().hasNetworkConnection(TapTalk.appContext)) {
            Log.e(TAG, "onReceive: hasNetworkConnection");
            TAPNetworkStateManager.getInstance().onNetworkAvailable();
        } else {
            Log.e(TAG, "onReceive: No Network Connection");
            TAPNetworkStateManager.getInstance().onNetworkLost();
        }
    }

    private void onNetworkAvailable() {
        List<TapTalkNetworkInterface> listenersCopy = new ArrayList<>(listeners);
        if (!listenersCopy.isEmpty()) {
            for (TapTalkNetworkInterface listener : listenersCopy) {
                listener.onNetworkAvailable();
            }
        }
    }

    private void onNetworkLost() {
        TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
        TAPDataManager.getInstance().setNeedToQueryUpdateRoomList(true);
        TAPConnectionManager.getInstance().close();
    }

    public class TapNetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e(TAG, "onAvailable: " );
            onNetworkAvailable();
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e(TAG, "onLost: " );
            onNetworkLost();
        }
    }

    public static class TapNetworkBroadcastReceiver extends BroadcastReceiver {

        public TapNetworkBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: " + intent.getAction());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return;
            }
            TAPNetworkStateManager.getInstance().triggerConnectivityChange();
        }
    }
}
