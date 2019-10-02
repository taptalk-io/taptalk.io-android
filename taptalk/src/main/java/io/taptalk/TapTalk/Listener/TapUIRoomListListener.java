package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.content.Intent;

import io.taptalk.TapTalk.Interface.TapUIRoomListInterface;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.EDIT_PROFILE;

public abstract class TapUIRoomListListener implements TapUIRoomListInterface {
    @Override
    public void onTapTalkAccountButtonTapped(Activity activity) {
        Intent intent = new Intent(activity, TAPMyAccountActivity.class);
        activity.startActivityForResult(intent, EDIT_PROFILE);
        activity.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }
}
