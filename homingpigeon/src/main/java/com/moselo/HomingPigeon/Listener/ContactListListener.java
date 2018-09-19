package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.UserModel;

public interface ContactListListener {

    boolean onContactSelected(UserModel contact, boolean isSelected);

    void onContactRemoved(UserModel contact);
}
