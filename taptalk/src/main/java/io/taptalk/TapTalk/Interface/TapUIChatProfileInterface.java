package io.taptalk.TapTalk.Interface;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.HashMap;

import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapUIChatProfileInterface {
    void onReportUserButtonTapped(Activity activity, TAPRoomModel room, TAPUserModel reportedUser);

    void onReportGroupButtonTapped(Activity activity, TAPRoomModel room);

    void onGroupInCommonItemTapped(Activity activity, TAPRoomModel room);
}
