package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.HpUserModel;

public interface ContactListInterface {

    boolean onContactSelected(HpUserModel contact);

    void onContactDeselected(HpUserModel contact);
}
