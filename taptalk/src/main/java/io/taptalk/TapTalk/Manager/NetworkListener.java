package io.taptalk.TapTalk.Manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

public class NetworkListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (TAPNetworkStateManagerNonLol.getInstance().hasNetworkConnection(TapTalk.appContext)) {
                List<TapTalkNetworkInterface> listenersCopy = new ArrayList<>(TAPNetworkStateManagerNonLol.getInstance().getListeners());
                if (!listenersCopy.isEmpty()) {
                    for (TapTalkNetworkInterface listener : listenersCopy) {
                        listener.onNetworkAvailable();
                    }
                }
            } else {
                TAPRoomListViewModel.setShouldNotLoadFromAPI(false);
                TAPDataManager.getInstance().setNeedToQueryUpdateRoomList(true);
                TAPConnectionManager.getInstance().close();
            }
        }
    }
}
