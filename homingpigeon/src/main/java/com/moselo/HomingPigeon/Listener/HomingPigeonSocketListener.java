package com.moselo.HomingPigeon.Listener;

import com.moselo.HomingPigeon.Model.MessageModel;

import org.json.JSONObject;

public interface HomingPigeonSocketListener {
   void onNewMessage(String eventName, String emitData);
}
