package io.taptalk.TapTalk.Listener;

import android.support.annotation.Keep;

import io.taptalk.TapTalk.Interface.TapTalkContactListInterface;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;

@Keep
public abstract class TapContactListListener implements TapTalkContactListInterface {
    @Override
    public boolean onContactSelected(TapContactListModel contact) {
        return contact.isSelected();
    }

    @Override
    public void onContactDeselected(TapContactListModel contact) {

    }

    @Override
    public void onMenuButtonTapped(int actionId) {

    }

    @Override
    public void onInfoLabelButtonTapped(int actionId) {

    }
}
