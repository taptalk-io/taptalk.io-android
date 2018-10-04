package com.moselo.HomingPigeon.Interface;

import com.moselo.HomingPigeon.Model.UserModel;

public interface ContactListInterface {

    boolean onContactSelected(UserModel contact, boolean isSelected);

    void onContactRemoved(UserModel contact);
}
