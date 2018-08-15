package com.moselo.HomingPigeon.Listener;

public interface HomingPigeonSocketListener {
   void onConnect();

   void onDisconnect();

   void onReconnect();

   void onNewMessage(String message);
}
