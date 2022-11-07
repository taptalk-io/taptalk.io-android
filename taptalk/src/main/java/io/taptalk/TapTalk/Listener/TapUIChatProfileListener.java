package io.taptalk.TapTalk.Listener;

import android.app.Activity;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapUIChatProfileInterface;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TapUIChatProfileListener implements TapUIChatProfileInterface {

    public TapUIChatProfileListener() {
    }

    @Override
    public void onReportUserButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel reportedUser) {

    }

    @Override
    public void onReportGroupButtonTapped(Activity activity, TAPRoomModel room) {

    }

    @Override
    public void onGroupInCommonItemTapped(Activity activity, TAPRoomModel room) {

    }
}
