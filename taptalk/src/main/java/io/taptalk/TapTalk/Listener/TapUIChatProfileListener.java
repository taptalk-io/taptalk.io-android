package io.taptalk.TapTalk.Listener;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

import android.app.Activity;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.HashMap;

import io.taptalk.TapTalk.Interface.TapUIChatProfileInterface;
import io.taptalk.TapTalk.Interface.TapUIChatRoomInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPProductModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPChatProfileActivity;

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
}
