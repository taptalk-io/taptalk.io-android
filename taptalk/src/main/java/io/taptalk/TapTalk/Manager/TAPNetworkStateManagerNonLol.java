package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;

public class TAPNetworkStateManagerNonLol {
    private static final String TAG = TAPNetworkStateManagerNonLol.class.getSimpleName();
    private static TAPNetworkStateManagerNonLol instance;
    private List<TapTalkNetworkInterface> listeners;
    private NetworkRequest networkRequest;

    public static TAPNetworkStateManagerNonLol getInstance() {
        return instance == null ? (instance = new TAPNetworkStateManagerNonLol()) : instance;
    }

    public TAPNetworkStateManagerNonLol() {
        listeners = new ArrayList<>();
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
            return false;
        }
    }

    public List<TapTalkNetworkInterface> getListeners() {
        return listeners;
    }

    private ConnectivityManager getConnectivityManager(Context context) {
        return (null != context) ?
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) : null;
    }

    public void addNetworkListener(TapTalkNetworkInterface listener) {
        listeners.add(listener);
        Log.e("]]]]]", "Network Listener added   : " + listeners.size());
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

}
