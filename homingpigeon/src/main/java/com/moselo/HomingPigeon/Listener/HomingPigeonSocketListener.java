package com.moselo.HomingPigeon.Listener;

public interface HomingPigeonSocketListener {

   void onNewMessage(String eventName, String emitData);

   void onSocketConnected();
}
