package io.taptalk.TapTalk.Listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Keep;

import java.lang.ref.WeakReference;

import io.taptalk.TapTalk.Interface.TapUIRoomListInterface;
import io.taptalk.TapTalk.View.Activity.TAPMyAccountActivity;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Fragment.TapUIMainRoomListFragment;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.EDIT_PROFILE;

@Keep
public abstract class TapUIRoomListListener implements TapUIRoomListInterface {
    @Override
    public void onSearchChatBarTapped(Activity activity, TapUIMainRoomListFragment mainRoomListFragment) {
        if (null != mainRoomListFragment) {
            mainRoomListFragment.showSearchChat();
        }
    }

    @Override
    public void onTapTalkAccountButtonTapped(Activity activity) {
        WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
        Intent intent = new Intent(contextWeakReference.get(), TAPMyAccountActivity.class);
        contextWeakReference.get().startActivityForResult(intent, EDIT_PROFILE);
        contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }

    @Override
    public void onNewChatButtonTapped(Activity activity) {
        WeakReference<Activity> contextWeakReference = new WeakReference<>(activity);
        Intent intent = new Intent(contextWeakReference.get(), TAPNewChatActivity.class);
        contextWeakReference.get().startActivity(intent);
        contextWeakReference.get().overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
    }
}
