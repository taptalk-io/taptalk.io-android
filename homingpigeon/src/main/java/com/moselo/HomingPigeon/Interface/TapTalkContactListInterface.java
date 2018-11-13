package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.HpUserModel;

public interface TapTalkContactListInterface {

    boolean onContactSelected(HpUserModel contact);

    void onContactDeselected(HpUserModel contact);
}
