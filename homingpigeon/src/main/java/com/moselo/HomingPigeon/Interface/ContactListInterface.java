package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.HpUserModel;

public interface ContactListInterface {

    boolean onContactSelected(HpUserModel contact, boolean isSelected);

    void onContactRemoved(HpUserModel contact);
}
