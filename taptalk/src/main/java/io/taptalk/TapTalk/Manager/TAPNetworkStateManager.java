package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

public class TAPNetworkStateManager extends ConnectivityManager.NetworkCallback {
    private static final String TAG = TAPNetworkStateManager.class.getSimpleName();
    private static TAPNetworkStateManager instance;
    private List<TapTalkNetworkInterface> listeners;
    private NetworkRequest networkRequest;

    public static TAPNetworkStateManager getInstance() {
        return instance == null ? (instance = new TAPNetworkStateManager()) : instance;
    }

    public TAPNetworkStateManager() {
        listeners = new ArrayList<>();
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void registerCallback(Context context) {
        getConnectivityManager(context).registerNetworkCallback(networkRequest, this);
    }

    public void unregisterCallback(Context context) {
        getConnectivityManager(context).unregisterNetworkCallback(this);
    }

    public boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (null != connectivityManager &&
                null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) &&
                null != connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) &&
                (NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() ||
                NetworkInfo.State.CONNECTED == connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState())) {
            return true;
        } else {
//            Toast.makeText(context, "No Network Available.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.e(TAG, "onAvailable: " );
        List<TapTalkNetworkInterface> listenersCopy = new ArrayList<>(listeners);
        if (!listenersCopy.isEmpty()) {
            for (TapTalkNetworkInterface listener : listenersCopy) {
                listener.onNetworkAvailable();
            }
        }
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.e(TAG, "onLost: " );
        TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
        TAPDataManager.getInstance().setNeedToQueryUpdateRoomList(true);
        TAPConnectionManager.getInstance().close();
    }
}
