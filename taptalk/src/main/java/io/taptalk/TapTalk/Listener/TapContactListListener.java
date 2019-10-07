package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkContactListInterface;
import io.taptalk.TapTalk.Model.TAPUserModel;

@Keep
public abstract class TapContactListListener implements TapTalkContactListInterface {
    @Override
    public boolean onContactSelected(TAPUserModel contact) {
        return false;
    }

    @Override
    public void onContactDeselected(TAPUserModel contact) {

    }

    @Override
    public void onMenuButtonTapped(int actionId) {

    }

    @Override
    public void onInfoLabelButtonTapped(int actionId) {

    }
}
