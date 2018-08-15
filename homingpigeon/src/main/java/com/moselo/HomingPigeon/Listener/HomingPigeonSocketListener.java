package com.moselo.HomingPigeon.Listener;

import org.json.JSONObject;

public interface HomingPigeonSocketListener {
   void onNewMessage(String eventName, JSONObject emitData);
}
