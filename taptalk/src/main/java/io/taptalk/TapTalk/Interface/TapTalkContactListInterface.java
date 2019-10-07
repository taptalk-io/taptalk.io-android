package io.taptalk.TapTalk.Interface;

import io.taptalk.TapTalk.Model.TAPUserModel;

public interface TapTalkContactListInterface {

    boolean onContactSelected(TAPUserModel contact);

    void onContactDeselected(TAPUserModel contact);

    void onMenuButtonTapped(int actionId);

    void onInfoLabelButtonTapped(int actionId);
}
