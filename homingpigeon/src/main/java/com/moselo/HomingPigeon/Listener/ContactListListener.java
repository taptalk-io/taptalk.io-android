package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.UserModel;

public interface ContactListListener {

    void onContactSelected(UserModel contact, boolean isSelected);

    void onContactRemoved(UserModel contact);
}
