package com.moselo.TapTalk.Interface;

import com.moselo.HomingPigeon.Model.TAPUserModel;

public interface TapTalkContactListInterface {

    boolean onContactSelected(TAPUserModel contact);

    void onContactDeselected(TAPUserModel contact);
}
