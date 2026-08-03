package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

public class TAPNetworkStateManager {
    private static final String TAG = TAPNetworkStateManager.class.getSimpleName();
    private static HashMap<String, TAPNetworkStateManager> instances;

    private String instanceKey = "";
    private final List<TapTalkNetworkInterface> listeners;

    private final TapNetworkCallback networkCallback;
    private final NetworkRequest networkRequest;

    public static TAPNetworkStateManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TAPNetworkStateManager instance = new TAPNetworkStateManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TAPNetworkStateManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public TAPNetworkStateManager(String instanceKey) {
        this.instanceKey = instanceKey;
        listeners = new ArrayList<>();

        networkCallback = new TapNetworkCallback();
        networkRequest = new NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build();
    }

    public void registerCallback(Context context) {
        if (null != networkCallback) {
            try {
                getConnectivityManager(context).registerNetworkCallback(networkRequest, networkCallback);
            }
            catch (IllegalArgumentException e) {
                // FIXME: 31 Mar 2020
            }
        }
        else {
            // Broadcast receiver will not receive callback right away, trigger connectivity change manually to update connection status
            triggerConnectivityChange();
        }
    }

    public void unregisterCallback(Context context) {
        if (null != networkCallback) {
            try {
                getConnectivityManager(context).unregisterNetworkCallback(networkCallback);
            }
            catch (IllegalArgumentException e) {
                // FIXME: 31 Mar 2020 java.lang.IllegalArgumentException:
                //  NetworkCallback was already unregistered when called more than once
            }
        }
    }

    public boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (null != connectivityManager &&
            null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) &&
            null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) &&
            (NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() ||
            NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState())
        ) {
            return true;
        }
        else {
            return false;
        }
    }

    private ConnectivityManager getConnectivityManager(Context context) {
        return (null != context) ?
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) : null;
    }

    public synchronized void addNetworkListener(TapTalkNetworkInterface listener) {
        listeners.remove(listener);
        listeners.add(listener);
    }

    public synchronized void removeNetworkListener(TapTalkNetworkInterface listener) {
        listeners.remove(listener);
    }

    public synchronized void removeNetworkListenerAt(int index) {
        listeners.remove(index);
    }

    public synchronized void clearNetworkListener() {
        listeners.clear();
    }

    private void triggerConnectivityChange() {
        if (TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
            TAPNetworkStateManager.getInstance(instanceKey).onNetworkAvailable();
        }
        else {
            TAPNetworkStateManager.getInstance(instanceKey).onNetworkLost();
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
        TAPRoomListViewModel.setShouldNotLoadFromAPI(instanceKey,false);
        TAPDataManager.getInstance(instanceKey).setNeedToQueryUpdateRoomList(true);
        TAPConnectionManager.getInstance(instanceKey).close();
    }

    public class TapNetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            onNetworkAvailable();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            onNetworkLost();
        }
    }
}
